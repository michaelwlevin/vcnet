package vcnet.client;

import graphicutils.GraphicUtils;
import graphicutils.MessageBox;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import static graphicutils.GraphicUtils.*;
import vcnet.mech.RuleList;
import vcnet.net.ChatMessage;
import vcnet.net.JoinMessage;
import vcnet.net.JoinSuccessMessage;
import vcnet.net.MessageHandler;
import vcnet.net.PrivateMessage;
import vcnet.net.RemoveServerListMessage;
import vcnet.net.ServerConnectMessage;
import vcnet.net.ServerListMessage;
import vcnet.net.ServerListing;
import vcnet.net.UserListMessage;
import vcnet.server.CreateServerMessage;

public class MultiClient extends MessageHandler
{
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
						PrintWriter fileout=new PrintWriter(new File("options/ConnectTo.dat"));
						fileout.println(ip.getText().trim());
						fileout.println(port.getText().trim());
						fileout.close();
					}
					catch(IOException e3){}
					frame.setVisible(false);
					MultiClient c=new MultiClient(s);
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

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(1);
			}
		});

		frame.setVisible(true);
	}

	private TreeSet<String> users;
	private JFrame frame;
	private JTextArea output;
	private JTextField input, pmname;
	private JList ulist;
	private JList slist;
	private String name;
	private JPanel panel;

	private TreeSet<ServerListing> servers;

	public MultiClient(Socket s)
	{
		super(s);


		name="";

		frame=new JFrame("VCNet");
		output=new JTextArea(25, 40);
		output.setEditable(false);

		input=new JTextField(34);
		pmname=new JTextField(10);

		JButton send=new JButton("Send");
		JButton pm=new JButton("PM");
		

		ActionListener action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendChat(input.getText().trim());
				input.setText("");
			}
		};
		input.addActionListener(action);
		send.addActionListener(action);
		
		action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sendPM(pmname.getText().trim(), input.getText().trim());
				input.setText("");
			}
		};
		
		pmname.addActionListener(action);
		pm.addActionListener(action);

		users=new TreeSet<String>();
		servers=new TreeSet<ServerListing>();

		ulist=new JList();
		ulist.setSelectionMode(0);
		ulist.setFixedCellWidth(100);
		ulist.setFixedCellHeight(20);

		slist=new JList();
		slist.setSelectionMode(0);
		slist.setFixedCellWidth(100);
		slist.setFixedCellHeight(20);



		JButton create=new JButton("New server");
		create.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				newServer();
			}
		});

		final JButton join=new JButton("Join server");
		join.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(slist.getSelectedValue()!=null)
				{
					ServerListing temp=(ServerListing)slist.getSelectedValue();
					
					joinServer(temp.getID(), temp.hasPassword());
				}
			}
		});
		join.setEnabled(false);

		slist.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				join.setEnabled(slist.getSelectedValue()!=null);
			}
		});

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());

		panel=p;
		
		constrain(p, new JLabel("Users:"), 0, 1, 1, 1);
		constrain(p, new JScrollPane(ulist), 0, 2, 1, 1);
		constrain(p, new JScrollPane(output), 1, 1, 4, 3);
		constrain(p, input, 1, 4, 3, 1);
		constrain(p, send, 4, 4, 1, 1);
		constrain(p, new JLabel("PM to "), 1, 5, 1, 1);
		constrain(p, pmname, 2, 5, 1, 1);
		constrain(p, pm, 3, 5, 1, 1);
		constrain(p, new JScrollPane(slist), 0, 3, 1, 1);
		constrain(p, join, 0, 4, 1, 1);
		constrain(p, create, 0, 5, 1, 1);
		
		JMenuBar mb=new JMenuBar();
		JMenu me=new JMenu("Options");
		JMenuItem mi=new JMenuItem("Log out");
		mi.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(1);
			}
		});
		me.add(mi);
		mb.add(me);

		frame.setJMenuBar(mb);
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

		login();

		start();
	}

	public void joinServer(final long id, boolean pass)
	{
		if(!pass)
		{
			joinServer(id, "");
		}
		else
		{
			final JFrame frame=new JFrame("Password?");
			
			final JTextField pw=new JTextField(10);
			
			final JButton enter=new JButton("Enter");
			
			ActionListener action=new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					frame.setVisible(false);
					
					joinServer(id, pw.getText());
					
					frame.dispose();
				}	
			};
			enter.addActionListener(action);
			pw.addActionListener(action);
			
			JPanel p=new JPanel();
			p.setLayout(new GridBagLayout());
			constrain(p, new JLabel("Password:"), 0, 0, 1, 1);
			constrain(p, pw, 1, 0, 1, 1);
			constrain(p, enter, 2, 0, 1, 1);
			
			frame.add(p);
			frame.pack();
			frame.setResizable(false);
			frame.setVisible(true);
		}
	}
	public void joinServer(long id, String pass)
	{
		try
		{
			new Client2(new Socket(getIP(), getPort()+1), name, pass, id);
		}
		catch(IOException ex)
		{
			new MessageBox("Connection failed", "Could not connect to game server.");

		}
	}
	public void newServer()
	{
		final JTextField name=new JTextField(10);
		final JTextField password=new JTextField(10);


		final JButton create=new JButton("Create server");

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());




		final JCheckBox[] boxes=new JCheckBox[4];

		boxes[0]=new JCheckBox("Winner starts");
		boxes[1]=new JCheckBox("Play after pass");
		boxes[2]=new JCheckBox("Ignore locks");
		boxes[3]=new JCheckBox("Breaks affect only 2s");
		
		boolean temp=true;

		try
		{
			Scanner file=new Scanner(new File("options/Rules.dat"));
			
			for(byte x=0; x<boxes.length; x++)
			{
				boxes[x].setSelected(file.nextLine().trim().equals("1"));
			}
			temp=file.nextLine().trim().equals("1");
			name.setText(file.nextLine().trim());
		}
		catch(Exception e){}
		
		final boolean temp2=temp;

		GraphicUtils.constrain(p, new JLabel("Name: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p, name, 1, 0, 1, 1);
		GraphicUtils.constrain(p, new JLabel("Password:"), 0, 1, 1, 1);
		GraphicUtils.constrain(p, password, 1, 1, 1, 1);

		GraphicUtils.constrain(p, new JLabel(""), 0, 1, 1, 1);
		int loc=3;

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
				if(name.getText().trim().equals(""))
				{
					name.setText("");
					name.requestFocus();
					return;
				}

				RuleList rules=new RuleList();

				rules.setWinnerStarts(boxes[0].isSelected());
				rules.setPlayAfterPass(boxes[1].isSelected());
				rules.setLocksUsed(!boxes[2].isSelected());
				rules.setUniversalBreaks(!boxes[3].isSelected());


				try
				{
					PrintWriter fileout=new PrintWriter(new File("options/Rules.dat"));
					
					for(JCheckBox b:boxes)
					{
						fileout.println(b.isSelected()?"1":"0");
					}
					fileout.println(temp2?"1":"0");
					fileout.close();
				}
				catch(IOException e2){}

				send(new CreateServerMessage(name.getText().trim(), password.getText().trim(), rules));

				frame.setVisible(false);
				frame.dispose();



			}
		};

		name.addActionListener(action);
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
	public int getSubServerPort()
	{
		return getPort()+1;
	}
	public void sendChat(String msg)
	{
		send(new ChatMessage(name, msg));
	}
	public void sendPM(String to, String msg)
	{
		send(new PrivateMessage(to, name, msg));
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

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(1);
			}
		});

		name.requestFocus();

		frame.setVisible(true);
	}

	public String getName()
	{
		return name;
	}
	
	public void showChat(String line)
	{
		output.setText(output.getText()+line+"\n");
	}
	public void handleChat(ChatMessage msg)
	{
		showChat(msg.getFrom()+": "+msg.getMessage());
	}
	public void handlePM(PrivateMessage msg)
	{
		if(getName().equals(msg.getTo()))
		{
			showChat("From "+msg.getFrom()+": "+msg.getMessage());
		}
		else if(getName().equals(msg.getFrom()))
		{
			showChat("To "+msg.getTo()+": "+msg.getMessage());
		}
	}
	public void handleJoinSuccess(JoinSuccessMessage msg)
	{

		if(msg.isSuccessful())
		{
			name=msg.getName();
			constrain(panel, new JLabel("You are logged in as "+name), 0, 0, 5, 1);
			
			frame.pack();
			frame.setVisible(true);

		}
		else
		{
			new MessageBox("Login failed", "Your username is already in use.")
			{
				public void close()
				{
					login();
				}
			};
		}
	}
	public void handleUserList(UserListMessage msg)
	{
		if(msg.getAdd())
		{
			users.add(msg.getName());
		}
		else
		{
			users.remove(msg.getName());
		}
		refreshUserList();
	}
	public void refreshUserList()
	{
		ulist.setListData(users.toArray());
	}
	public void refreshServerList()
	{
		slist.setListData(servers.toArray());
	}
	public void handleServerList(ServerListMessage msg)
	{
		servers.remove(msg.getListing());
		servers.add(msg.getListing());

		refreshServerList();
	}
	public void handleRemoveServerList(RemoveServerListMessage msg)
	{
		for(ServerListing s:servers)
		{
			if(s.getID()==msg.getID())
			{
				servers.remove(s);
				break;
			}
		}
		
		refreshServerList();
	}
	public void handleServerConnect(ServerConnectMessage msg)
	{
		try
		{
			new Client2(new Socket(getIP(), msg.getPort()), name, msg.getPassword(), msg.getID());
		}
		catch(IOException e)
		{
			new MessageBox("Connection failed", "Could not connect to game server.");
		}
	}
	
}