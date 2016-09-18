package jar_input;

import java.io.*;
import java.util.*;

public class TextInputStream extends InputStream
{
	private CmdWindow window;
	
	private ByteArrayInputStream input;
	
	private boolean flag;
	
	public TextInputStream(CmdWindow w)
	{
		input = new ByteArrayInputStream(new byte[0]);
		flag = false;
		
		window = w;
		
		window.setCommandListener(new CommandListener()
		{
			public void entered(String text)
			{
				update(text+"\n");
			}
		});
	}
	
	public synchronized void update(final String text)
	{
		Thread t = new Thread()
		{
			public void run()
			{
				byte[] temp = text.getBytes();
				input = new ByteArrayInputStream(temp);
				
				unblock();
			}
		};
		t.start();

	}
	
	public synchronized void unblock()
	{
		flag = true;
		notifyAll();
	}
	

	
	public CmdWindow getWindow()
	{
		return window;
	}
	
	public int available()
	{
		return input.available();
	}
	
	public synchronized int read()
	{
		
		
		while(!flag)
		{
			try
			{
				wait();
			}
			catch(InterruptedException ex){}
		}
		int temp = input.read();
		

		if(temp == -1)
		{
			flag = false;
		}
		
		
		return temp;

	}
	
	public boolean markSupported()
	{
		return false;
	}

}
