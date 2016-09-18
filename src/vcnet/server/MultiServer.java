package vcnet.server;

import graphicutils.GraphicUtils;
import graphicutils.MessageBox;
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import vcnet.mech.RuleList;
import graphicutils.*;
import vcnet.net.JoinSuccessMessage;
import vcnet.net.NetMessage;
import vcnet.net.RemoveServerListMessage;
import vcnet.net.ServerListMessage;
import vcnet.net.UserListMessage;

public class MultiServer implements Runnable
{

	public static void main(String[] args)
	{


		final JTextField port=new JTextField(6);



		final JButton create=new JButton("Create server");

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());



		try
		{
			Scanner file=new Scanner(new File("options/Port.dat"));
			port.setText(file.nextLine().trim());

		}
		catch(Exception e){}

		GraphicUtils.constrain(p, new JLabel("Port: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p, port, 1, 0, 1, 1);

		GraphicUtils.constrain(p, create, 0, 1, 2, 1, GridBagConstraints.CENTER);


		final JFrame frame=new JFrame("Start server");

		ActionListener action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					MultiServer server=new MultiServer(Integer.parseInt(port.getText().trim()));


					try
					{
						PrintWriter fileout=new PrintWriter(new File("options/Port.dat"));
						fileout.println(port.getText().trim());

						fileout.close();
					}
					catch(IOException ex){}

					frame.setVisible(false);
					frame.dispose();
				}
				catch(NumberFormatException e1)
				{
					port.setText("");
					port.requestFocus();
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


	}


	private long next_id;

	private int port;


	private HashMap<Long, SubServer> servers;

	private HashMap<String, Connection2> connections;



	public MultiServer(int p)
	{
		port=p;
		servers=new HashMap<Long, SubServer>();

		connections=new HashMap<String, Connection2>();

		start();
	}
	public int getPort()
	{
		return port;
	}
	public int getSubServerPort()
	{
		return port+1;
	}
	public SubServer createServer(String n, String p, RuleList r)
	{
		while(servers.containsKey(++next_id));

		return createServer(next_id, n, p, r);
	}
	public SubServer createServer(long s, String n, String p, RuleList r)
	{
		SubServer output=new SubServer(n, p, s, r, this);

		synchronized(servers)
		{

			servers.put(s, output);
		}
		return output;
	}
	public void removeServer(SubServer s)
	{
		long id=s.getID();
		
		servers.remove(id);
		
		sendToAll(new RemoveServerListMessage(id));
		
	}
	public void start()
	{
		Thread t=new Thread(this, getClass().getName());
		t.start();


		final MultiServer server=this;

		t=new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					ServerSocket socket=new ServerSocket(getSubServerPort());

					while(true)
					{
						Connection3 c=new Connection3(socket.accept(), server);

					}
				}
				catch(IOException e)
				{
					e.printStackTrace(System.err);
					System.exit(1);
				}
			}
		}, "SubServer connections");
		t.start();
	}
	public void run()
	{
		try
		{
			ServerSocket socket=new ServerSocket(port);

			new MessageBox("VC MetaServer", "Click to exit.")
			{
				public void close()
				{
					System.exit(1);
				}
			};

			while(true)
			{
				new Connection2(socket.accept(), this);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
	public void joinServer(String n, String p, long id, Connection c)
	{

		SubServer s=servers.get(id);

		if(s!=null && s.getNumPlayers()<4 && p.equals(s.getPassword()))
		{
			c.setServer(s);
			s.addConnection(n, c);

		}
		else
		{
			c.disconnect();
		}

	}

	public Connection2 getConnection(String name)
	{
		return connections.get(name);
	}
	public void addConnection(String n, long id, Connection2 c)
	{
		if(connections.containsKey(n))
		{
			c.send(new JoinSuccessMessage(false, n));
			return;
		}

		c.setName(n);
		c.send(new JoinSuccessMessage(true, n));

		synchronized(connections)
		{
			connections.put(n, c);
		}
		for(String x:connections.keySet())
		{
			connections.get(x).send(new UserListMessage(n, true));
			c.send(new UserListMessage(x, true));
		}



		for(long i:servers.keySet())
		{
			c.send(new ServerListMessage(servers.get(i).getServerListing()));
		}

	}
	public void removeConnection(String n)
	{
		synchronized(connections)
		{
			connections.remove(n);

			for(String c:connections.keySet())
			{
				connections.get(c).send(new UserListMessage(n, false));
			}
		}
	}
	public void update(SubServer s)
	{
		sendToAll(new ServerListMessage(s.getServerListing()));
	}
	public void sendToAll(NetMessage msg)
	{

		for(String c:connections.keySet())
		{
			connections.get(c).send(msg);
		}

	}
}