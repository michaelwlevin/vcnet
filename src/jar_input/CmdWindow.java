package jar_input;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.util.ArrayList;

public class CmdWindow extends JTextArea
{

	private int limit;
	
	private ArrayList<String> entered;
	private int loc;
	
	private CommandListener listener;
	
	public CmdWindow()
	{
		init();
	}
	public CmdWindow(int rows, int cols)
	{
		super(rows, cols);
		init();
	}
	private void init()
	{
		
		limit = 0;
		entered = new ArrayList<String>();
		
		setNavigationFilter(new NavigationFilter()
		{
			public void setDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
			{
				if(dot < limit)
				{
					super.setDot(fb, limit, bias);
				}
				else
				{
					super.setDot(fb, dot, bias);
				}
			}
			public void moveDot(NavigationFilter.FilterBypass fb, int dot, Position.Bias bias)
			{
				if(dot < limit)
				{
					super.setDot(fb, limit, bias);
				}
				else
				{
					super.setDot(fb, dot, bias);
				}
			}
		});
		
		addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				int code = e.getKeyCode();
				
				if(code==KeyEvent.VK_ENTER)
				{
					String temp = getText().substring(limit);
					entered.add(temp);
					limit = getText().length()+1;
					setCaretPosition(limit-1);
					loc = entered.size();
					
					entered(temp);
				}
				
				else if(code == KeyEvent.VK_DOWN)
				{				
					if(loc < entered.size()-1)
					{
						loc++;
						setText(getText().substring(0, limit)+entered.get(loc));
					}
					else if(loc==entered.size()-1)
					{
						loc++;
						setText(getText().substring(0, limit));
					}
				}
				else if(code == KeyEvent.VK_UP)
				{
					if(loc > 0)
					{
						loc--;
						setText(getText().substring(0, limit)+entered.get(loc));
					}
				}
				
				
			}
		});
	}
	
	public void entered(String text)
	{
		if(listener != null)
		{
			listener.entered(text);
		}
	}
	
	public void setCommandListener(CommandListener l)
	{
		listener = l;
	}
	public void append(String text)
	{
		super.append(text);
		limit = getText().length();
		setCaretPosition(limit);
	}
}