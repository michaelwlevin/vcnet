package vcnet.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import vcnet.mech.CardPlay;

public class CenterPanel extends JPanel
{
	private String lastPlayer;
	private String turnPlayer;
	private boolean pass;
	private boolean hasControl;
	
	private CardPlay lastPlay;
	
	private String name;
	public CenterPanel()
	{
		setPreferredSize(new Dimension(400, 150));
		
		//setBackground(new Color(55, 172, 26));
		
		clear();
		
		addMouseListener(new MouseListener()
		{
			public void mouseEntered(MouseEvent e)
			{
				repaint();
			}
			public void mouseExited(MouseEvent e){}
			public void mouseClicked(MouseEvent e){}
			public void mousePressed(MouseEvent e){}
			public void mouseReleased(MouseEvent e){}
		});
	}
	public void setName(String n)
	{
		name=n;
	}
	public CardPlay getLastPlay()
	{
		return lastPlay;
	}
	public void paint(Graphics window)
	{
		window.setColor(getBackground());
		window.fillRect(0, 0, getWidth(), getHeight());
		
		window.setColor(Color.yellow);
		window.setFont(new Font("Arial", 0, 12));
		
		String temp=""+lastPlayer;
		
		
		if(lastPlayer!=null && lastPlay!=null && !lastPlayer.trim().equals(""))
		{
			if(temp.equals(name))
			{
				temp="You";
			}
			
			if(pass)
			{
				window.drawString(temp+" passed.", 10, 13);
			}
			else
			{
				window.drawString(temp+" played a "+lastPlay.getName()+".", 10, 13);
			}
		}
		
		
		if(turnPlayer!=null && !turnPlayer.trim().equals(""))
		{

			
			if(hasControl)
			{
				if(turnPlayer.equals(name))
				{
					window.drawString("You have control.", 10, 30);
				}
				else
				{
					window.drawString(turnPlayer+" has control.", 10, 30);
				}
			}
			else
			{
				if(turnPlayer.equals(name))
				{
					window.drawString("Your turn.", 10, 30);
				}
				else
				{
					window.drawString(turnPlayer+"'s turn.", 10, 30);
				}
			}
		}
		
		try
		{

			
			for(byte cnt=0; cnt<lastPlay.getNumCards(); cnt++)
			{
				
				lastPlay.getCard(cnt).draw(window, cnt*25+10, 50, 66, 99);
				
				
			}
		}
		catch(Exception e)
		{
			repaint();
		}
	}
	
	public void clear()
	{
		lastPlayer="";
		turnPlayer="";
		lastPlay=null;
		repaint();
	}
	
	public boolean getHasControl()
	{
		return hasControl;
	}
	
	public void playCards(String player, CardPlay cards)
	{
		lastPlayer=player;
		lastPlay=cards;
		pass=false;
		
		hasControl=(lastPlay==null);
		repaint();
	}
	public void pass(String player)
	{
		lastPlayer=player;
		pass=true;
		repaint();
	}
	public void hasControl(String player)
	{
		lastPlayer="";
		turnPlayer=player;
		pass=false;
		hasControl=true;
		lastPlay=null;
		repaint();
	}
	public void setTurn(String player)
	{
		turnPlayer=player;
		repaint();
		
	}
	
}