package vcnet.server;

import graphicutils.GraphicUtils;
import graphicutils.MessageBox;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import graphicutils.*;
import static graphicutils.GraphicUtils.*;
import vcnet.bot.BotClient;
import vcnet.mech.BombPlay;
import vcnet.mech.BreakPlay;
import vcnet.mech.Card;
import vcnet.mech.CardPlay;
import vcnet.mech.RuleList;
import vcnet.net.ControlMessage;
import vcnet.net.PopupMessage;
import vcnet.net.TurnChangeMessage;
import vcnet.player.ClientPlayer;
import vcnet.net.CardPlayMessage;
import vcnet.net.EndGameMessage;
import vcnet.net.JoinSuccessMessage;
import vcnet.net.NetMessage;
import vcnet.net.PassMessage;
import vcnet.net.PlayerInfoMessage;
import vcnet.net.PlayerOutMessage;
import vcnet.net.TextMessage;

public class Server implements Runnable
{
	public static void main(String[] args)
	{




		final JTextField port=new JTextField(6);



		final JButton create=new JButton("Create server");

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());




		final JCheckBox[] boxes=new JCheckBox[5];

		boxes[0]=new JCheckBox("Winner starts");
		boxes[1]=new JCheckBox("Play after pass");
		boxes[2]=new JCheckBox("Ignore locks");
		boxes[3]=new JCheckBox("Breaks affect only 2s");
		boxes[4]=new JCheckBox("BotPlayer delay");

		try
		{
			Scanner file=new Scanner(new File("Port.dat"));
			port.setText(file.nextLine().trim());
			for(byte x=0; x<boxes.length; x++)
			{
				boxes[x].setSelected(file.nextLine().trim().equals("1"));
			}
		}
		catch(Exception e){}

		GraphicUtils.constrain(p, new JLabel("Port: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p, port, 1, 0, 1, 1);

		GraphicUtils.constrain(p, new JLabel(""), 0, 1, 1, 1);
		int loc=2;

		for(JCheckBox b: boxes)
		{
			GraphicUtils.constrain(p, b, 0, loc++, 2, 1);
		}
		GraphicUtils.constrain(p, new JLabel(""), 0, loc++, 1, 1);
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
					BotClient.setUseDelay(boxes[4].isSelected());
					Server server=new Server(rules, Integer.parseInt(port.getText().trim()));

					try
					{
						PrintWriter fileout=new PrintWriter(new File("Port.dat"));
						fileout.println(port.getText().trim());
						for(JCheckBox b:boxes)
						{
							fileout.println(b.isSelected()?"1":"0");
						}
						fileout.close();
					}
					catch(IOException e2){}
					frame.setVisible(false);
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

		/*
		rules.setWinnerStarts(false);
		rules.setPlayAfterPass(true);
		rules.setLocksUsed(true);
		rules.setUniversalBreaks(false);
		*/
	}
	private int port;

	private String ip;

	private ArrayList<Connection> connections;

	private int time;

	private javax.swing.Timer timer;
	
	private Thread thread;

	private ArrayList<ClientPlayer> players;

	private RuleList rules;

	private CardPlay lastPlay;

	private String turn;

	private HashMap<Byte, String> places;

	private byte passed;
	
	private byte passReq;

	public Server(RuleList r, int p)
	{
		connections=new ArrayList<Connection>();

		players=new ArrayList<ClientPlayer>();

		port=p;

		rules=r;

		start();
	}

	public void setTime(int t)
	{
		time=t;
	}

	public void resetTimer()
	{
		if(time<=0)
		{
			return;
		}

		if(timer!=null)
		{
			timer.stop();
		}
		// if time passes without a move from current player, drop current player from the game

		timer=new javax.swing.Timer(1000*time, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				timer.stop();
				dropPlayer();
			}
		});
		timer.setRepeats(false);
		timer.start();
	}
	public void dropPlayer()
	{
		Connection c=getConnection(turn);
		c.disconnect();
		removeConnection(c);

	}
	public void stop()
	{
		thread.stop();
	}
	public byte getNumPlayers()
	{
		return (byte)connections.size();
	}
	public int getPort()
	{
		return port;
	}
	public String getPlace(byte b)
	{
		return places.get(b);
	}
	public void disconnectAll()
	{
		for(Connection c:connections)
		{
			c.disconnect();
		}
	}

	public void showServerStartup()
	{
		new MessageBox("Server IP", "Server running on "+ip+": "+port+"\n\nServer will exit if all players leave.");

	}
	public void start()
	{
		thread=new Thread(this, "Server");
		thread.start();
	}
	public void run()
	{

		try
		{
			ServerSocket listenSocket=new ServerSocket(port);

			Socket socket=new Socket("0.0.0.0", port);

			listenSocket.accept().close();

			ip=socket.getInetAddress().toString();

			showServerStartup();




			while(true)
			{
				Connection c=new Connection(listenSocket.accept(), this);
				c.start();


			}
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
	}

	public ClientPlayer getPlayer(String name)
	{
		if(name==null || name.trim().equals(""))
		{
			return null;
		}
		synchronized(players)
		{
			if(players==null)
			{
				return null;
			}

			for(ClientPlayer p:players)
			{
				if(p.getName().equalsIgnoreCase(name))
				{
					return p;
				}
			}

		}
		return null;
	}
	public Connection getConnection(String name)
	{
		if(name==null || name.trim().equals(""))
		{
			return null;
		}

		synchronized(connections)
		{


			for(Connection c:connections)
			{
				if(c.getName().equals(name))
				{
					return c;
				}
			}

		}
		return null;
	}

	public synchronized void removeConnection(Connection c)
	{
		ClientPlayer p=getPlayer(c.getName());

		if(p!=null)
		{


			sendToAll(new PlayerOutMessage(p.getName(), addToLast(c.getName())));

			synchronized(players)
			{
				players.remove(p);
			}

			if(turn.equals(p.getName()))
			{
				nextTurn();
			}

		}

		synchronized(connections)
		{
			connections.remove(c);

			if(checkExit())
			{
				exit();
			}
		}

		sendToAll(new TextMessage(c.getName()+" has left."));

	}
	public void exit()
	{
		stop();
		System.exit(1);
	}

	public boolean checkExit()
	{


		return connections.isEmpty();


		//return false;
	}

	public void startGame()
	{

		players.clear();

		ArrayList<Card> cards=getDeck();
		ArrayList<Card> temp;

		for(byte x=0; x<4; x++)
		{
			temp=new ArrayList<Card>();

			for(byte y=0; y<13; y++)
			{
				temp.add(cards.remove(0));
			}

			Collections.sort(temp);

			players.add(new ClientPlayer(connections.get(x).getName(), temp));

		}


		connections.get(0).send(new PlayerInfoMessage(rules, players.get(0).getCards(), players.get(1).getName(), players.get(2).getName(), players.get(3).getName()));
		connections.get(1).send(new PlayerInfoMessage(rules, players.get(1).getCards(), players.get(2).getName(), players.get(3).getName(), players.get(0).getName()));
		connections.get(2).send(new PlayerInfoMessage(rules, players.get(2).getCards(), players.get(3).getName(), players.get(0).getName(), players.get(1).getName()));
		connections.get(3).send(new PlayerInfoMessage(rules, players.get(3).getCards(), players.get(0).getName(), players.get(1).getName(), players.get(2).getName()));




		sendToAll(new TextMessage("Starting game."));

		passed=0;

		try
		{
			Thread.sleep(500);
		}
		catch(InterruptedException e){}

		if(rules.winnerStarts() && places!=null && places.get(1)!=null)
		{
			turn=places.get(1);

			hasControl();
		}
		else
		{
			for(ClientPlayer p:players)
			{
				if(p.hasCard(Card.THREE_OF_SPADES))
				{
					turn=p.getName();
					hasControl();
					break;
				}
			}
		}

		places=new HashMap<Byte, String>();

		resetTimer();
	}
	public CardPlay getLastPlay()
	{
		return lastPlay;
	}
	public boolean play(CardPlay cards, Connection connection)
	{
		passReq=(byte)(players.size()-1);
		
		if(!turn.equals(connection.getName()))
		{
			connection.send(new PopupMessage("Error", "It's not your turn."));
			return false;
		}

		ClientPlayer cp=getPlayer(connection.getName());

		if(cp.getPassed() && !((cards instanceof BreakPlay) || (cards instanceof BombPlay)))
		{
			connection.send(new PopupMessage("Error", "You have already passed this round."));

			return false;
		}

		for(Card c:cards.getCards())
		{
			if(!cp.hasCard(c))
			{
				connection.send(new PopupMessage("Error", "One or more of the cards played are not held."));
				if(lastPlay!=null)
				{
					connection.send(new TurnChangeMessage(connection.getName()));
				}
				else
				{
					connection.send(new ControlMessage(connection.getName()));
				}
				return false;
			}
		}

		if(!cards.verify())
		{
			connection.send(new PopupMessage("Error", "The CardPlay is incorrect."));

			if(lastPlay!=null)
			{
				connection.send(new TurnChangeMessage(connection.getName()));
			}
			else
			{
				connection.send(new ControlMessage(connection.getName()));
			}
			return false;
		}

		if(lastPlay!=null)
		{
			if(!cards.playable(lastPlay, rules))
			{
				connection.send(new PopupMessage("Error", "Those cards cannot be played."));
				connection.send(new TurnChangeMessage(connection.getName()));
				return false;
			}
		}
		else
		{
			if(!rules.winnerStarts() && !cards.getLowestCard().equals(Card.THREE_OF_SPADES) && cp.hasCard(Card.THREE_OF_SPADES))
			{
				connection.send(new PopupMessage("Error", "You must play the 3 of spades."));
				connection.send(new ControlMessage(connection.getName()));
				return false;
			}
		}

		sendToAll(new CardPlayMessage(cp.getName(), cards));

		for(Card c:cards.getCards())
		{
			cp.removeCard(c);
		}
		lastPlay=cards;

		setNextTurn(true);
		
		if(cp.getNumCards()==0)
		{
			players.remove(cp);
			sendToAll(new PlayerOutMessage(cp.getName(), addToFirst(cp.getName())));
		}

		if(players.size()==1)
		{
			sendToAll(new PlayerOutMessage(cp.getName(), addToLast(players.get(0).getName())));
			players.remove(0);

			endGame();
		}
		else
		{
			passed=0;
			nextTurn();
		}

		resetTimer();

		return true;
	}
	public void endGame()
	{
		System.out.println(places);
		sendToAll(new EndGameMessage(new String[]{places.get((byte)1), places.get((byte)2), places.get((byte)3), places.get((byte)4)}));

		if(checkRestart())
		{
			startGame();
		}
	}
	public boolean pass(Connection connection)
	{
		if(!turn.equals(connection.getName()))
		{
			connection.send(new PopupMessage("Error", "It's not your turn."));
			return false;
		}

		getPlayer(connection.getName()).setPassed(true);

		passed++;

		sendToAll(new PassMessage(connection.getName()));

		setNextTurn(true);
		
		if(passed>=passReq)
		{
			hasControl();
		}
		else
		{
			nextTurn();
		}

		return true;

	}

	protected void setNextTurn(boolean adv)
	{
		int idx=getPlayerIndex(turn);

		if(adv)
		{
			idx++;
		}

		if(idx>=players.size())
		{
			idx-=players.size();
		}
		turn=players.get(idx).getName();


	}
	public void nextTurn()
	{

		sendToAll(new TurnChangeMessage(turn));
	}

	public void hasControl()
	{

		lastPlay=null;
		passed=0;

		for(ClientPlayer p:players)
		{
			p.setPassed(false);
		}
		sendToAll(new ControlMessage(turn));

	}


	public void sendToAll(NetMessage msg)
	{
		for(Connection c:connections)
		{
			c.send(msg);
		}
	}
	public byte addToFirst(String name)
	{
		for(byte x=1; x<=4; x++)
		{
			if(!places.containsKey(x))
			{
				places.put(x, name);

				return x;
			}
		}
		return 0;
	}
	public byte addToLast(String name)
	{
		for(byte x=4; x>=1; x--)
		{
			if(!places.containsKey(x))
			{
				places.put(x, name);

				return x;
			}
		}
		return 0;
	}

	protected int getPlayerIndex(String name)
	{
		synchronized(players)
		{
			for(byte x=0; x<players.size(); x++)
			{
				if(players.get(x).getName().equals(name))
				{
					return x;
				}
			}
		}

		return -1;
	}
	protected ArrayList<Card> getDeck()
	{
		ArrayList<Card> cards=new ArrayList<Card>();

		for(byte x=0; x<13; x++)
		{
			for(byte y=0; y<4; y++)
			{
				cards.add(new Card(x, y));
			}


		}

		Collections.shuffle(cards);
		Collections.shuffle(cards);
		Collections.shuffle(cards);

		return cards;
	}

	public boolean addConnection(String name, Connection c)
	{
		// returns whether a new connection is added

		name=name.trim();
		if(name.length()<3)
		{
			c.send(new PopupMessage("Error", "Your username is less than 3 characters long."));
			return false;
		}
		synchronized(connections)
		{
			if(connections.contains(c) && getConnection(name)==null)
			{
				sendToAll(new TextMessage(c.getName()+" is now known as "+name+"."));
				c.send(new JoinSuccessMessage(true, name));
				return false;
			}
			else if(getConnection(name)!=null)
			{
				c.send(new JoinSuccessMessage(false, name));
				return false;
			}
			else if(connections.size()<4)
			{

				c.setName(name);
				sendToAll(new TextMessage(c.getName()+" has joined."));
				connections.add(c);
				c.send(new JoinSuccessMessage(true, name));
			}
			else
			{
				c.send(new PopupMessage("Server is full", "The server already has 4 players."));
				c.disconnect();
				return false;
			}
		}



		if(checkStart())
		{
			startGame();
		}
		else
		{
			c.send(new TextMessage("Waiting for other players to join..."));
		}

		return true;
	}

	public boolean checkRestart()
	{
		return connections.size()==4;
	}
	public boolean checkStart()
	{

		return connections.size()==4;

	}
	public String getIP()
	{
		return ip;
	}
}