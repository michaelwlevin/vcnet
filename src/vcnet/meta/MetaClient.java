package vcnet.meta;

import java.io.*;
import java.net.*;
import java.util.*;
import vcnet.net.*;
import vcnet.bot.BotClient;
import vcnet.bot.DefaultBotClient;
import vcnet.gui.ScoreDisplay;

public class MetaClient extends MessageHandler
{
	public static void main(String[] args) throws IOException
	{
		new MetaClient(new Socket("0.0.0.0", 3000));
	}

	private int loc=0;

	private ArrayList<String> records;

	private BotClient bot;

	private ScoreDisplay scores;

	private String name;

	private ArrayList<String> usedNames;

	public MetaClient(Socket s)
	{
		super(s);

		start();

		records=new ArrayList<String>();

		usedNames=new ArrayList<String>();

		System.out.println("[CONNECT] Connected to "+s.getInetAddress()+" on port "+s.getPort());

		tryJoin();

	}

	public void tryJoin()
	{
		String n=getUsername(usedNames);

		System.out.println("[CONNECT] Joining as \""+n+"\"");
		send(new JoinMessage(n, 0));
	}

	public BotClient instantiate(String ip, int port) throws IOException
	{

		return new DefaultBotClient(ip, port);
	}
	public void handleConnect(ConnectMessage msg)
	{
		if(bot!=null)
		{
			for(String x:bot.getRecords())
			{
				records.add(x);
			}
		}

		try
		{
			System.out.println("[START] Instantiating BotClient.");
			bot=instantiate(msg.getIP(), msg.getPort());
			System.out.println("[START] Created "+bot.getClass().getName());
			System.out.println("[CONNECT] BotClient connect: "+msg.getIP()+" on port "+msg.getPort());
		}
		catch(IOException e)
		{
			System.err.println(e);
		}
	}

	public void save(File f) throws IOException
	{

		PrintWriter fileout=new PrintWriter(f);

		for(String x:records)
		{
			fileout.println(x);
		}
		fileout.close();

		System.out.println("[FILE] Saved records");
	}

	public void handleScores(ScoresMessage msg)
	{
		if(scores==null)
		{
			scores=new ScoreDisplay(msg.getScores().size());
		}

		scores.refresh(msg.getScores());
	}

	public void handleJoinSuccess(JoinSuccessMessage msg)
	{
		if(msg.isSuccessful())
		{
			name=msg.getName();
			usedNames=null;

			System.out.println("[CONNECT] Joined as \""+name+"\"");
		}
		else
		{
			usedNames.add(msg.getName());

			try
			{
				Thread.sleep(250);
			}
			catch(InterruptedException e){}

			tryJoin();
		}
	}

	public String getName()
	{
		return name;
	}

	public String getUsername(List<String> unusable)
	{
		return "test"+(loc++);
	}

}