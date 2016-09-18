package vcnet.client;

import vcnet.gui.ClientGUI;
import graphicutils.GraphicUtils;
import graphicutils.MessageBox;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import graphicutils.*;
import vcnet.gui.VCIcon;
import vcnet.gui.VCPanel;
import vcnet.mech.Card;
import vcnet.mech.RuleList;
import vcnet.net.CardPlayMessage;
import vcnet.net.ChatMessage;
import vcnet.net.ControlMessage;
import vcnet.net.EndGameMessage;
import vcnet.net.JoinMessage;
import vcnet.net.JoinSuccessMessage;
import vcnet.net.MessageHandler;
import vcnet.net.PassMessage;
import vcnet.net.PlayerInfoMessage;
import vcnet.net.PlayerOutMessage;
import vcnet.net.PopupMessage;
import vcnet.net.TextMessage;
import vcnet.net.TurnChangeMessage;
import vcnet.player.AbstractPlayer;
import vcnet.player.BasicPlayer;
import vcnet.player.ClientPlayer;

public class Client extends MessageHandler
{
	public static String convertRank(int rank)
	{
		switch(rank)
		{
			case 1: return "1st";
			case 2: return "2nd";
			case 3: return "3rd";
			case 4: return "4th";
			default: return "";
		}
	}
	public static void main(String[] args)
	{

		final JTextField ip=new JTextField(12);
		final JTextField port=new JTextField(5);

		String name="";

		try
		{
			Scanner file=new Scanner(new File("options/ConnectTo.dat"));

			ip.setText(file.nextLine().trim());
			port.setText(file.nextLine().trim());
		}
		catch(Exception e){}

		JButton join=new JButton("Join Server");

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());

		GraphicUtils.constrain(p, new JLabel("IP: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p, ip, 1, 0, 1, 1);
		GraphicUtils.constrain(p, new JLabel("Port: "), 0, 1, 1, 1);
		GraphicUtils.constrain(p, port, 1, 1, 1, 1);
		GraphicUtils.constrain(p, join, 0, 2, 2, 1, GridBagConstraints.CENTER);

		final JFrame frame=new JFrame("Join server");

		ActionListener action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if(ip.getText().trim().equals(""))
					{
						ip.setText("");
						ip.requestFocus();
						return;
					}

					Socket s=new Socket(ip.getText().trim(), Integer.parseInt(port.getText().trim()));

					try
					{
						PrintWriter fileout=new PrintWriter(new File("ConnectTo.dat"));
						fileout.println(ip.getText().trim());
						fileout.println(port.getText().trim());
						fileout.close();
					}
					catch(IOException e3){}
					frame.setVisible(false);
					Client c=new Client(s);
				}
				catch(NumberFormatException n)
				{
					port.setText("");
					port.requestFocus();
				}
				catch(IOException e2)
				{
					new MessageBox("Error", "Could not connect to server.");
					ip.setText("");
					port.setText("");
					ip.requestFocus();
				}
			}
		};

		ip.addActionListener(action);
		port.addActionListener(action);
		join.addActionListener(action);


		frame.add(p);
		frame.pack();
		frame.setResizable(false);
                
                frame.setIconImage(VCIcon.getIcon());

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(1);
			}
		});

		frame.setVisible(true);
	}
	private String name;
	private ClientGUI gui;

	private VCPanel vcPanel;

	private RuleList rules;

	private ArrayList<AbstractPlayer> players;

	public Client(Socket s)
	{
		super(s);

		login();


		Card.loadStaticImages();


		players=new ArrayList<AbstractPlayer>();

		start();

	}
	public void run()
	{
		gui=new ClientGUI(this);
		vcPanel=gui.getVCPanel();

		super.run();
	}

	public void login()
	{
		final JTextField name=new JTextField(10);

		try
		{
			Scanner file=new Scanner(new File("options/Name.dat"));

			name.setText(file.nextLine().trim());
		}
		catch(IOException e){}

		final JButton join=new JButton("Join");


		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());



		final JFrame frame=new JFrame("Join server");

		ActionListener action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(name.getText().trim().equals(""))
				{
					name.setText("");
					name.requestFocus();
				}
				else if(name.getText().trim().length()>10)
				{
					new MessageBox("Username length", "Your username is too long.");
					name.setText("");
				}
				else
				{
					frame.setVisible(false);
					send(new JoinMessage(name.getText().trim(), 0));



				}
			}
		};

		name.addActionListener(action);
		join.addActionListener(action);


		GraphicUtils.constrain(p, new JLabel("Name: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p, name, 1, 0, 1, 1);
		GraphicUtils.constrain(p, join, 0, 1, 2, 1, GridBagConstraints.CENTER);

		frame.add(p);
		frame.pack();
		frame.setResizable(false);
                
                frame.setIconImage(VCIcon.getIcon());

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(1);
			}
		});

		name.requestFocus();
                
                frame.setLocationRelativeTo(null);

		frame.setVisible(true);
	}

	public void disconnect()
	{
		if(gui!=null)
		{
			gui.setVisible(false);
		}
		super.disconnect();

		new MessageBox("Disconnected", "You have been disconnected!")
		{
			public void close()
			{
				System.exit(1);
			}
		};
	}

	public void handleJoinSuccess(JoinSuccessMessage msg)
	{
		if(msg.isSuccessful())
		{
			name=msg.getName();
			gui.setTitle("VCNet - "+name);
			gui.setVisible(true);

			try
			{
				PrintWriter fileout=new PrintWriter(new File("options/Name.dat"));
				fileout.println(name);
				fileout.close();
			}
			catch(IOException e){}
		}
		else
		{
			new MessageBox("Duplicate username", "Your username is already in use.")
			{
				public void close()
				{
					login();
				}
			};

		}
	}

	public void handlePlayerInfo(PlayerInfoMessage msg)
	{

		rules=msg.getRules();

		Collections.sort(msg.getCards());

		players=new ArrayList<AbstractPlayer>();

		players.add(new ClientPlayer(getName(), msg.getCards()));
		players.add(new BasicPlayer(msg.getPlayer2()));
		players.add(new BasicPlayer(msg.getPlayer3()));
		players.add(new BasicPlayer(msg.getPlayer4()));


		vcPanel.setPlayers(players);

		addGameMsg("[Start game]");



	}
	public void handleText(TextMessage msg)
	{
		gui.addChat(msg.getText());
	}
	public void handlePass(PassMessage msg)
	{
		vcPanel.getCenter().pass(msg.getPlayer());

		if(msg.getPlayer().equals(getName()))
		{
			addGameMsg("You passed.");
			getClientPlayer().setPassed(true);
		}
		else
		{
			addGameMsg(msg.getPlayer()+" passed.");
		}
	}
	public void handleCardPlay(CardPlayMessage msg)
	{
		vcPanel.getCenter().playCards(msg.getPlayer(), msg.getCardPlay());

		getPlayer(msg.getPlayer()).playedCards(msg.getCardPlay().getCards());

		if(msg.getPlayer().equals(getName()))
		{
			vcPanel.getBottom().refresh();

			addGameMsg("You played "+msg.getCardPlay().getCards().toString());
		}
		else
		{
			addGameMsg(msg.getPlayer()+" played "+msg.getCardPlay().getCards().toString()+".");
		}
	}
	public void handleControl(ControlMessage msg)
	{
		vcPanel.getCenter().hasControl(msg.getPlayer());

		getClientPlayer().setPassed(false);
		
		String text="";
		
		for(byte x=0; x<gui.getTextWidth(); x++)
		{
			text+="_";
		}
		addGameMsg(text);

		if(msg.getPlayer().equals(getName()))
		{
			vcPanel.startTurn();

			addGameMsg("You have control.");
		}
		else
		{
			addGameMsg(msg.getPlayer()+" has control.");
		}
	}
	public void handleTurnChange(TurnChangeMessage msg)
	{
		vcPanel.getCenter().setTurn(msg.getPlayer());

		if(msg.getPlayer().equals(getName()))
		{
			vcPanel.startTurn();

			//addGameMsg("Your turn.");
		}
		else
		{
			//addGameMsg(msg.getPlayer()+"'s turn.");
		}
	}
	public void handleChat(ChatMessage msg)
	{
		gui.addChat(msg.getFrom()+": "+msg.getMessage());
	}
	public void handlePopup(PopupMessage msg)
	{
		new MessageBox(msg.getTitle(), msg.getMessage());
	}
	public void handleEndGame(EndGameMessage msg)
	{

		addGameMsg("");
		addGameMsg("Game results:");
		String temp="";
		for(byte x=0; x<msg.getPlacings().length; x++)
		{
			temp+=""+(x+1)+".  "+msg.getPlacings()[x]+"\n";

			addGameMsg(""+(x+1)+". "+msg.getPlacings()[x]);
		}
		gui.addChat(temp);

		new MessageBox("Game results", temp);

		vcPanel.clear();

		addGameMsg("");


	}
	public void handlePlayerOut(PlayerOutMessage msg)
	{

		AbstractPlayer p=getPlayer(msg.getPlayer());
		p.setRank(msg.getRank());
		p.clearCards();

		addGameMsg(msg.getPlayer()+" got "+convertRank(msg.getRank())+" place!");
		gui.repaint();
	}

	public void addGameMsg(String msg)
	{
		gui.addGameMsg(msg);
	}

	public ClientPlayer getClientPlayer()
	{
		return (ClientPlayer)players.get(0);
	}
	public AbstractPlayer getPlayer(String name)
	{
		for(AbstractPlayer p:players)
		{
			if(p.getName().equals(name))
			{
				return p;
			}
		}
		return null;
	}

	public String getName()
	{
		return name;
	}

	public RuleList getRules()
	{
		return rules;
	}
}