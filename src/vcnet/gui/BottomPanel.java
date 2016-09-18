package vcnet.gui;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import vcnet.player.ClientPlayer;
import vcnet.mech.Card;
import vcnet.mech.CardPlay;
import vcnet.client.Client;

public class BottomPanel extends JPanel implements MouseListener
{
	private ClientPlayer player;
	
	private ArrayList<Card> cards;
	private boolean[] selected;
	
	private VCPanel vcGui;
	
	private boolean turn;
	
	public BottomPanel(ClientPlayer p, VCPanel vc)
	{
		vcGui=vc;
		
		player=p;
		
		selected=new boolean[0];
		
		setPreferredSize(new Dimension(400, 140));
		
		addMouseListener(this);
	}
	
	public void setPlayer(ClientPlayer p)
	{
		player=p;
		
		refresh();
		
		repaint();
	}
	
	public void refresh()
	{
		if(player!=null)
		{
			cards=player.getCards();
		}
		else
		{
			cards=null;
		}
		if(cards!=null)
		{
			selected=new boolean[cards.size()];
		}
		else
		{
			selected=new boolean[0];
		}
		repaint();
		
	}
	
	public void setTurn(boolean t)
	{
		turn=t;
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
		
		window.drawString(player.getName(), 5, 15);
		
		if(player.getRank()==0)
		{
			window.drawString(""+player.getNumCards()+" card"+(player.getNumCards()!=1?"s":"")+" left", 5, 30);
		}
		else
		{
			window.drawString(Client.convertRank(player.getRank())+" place", 5, 30);
		}
		

		
		
		if(cards!=null)
		{
			for(int x=0; x<cards.size(); x++)
			{
				
				if(selected[x])
				{
					cards.get(x).draw(window, x*25+3, 20, 66, 99);
				}
				else
				{
					cards.get(x).draw(window, x*25+3, 35, 66, 99);
				}
			}
		}
	}
	
	public ArrayList<Card> getSelectedCards()
	{
		ArrayList<Card> output=new ArrayList<Card>();
		
		for(int x=0; x<cards.size(); x++)
		{
			if(selected[x])
			{
				output.add(cards.get(x));
			}
		}
		return output;
	}
	public void endTurn()
	{
		turn=false;
		for(int x=0; x<selected.length; x++)
		{
			selected[x]=false;
		}
		repaint();
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e)
	{
		repaint();
	}
	public void mouseExited(MouseEvent e){}
	
	public void mouseClicked(MouseEvent e)
	{
		if(!turn)
		{
			return;
		}
		
		int x=e.getX();
		int y=e.getY();
		
		for(int a=cards.size()-1; a>=0; a--)
		{
			if(x>a*25+3 && x<a*25+69)
			{
				if(selected[a])
				{
					if(e.getY()>20 && e.getY()<119)
					{
						selected[a]=false;
						vcGui.checkCards(getSelectedCards());
						break;
					}
				}
				else
				{
					if(e.getY()>35 && e.getY()<134)
					{
						selected[a]=true;
						vcGui.checkCards(getSelectedCards());
						break;
					}
				}
				
			}
		} 
		repaint();
	}
}