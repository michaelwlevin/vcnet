package vcnet.meta;

import graphicutils.GraphicUtils;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import vcnet.mech.RuleList;
import vcnet.gui.ScoreDisplay;
import graphicutils.*;
import jar_input.JarIOWindow;
import vcnet.mech.Score;
import vcnet.net.ConnectMessage;
import vcnet.net.NetMessage;
import vcnet.net.ScoresMessage;
import vcnet.net.TextMessage;

public class MetaServer implements Runnable
{
	public static void main(String[] args)
	{




		final JTextField port=new JTextField(6);



		final JButton create=new JButton("Create server");

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());

		JPanel p1=new JPanel();
		p1.setLayout(new GridBagLayout());

		final JTextField numRounds=new JTextField(6);
		final JTextField numPlayers=new JTextField(6);

		final JTextField[] points=new JTextField[4];

		for(byte x=0; x<4; x++)
		{
			points[x]=new JTextField(3);

			points[x].setText(""+(4-x));
		}


		final JCheckBox[] boxes=new JCheckBox[4];

		boxes[0]=new JCheckBox("Winner starts");
		boxes[1]=new JCheckBox("Play after pass");
		boxes[2]=new JCheckBox("Ignore locks");
		boxes[3]=new JCheckBox("Breaks affect only 2s");

		try
		{
			Scanner file=new Scanner(new File("Port.dat"));
			port.setText(file.nextLine().trim());
			for(byte x=0; x<boxes.length; x++)
			{
				boxes[x].setSelected(file.nextLine().trim().equals("1"));
			}

			numRounds.setText(file.nextLine().trim());
			numPlayers.setText(file.nextLine().trim());
			for(byte x=0; x<4; x++)
			{
				points[x].setText(file.nextLine().trim());
			}
		}
		catch(Exception e){}

		GraphicUtils.constrain(p1, new JLabel("Port: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p1, port, 1, 0, 1, 1);

		GraphicUtils.constrain(p1, new JLabel(""), 0, 1, 1, 1);
		byte loc=2;

		for(JCheckBox b: boxes)
		{
			GraphicUtils.constrain(p1, b, 0, loc++, 2, 1);
		}
		GraphicUtils.constrain(p1, new JLabel(""), 0, loc++, 1, 1);

		GraphicUtils.constrain(p, p1, 0, 0, 1, 1);


		p1=new JPanel();
		p1.setLayout(new GridBagLayout());

		GraphicUtils.constrain(p1, new JLabel("Num players: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p1, numPlayers, 1, 0, 1, 1);
		GraphicUtils.constrain(p1, new JLabel("Num rounds: "), 0, 1, 1, 1);
		GraphicUtils.constrain(p1, numRounds, 1, 1, 1, 1);
		GraphicUtils.constrain(p1, new JLabel(""), 0, 2, 1, 1);
		GraphicUtils.constrain(p1, new JLabel("Points gained for rank:"), 0, 3, 2, 1);
		for(byte x=0; x<4; x++)
		{
			GraphicUtils.constrain(p1, new JLabel(""+(x+1)), 0, x+4, 1, 1);
			GraphicUtils.constrain(p1, points[x], 1, x+4, 1, 1);
		}

		GraphicUtils.constrain(p, p1, 1, 0, 1, 1);
		GraphicUtils.constrain(p, create, 0, loc, 2, 1, GridBagConstraints.CENTER);

		final RuleList rules=new RuleList();

		final JFrame frame=new JFrame("Start server");

		ActionListener action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					rules.setWinnerStarts(boxes[0].isSelected());
					rules.setPlayAfterPass(boxes[1].isSelected());
					rules.setLocksUsed(!boxes[2].isSelected());
					rules.setUniversalBreaks(!boxes[3].isSelected());

					int[] point=new int[4];

					for(byte x=0; x<4; x++)
					{
						point[x]=Integer.parseInt(points[x].getText().trim());
					}

					frame.setVisible(false);
					frame.dispose();

					MetaServer server=new MetaServer(Integer.parseInt(port.getText().trim()), Integer.parseInt(numRounds.getText().trim()), point, Integer.parseInt(numPlayers.getText().trim()), rules);

					try
					{
						PrintWriter fileout=new PrintWriter(new File("Port.dat"));
						fileout.println(port.getText().trim());
						for(JCheckBox b:boxes)
						{
							fileout.println(b.isSelected()?"1":"0");
						}
						fileout.println(numRounds.getText().trim());
						fileout.println(numPlayers.getText().trim());

						for(JTextField x:points)
						{
							fileout.println(x.getText().trim());
						}
						fileout.close();
					}
					catch(IOException e2){}

				}
				catch(NumberFormatException e1)
				{

				}

			}
		};

		port.addActionListener(action);
		create.addActionListener(action);

		frame.add(p);
		frame.pack();
		frame.setResizable(false);

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(1);
			}
		});

		frame.setVisible(true);

		/*
		rules.setWinnerStarts(false);
		rules.setPlayAfterPass(true);
		rules.setLocksUsed(true);
		rules.setUniversalBreaks(false);
		*/
	}

	private int port;
	private int numRounds;
	private volatile int roundCnt;
	private volatile int serverCnt;

	private String ip;
	private int[] points;

	private HashMap<String, Long> scores;

	private ArrayList<MetaConnection> connections;

	private CountServer[] servers;

	private volatile int userLimit;

	private RuleList rules;

	private ScoreDisplay scoreDisplay;


	public MetaServer(int p, int n, int[] po, int limit, RuleList r)
	{
		numRounds=n;
		port=p;

		points=po;

		userLimit=limit;


		rules=r;

		scoreDisplay=new ScoreDisplay(userLimit);

		final JFrame frame=new JFrame("Current scores");
		JPanel pa=new JPanel();
		pa.add(new JScrollPane(scoreDisplay));
		frame.add(pa);
		frame.pack();
		frame.setResizable(false);

		/*
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowDeactivated(WindowEvent e)
			{
				frame.setVisible(true);
			}
		});
		*/
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		start();

		frame.setVisible(true);
	}

	public void start()
	{
		Thread t=new Thread(this, "Metaserver");

		t.start();
	}

	public void run()
	{


		try
		{

			new JarIOWindow("VCNET MetaServer");

			System.out.println("[SUBSERVER] Creating "+(userLimit/4)+" servers.");
			servers=new CountServer[userLimit/4];

			connections=new ArrayList<MetaConnection>();

			scores=new HashMap<String, Long>();

			for(int x=0; x<servers.length; x++)
			{
				servers[x]=new CountServer(rules, port+x+1, 50, this);
			}

			ServerSocket socket=new ServerSocket(port);



			Socket temp=new Socket("0.0.0.0", port);
			socket.accept().close();

			ip=temp.getInetAddress().toString();

			if(ip.indexOf('/')>=0)
			{
				ip=ip.substring(ip.lastIndexOf('/')+1);
			}

			if(ip.indexOf("\\".charAt(0))>=0)
			{
				ip=ip.substring(ip.lastIndexOf("\\".charAt(0))+1);
			}

			temp=null;

			System.out.println("[CONNECT] Accepting connections at "+ip+" on port "+port);


			while(true)
			{
				Socket s=socket.accept();

				System.out.println("[CONNECT] Connected "+s.getInetAddress().toString());



				if(connections.size()<userLimit)
				{
					connections.add(new MetaConnection(s, this));
				}
				else
				{
					MetaConnection c=new MetaConnection(s, this);

					System.out.println("[CONNECT] Rejected "+s.getInetAddress().toString()+": server is full.");

					c.send(new TextMessage("Server is full."));

					c.disconnect();
				}
			}


		}
		catch(IOException e)
		{
			System.err.println(e);
			System.exit(1);
		}
	}

	public boolean setName(String name, MetaConnection c)
	{
		if(scores.containsKey(name))
		{
			return false;
		}

		scores.put(name, (long)0);

		if(connections.size()==userLimit)
		{
			startRound();
		}

		return true;
	}

	public void sendToAll(final NetMessage msg)
	{
		Thread t=new Thread(new Runnable()
		{
			public void run()
			{
				synchronized(connections)
				{
					for(MetaConnection c:connections)
					{
						c.send(msg);
					}
				}
			}
		}, "Message Sender");

		t.start();
	}

	public void startRound()
	{
		System.out.println("[GAME] Starting round.");

		serverCnt=0;

		sendToAll(new ScoresMessage((Map<String, Long>)scores.clone()));

		synchronized(connections)
		{
			Collections.shuffle(connections);
			Collections.shuffle(connections);

			for(int x=0; x<connections.size(); x++)
			{
				connections.get(x).send(new ConnectMessage(ip, servers[x/4].getPort()));
			}
		}
	}

	public String getIP()
	{
		return ip;
	}

	public void addPlacings(String[] names)
	{
		synchronized(scores)
		{
			int loc=0;

			for(String x:names)
			{
				if(!scores.containsKey(x))
				{
					scores.put(x, (long)0);
				}

				scores.put(x, (long)(scores.get(x)+points[loc]));

				System.out.println("[SCORE] "+x+" - "+(scores.get(x)));
			}

			scoreDisplay.refresh(scores);
		}
	}

	public void serverFinished()
	{

		serverCnt++;

		if(serverCnt==servers.length)
		{
			roundCnt++;

			if(roundCnt<numRounds)
			{
				startRound();
			}
			else
			{
				showResults();
			}
		}
	}

	public Map<String, Long> getScores()
	{
		return scores;
	}

	public void removeConnection(MetaConnection c)
	{
		connections.remove(c);
	}

	public void showResults()
	{
		try
		{
			TreeSet<Score> set=new TreeSet<Score>();

			for(String x:scores.keySet())
			{
				set.add(new Score(x, scores.get(x)));
			}

			PrintWriter fileout=new PrintWriter(new File("Results.txt"));

			for(Score x:set)
			{
				fileout.println(x.getName()+"\n\n"+x.getScore());
				System.out.println("[RESULTS] "+x.getName()+" - "+x.getScore());
			}

			fileout.close();
		}
		catch(IOException e){}
	}
	public void showServerStartup()
	{
		// do nothing
	}
}