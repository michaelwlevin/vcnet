package vcnet.gui;

import graphicutils.GraphicUtils;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import vcnet.client.Client;
import graphicutils.*;
import vcnet.mech.BombPlay;
import vcnet.mech.BreakPlay;
import vcnet.mech.Card;
import vcnet.mech.CardPlay;
import vcnet.net.PassMessage;
import vcnet.net.PlayCardMessage;
import vcnet.player.AbstractPlayer;
import vcnet.player.ClientPlayer;

public class VCPanel extends JPanel
{
	private BottomPanel bottom;
	private SidePanel right;
	private SidePanel left;
	private TopPanel top;
	
	private JButton play;
	private JButton pass;
	
	private CenterPanel center;
	
	private Client client;
	
	public VCPanel(Client c)
	{
		client=c;
		
		setBackground(new Color(29, 131, 36));
		
		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());
		p.setBackground(getBackground());
		
		bottom=new BottomPanel(null, this);
		bottom.setBackground(getBackground());
		
		left=new SidePanel(null);
		left.setBackground(getBackground());
		
		right=new SidePanel(null);
		right.setBackground(getBackground());
		
		top=new TopPanel(null);
		top.setBackground(getBackground());
		
		center=new CenterPanel();
		center.setBackground(getBackground());
		
		play=new JButton("Play");
		pass=new JButton("Pass");
		
		play.setEnabled(false);
		pass.setEnabled(false);
		
		JPanel p1=new JPanel();
		p1.setLayout(new GridBagLayout());
		p1.setBackground(getBackground());
		
		GraphicUtils.constrain(p1, play, 0, 0, 1, 1);
		GraphicUtils.constrain(p1, pass, 0, 1, 1, 1);
		
		GraphicUtils.constrain(p, left, 0, 1, 1, 1);
		GraphicUtils.constrain(p, top, 1, 0, 1, 1);
		GraphicUtils.constrain(p, right, 2, 1, 1, 1);
		GraphicUtils.constrain(p, center, 1, 1, 1, 1);
		GraphicUtils.constrain(p, bottom, 1, 2, 1, 1);
		GraphicUtils.constrain(p, p1, 2, 2, 1, 1);
		
		pass.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				client.send(new PassMessage(client.getName()));
				endTurn();
			}
		});
		
		play.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				client.send(new PlayCardMessage(getBottom().getSelectedCards(), client.getName()));
				endTurn();
			}
		});
		
		
		
		
		add(p);	
	}
	
	public void repaint()
	{
		try
		{
			center.repaint();
			left.repaint();
			top.repaint();
			bottom.repaint();
			right.repaint();
		}
		catch(NullPointerException e){}
		
		super.repaint();
	}
	public String getName()
	{
		return client.getName();
	}
	
	public void setPlayers(java.util.List<AbstractPlayer> players)
	{
	
		clear();
		
		//System.out.println(players);
		try
		{
			bottom.setPlayer((ClientPlayer)players.get(0));
			left.setPlayer(players.get(1));
			top.setPlayer(players.get(2));
			right.setPlayer(players.get(3));
			
			center.setName(getName());
			
		}
		catch(IndexOutOfBoundsException e)
		{
			e.printStackTrace(System.err);
		}
		
		repaint();
	}
	
	public BottomPanel getBottom()
	{
		return bottom;
	}
	public CenterPanel getCenter()
	{
		return center;
	}
	public void checkCards(ArrayList<Card> selected)
	{
		CardPlay temp=CardPlay.getCardPlay(selected);
		
		if(temp==null)
		{
			play.setEnabled(false);
		}
		else if(getCenter().getLastPlay()==null)
		{
			play.setEnabled(true);
		}
		else
		{
			play.setEnabled(temp.playable(getCenter().getLastPlay(), client.getRules()));
			
			if(!client.getRules().winnerStarts() && client.getClientPlayer().hasCard(new Card(0, 0)) && !getBottom().getSelectedCards().contains(new Card(0, 0)))
			{
				play.setEnabled(false);
			}
			
			if(!client.getRules().playAfterPass() && client.getClientPlayer().getPassed() && !((temp instanceof BreakPlay) || (temp instanceof BombPlay)))
			{
				play.setEnabled(false);
			}
			
		}
		
	}
	public void startTurn()
	{
		pass.setEnabled(true);
		getBottom().setTurn(true);
		
		if(center.getHasControl())
		{
			pass.setEnabled(false);
		}
	}
	public void endTurn()
	{
		pass.setEnabled(false);
		play.setEnabled(false);
		getBottom().endTurn();
	}
	public void clear()
	{
		bottom.setPlayer(null);
		left.setPlayer(null);
		right.setPlayer(null);
		top.setPlayer(null);
		
		center.clear();
		
		repaint();
	}
}