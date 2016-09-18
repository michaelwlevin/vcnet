package vcnet.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import vcnet.mech.Card;
import vcnet.player.AbstractPlayer;
import vcnet.client.Client;

public class SidePanel extends JPanel
{
	private AbstractPlayer player;
	
	public SidePanel(AbstractPlayer p)
	{
		player=p;
		
		setPreferredSize(new Dimension(140, 220));
		
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
	public void setPlayer(AbstractPlayer p)
	{
		player=p;
		
		repaint();
	}
	
	public void paint(Graphics window)
	{
		window.setColor(getBackground());
		window.fillRect(0, 0, getWidth(), getHeight());
		
		if(player==null)
		{
			return;
		}
		
		window.setColor(Color.yellow);
		window.setFont(new Font("Arial", 0, 12));
		
		window.drawString(player.getName(), 3, 15);
		
		
		if(player.getRank()==0)
		{
			window.drawString(""+player.getNumCards()+" card"+(player.getNumCards()!=1?"s":"")+" left", 3, 30); 
		}
		else
		{
			window.drawString(Client.convertRank(player.getRank())+" place", 3, 30);
		}

	

		
		for(byte x=0; x<player.getNumCards(); x++)
		{
			
			Card.drawSide(window, 3, x*10+35, 99, 66);
		}
	}
	
	
}