package vcnet;

import graphicutils.GraphicUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import vcnet.mech.RuleList;
import graphicutils.*;
import vcnet.bot.BotClient;
import vcnet.bot.DefaultBotClient;
import vcnet.bot.NoobBotClient;
import vcnet.client.Client;
import vcnet.server.Server;

public class SinglePlayerVC
{
	public static void main(String[] args)
	{
	
		
		
		
		
		final JButton create=new JButton("Start playing");
		
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
			Scanner file=new Scanner(new File("Rules.dat"));
			for(byte x=0; x<boxes.length; x++)
			{
				boxes[x].setSelected(file.nextLine().trim().equals("1"));
			}
		}
		catch(Exception e){}
		
		
		int loc=0;
		
		for(JCheckBox b: boxes)
		{
			GraphicUtils.constrain(p, b, 0, loc++, 2, 1);
		}
		GraphicUtils.constrain(p, new JLabel(""), 0, loc++, 1, 1);
		GraphicUtils.constrain(p, create, 0, loc, 2, 1, GridBagConstraints.CENTER);
		
		final RuleList rules=new RuleList();
		
		final JFrame frame=new JFrame("Start VCNet");
		
		ActionListener action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
	
				rules.setWinnerStarts(boxes[0].isSelected());
				rules.setPlayAfterPass(boxes[1].isSelected());
				rules.setLocksUsed(!boxes[2].isSelected());
				rules.setUniversalBreaks(!boxes[3].isSelected());
				BotClient.setUseDelay(boxes[4].isSelected());
				new SinglePlayerVC(rules);
					
				try
				{
					PrintWriter fileout=new PrintWriter(new File("Rules.dat"));
					for(JCheckBox b:boxes)
					{
						fileout.println(b.isSelected()?"1":"0");
					}
					fileout.close();
				}
				catch(IOException e2){}
				frame.setVisible(false);


			}
		};
		

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
                
                frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		

	}
	
	private Server server;
	
	private static final int port=2400;
	
	
	
	public SinglePlayerVC(RuleList rules)
	{
		server=new Server(rules, port)
		{
			public void showServerStartup(){}
		};
		
		try
		{
			Thread.sleep(400);
		}
		catch(InterruptedException e){}
		
		try
		{
			new DefaultBotClient("0.0.0.0", port)
			{
				public void showBotStart(){}
			};
			new DefaultBotClient("0.0.0.0", port)
			{
				public void showBotStart(){}
			};
			new DefaultBotClient("0.0.0.0", port)
			{
				public void showBotStart(){}
			};
			/*
			new DefaultBotClient("0.0.0.0", port)
			{
				public void showBotStart(){}
			};
			*/
		
		
			new Client(new Socket("0.0.0.0", port));
		}
		catch(IOException e){}
	}
}