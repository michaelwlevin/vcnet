package vcnet.gui;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import vcnet.mech.Score;

public class ScoreDisplay extends JPanel implements Scrollable
{
	private TreeSet<Score> scores;

	private int size;

	public ScoreDisplay(int s)
	{

		size=s;

		scores=new TreeSet<Score>();

		setPreferredSize(new Dimension(200, size*20+60));

		addMouseListener(new MouseListener()
		{
			public void mouseEntered(MouseEvent e)
			{
				repaint();
			}
			public void mouseExited(MouseEvent e){}
			public void mousePressed(MouseEvent e){}
			public void mouseReleased(MouseEvent e){}
			public void mouseClicked(MouseEvent e){}
		});
	}

	public void refresh(final Map<String, Long> map)
	{

		Thread t=new Thread(new Runnable()
		{
			public void run()
			{
				TreeSet<Score> set=new TreeSet<Score>();

				for(String x:map.keySet())
				{
					set.add(new Score(x, map.get(x)));
				}
				scores=set;
				repaint();
			}
		}, "ScoreCopier");
		t.start();



	}

	public void paint(Graphics window)
	{
		window.setColor(getBackground());
		window.fillRect(0, 0, getWidth(), getHeight());

		window.setColor(Color.black);


		window.drawLine(38, 0, 38, getHeight());
		window.drawLine(148, 0, 148, getHeight());

		window.setFont(new Font("Tahoma", 1, 12));

		window.setColor(new Color(0, 0, 200));

		window.drawString("Rank", 3, 20);
		window.drawString("Name", 43, 20);
		window.drawString("Score", 153, 20);


		window.setFont(new Font("Tahoma", 0, 12));

		synchronized(scores)
		{
			int rank=1;

			int diff=1;

			long score=-1;

			int loc=1;

			for(Score x:scores)
			{
				if(score>=0)
				{
					if(x.getScore()==score)
					{
						diff++;
					}
					else
					{
						diff=1;
						rank+=diff;
					}
				}
				else
				{
					score=x.getScore();
				}

				window.drawString(""+rank, 5, 30+loc*20);
				window.drawString(x.getName(), 40, 30+loc*20);
				window.drawString(""+x.getScore(), 150, 30+loc*20);

				loc++;
			}
		}
	}

	public Dimension getPreferredScrollableViewportSize()
	{
		if(getHeight()<400)
		{
			return new Dimension(200, size*20+60);
		}
		else
		{
			return new Dimension(200, 400);
		}
	}
	public int getScrollableUnitIncrement(Rectangle r, int x, int y)
	{
		return 20;
	}
	public int getScrollableBlockIncrement(Rectangle r, int x, int y)
	{
		return 20;
	}

	public boolean getScrollableTracksViewportWidth()
	{
		return false;
	}

	public boolean getScrollableTracksViewportHeight()
	{
		return false;
	}
}