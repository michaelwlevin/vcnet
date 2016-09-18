package vcnet.net;

import graphicutils.GraphicUtils;
import graphicutils.MessageBox;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Arrays;
import java.awt.image.*;
import graphicutils.*;
import vcnet.mech.Card;
import vcnet.mech.RuleList;
import vcnet.net.ChatMessage;
import vcnet.client.Client;
import vcnet.gui.VCPanel;

public class ClientGUI extends JFrame
{
	private VCPanel vcPanel;

	private JTextArea chatOut;
	private JTextField chatIn;

	private JTextArea gameMsg;

	private Client client;

	public ClientGUI(Client c)
	{
		client=c;

		vcPanel=new VCPanel(c);

		chatOut=new JTextArea(5, 35);
		chatOut.setEditable(false);
		chatOut.setLineWrap(true);
		chatIn=new JTextField(28);

		gameMsg=new JTextArea(7, 29);
		gameMsg.setEditable(false);
		//gameMsg.setLineWrap(true);

		JButton send=new JButton("Send");

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());

		GraphicUtils.constrain(p, vcPanel, 0, 0, 3, 1);

		setBackground(vcPanel.getBackground());
		p.setBackground(vcPanel.getBackground());

		GraphicUtils.constrain(p, new JScrollPane(chatOut), 0, 1, 2, 1);
		GraphicUtils.constrain(p, new JScrollPane(gameMsg), 2, 1, 1, 2);

		GraphicUtils.constrain(p, chatIn, 0, 2, 1, 1);
		GraphicUtils.constrain(p, send, 1, 2, 1, 1);

		ActionListener sendChat=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(!chatIn.getText().trim().equals(""))
				{
					sendChat(chatIn.getText().trim());
				}
				chatIn.setText("");

			}
		};

		chatIn.addActionListener(sendChat);
		send.addActionListener(sendChat);


		JMenuBar menu=new JMenuBar();
		JMenu m=new JMenu("Rules");
		JMenuItem rules=new JMenuItem("View rules");

		rules.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showRules();
			}
		});
		m.add(rules);
		
		
		JMenuItem order=new JMenuItem("Order of values/suits");
		order.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showOrder();
			}
		});
		m.add(order);
		menu.add(m);
		
		
		m=new JMenu("Options");
		JMenu m2=new JMenu("Suit symbols");
		
		
		JMenuItem symbols=new JMenuItem("Use symbols");
		symbols.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Card.setUseSymbols(true);
			}
		});
		m2.add(symbols);
		symbols=new JMenuItem("Use names");
		symbols.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Card.setUseSymbols(false);
			}
		});
		m2.add(symbols);
		
		m.add(m2);
		menu.add(m);
		
		
		setJMenuBar(menu);

		add(p);
		pack();
		setResizable(false);

		Timer t=new Timer(100, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				repaint();
			}

		});

		t.start();


		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(1);
			}
		});
	}
	public void showOrder()
	{
		if(Card.getUseSymbols())		
		{
			new MessageBox("Order of values/suits", "Values: "+Arrays.toString(Card.VALUES)+"\nSuits: "+Arrays.toString(Card.SYMBOLS));
		}
		else
		{
			new MessageBox("Order of values/suits", "Values: "+Arrays.toString(Card.VALUES)+"\nSuits: "+Arrays.toString(Card.SUITS));
		}
	}
	public void showRules()
	{

		/*
		Thread t=new Thread(new Runnable()
		{
			public void run()
			{
		*/
				final RuleList rules=client.getRules();

				if(rules==null)
				{
					return;
				}
				String output="";



				output+="Play after pass: "+(rules.playAfterPass()?"On":"Off")+"\n";
				output+=(rules.universalBreaks()?"Breaks beat everything":"Breaks can only be played on bombs and 2s")+"\n";
				output+=(rules.winnerStarts()?"Winner starts":"3 of spades starts")+"\n";
				output+=(rules.locksUsed()?"Locks enabled":"Locks disabled")+"\n";

				new MessageBox("VCNet Rules", output);
		/*
			}

		}, "Rules");

		t.start();
		*/
	}
	public void sendChat(String c)
	{
		client.send(new ChatMessage(client.getName(), c));
	}
	public VCPanel getVCPanel()
	{
		return vcPanel;
	}
	public int getTextWidth()
	{
		return gameMsg.getColumns();
	}
	public void addChat(String chat)
	{
		chatOut.setText(chatOut.getText()+chat+"\n");
		chatOut.setCaretPosition(chatOut.getText().length());
	}
	public void addGameMsg(String msg)
	{
		gameMsg.setText(gameMsg.getText()+msg+"\n");
		gameMsg.setCaretPosition(gameMsg.getText().length());
	}

	public void update(Graphics g)
	{
		Graphics2D graph=(Graphics2D)g;
		BufferedImage back = (BufferedImage)(this.createImage(getWidth(),getHeight()));
		Graphics window = back.createGraphics();

		paint(window);

		graph.drawImage(back, null, 0, 0);
	}

	public void repaint()
	{
		vcPanel.repaint();

		for(Component c:getComponents())
		{
			c.repaint();
		}

		super.repaint();
	}
}