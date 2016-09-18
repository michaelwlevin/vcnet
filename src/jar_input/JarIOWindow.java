package jar_input;

import java.io.*;
import javax.swing.*;
import java.util.*;

public class JarIOWindow extends JFrame
{
	public static void conditionalAdd()
	{
		conditionalAdd("Windows");
	}
	public static void conditionalAdd(String... systems)
	{
		String os = System.getProperty("os.name");
		
		boolean add = false;
	
		for(String x : systems)
		{
			if(os.indexOf(x) >= 0)
			{
				add = true;
				break;
			}
		}
		
		if(add)
		{
			new JarIOWindow();
		}
	}
		
		
	private CmdWindow window;
	private TextOutputStream output;
	private TextInputStream input;
	
	
	public JarIOWindow()
	{
		this("");
	}
	public JarIOWindow(String title)
	{
		super(title);
		
		window = new CmdWindow(18, 80);
		
		window.setBackground(java.awt.Color.black);
		window.setForeground(java.awt.Color.white);
		window.setFont(new java.awt.Font("Courier New", 1, 14));
		window.setCaretColor(java.awt.Color.white);
		
		output = new TextOutputStream(window);
		input = new TextInputStream(window);
		
		System.setOut(new PrintStream(output));
		System.setErr(new PrintStream(output));
		System.setIn(new BufferedInputStream(input));
		
		add(new JScrollPane(window));
		pack();
		setResizable(false);
		
		setExitOnClose(true);
		
		
		setVisible(true);
	}
	
	public void setExitOnClose(boolean exit)
	{
		if(exit)
		{
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
		else
		{
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}
	}
}