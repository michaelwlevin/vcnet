package vcnet.bot;

import graphicutils.GraphicUtils;
import graphicutils.MessageBox;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import graphicutils.*;
import vcnet.mech.CardPlay;
import vcnet.mech.SinglePlay;

public class NoobBotClient extends BotClient
{
	
	public static void main(String[] args) throws IOException
	{
		
		
		final JTextField ip=new JTextField(12);
		final JTextField port=new JTextField(5);
		
		String name="";
		
		try
		{
			Scanner file=new Scanner(new File("ConnectTo.dat"));
			
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
					
					new NoobBotClient(ip.getText().trim(), Integer.parseInt(port.getText().trim()));
					
					try
					{
						PrintWriter fileout=new PrintWriter(new File("ConnectTo.dat"));
						fileout.println(ip.getText().trim());
						fileout.println(port.getText().trim());
						fileout.close();
					}
					catch(IOException e3){}
					frame.setVisible(false);
					
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
		
	
	private int loc;
	
	public NoobBotClient(String ip, int port) throws IOException
	{
		super(ip, port);
		

	}
	
	
	public CardPlay myTurn()
	{
		delay();
		
		if(!(getLastPlay() instanceof SinglePlay))
		{
			return null;
		}
		/*
		if(new SinglePlay(getHighestCard()).playable(getLastPlay(), getRules()))
		{
			return new SinglePlay(getHighestCard());
		}
		*/
		for(byte b=0; b<getCards().size(); b++)
		{
			if(new SinglePlay(getCards().get(b)).playable(getLastPlay(), getRules()))
			{
				return new SinglePlay(getCards().get(b));
			}
		}
		return null;
	}
	public CardPlay myControl()
	{
		delay();
		
		return new SinglePlay(getLowestCard());
	}
	
	public String getUsername(java.util.List<String> u)
	{
		return "noob bot"+(loc++);
	}
}