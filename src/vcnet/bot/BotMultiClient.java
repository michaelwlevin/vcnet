package vcnet.bot;

import java.io.*;
import java.net.*;
import java.util.*;
import vcnet.mech.RuleList;
import vcnet.net.ChatMessage;
import vcnet.net.JoinMessage;
import vcnet.net.JoinSuccessMessage;
import vcnet.net.MessageHandler;
import vcnet.net.PrivateMessage;
import vcnet.net.RemoveServerListMessage;
import vcnet.net.ServerConnectMessage;
import vcnet.net.ServerListMessage;
import vcnet.net.ServerListing;
import vcnet.net.UserListMessage;
import vcnet.server.CreateServerMessage;

public abstract class BotMultiClient extends MessageHandler
{

	protected TreeSet<String> users;
	protected TreeSet<ServerListing> servers;
	
	private String name;

	private int port2;
	
	private ArrayList<String> usedNames;
	

	public BotMultiClient(String ip, int port1, int p2) throws IOException
	{
		// port 2 is actual game server
		super(ip, port1);

		port2=p2;

		users=new TreeSet<String>();

		servers=new TreeSet<ServerListing>();

		start();
		
		join();

	}
	public void sendChat(String msg)
	{
		send(new ChatMessage(getName(), msg));
	}
	public void sendPM(String to, String msg)
	{
		send(new PrivateMessage(to, getName(), msg));
	}
	public String getName()
	{
		return name;
	}
	public void join()
	{
		send(new JoinMessage(getUsername(usedNames), 0));
	}
	public void handleJoinSuccess(JoinSuccessMessage msg)
	{
		if(msg.isSuccessful())
		{
			name=msg.getName();

		}
		else
		{
			join();
		}
	}
	public void handleChat(ChatMessage msg)
	{
		receivedChat(msg.getMessage(), msg.getFrom());
	}
	public void handleServerConnect(ServerConnectMessage msg)
	{
		joinServer(getIP(), msg.getPort(), getName(), msg.getID());
	}
	public void handleServerList(ServerListMessage msg)
	{
		servers.add(msg.getListing());

		refreshServerList();
	}
	public void handleRemoveServerList(RemoveServerListMessage msg)
	{
		for(ServerListing s:servers)
		{
			if(s.getID()==msg.getID())
			{
				servers.remove(s);
				break;
			}
		}
		
		refreshServerList();
	}
	public void handleUserList(UserListMessage msg)
	{
		if(msg.getAdd())
		{
			users.add(msg.getName());
		}
		else
		{
			users.remove(msg.getName());
		}
		refreshUserList();
	}
	public void handlePM(PrivateMessage msg)
	{
		if(msg.getTo().equals(getName()))
		{
			receivedPM(msg.getFrom(), msg.getMessage());
		}
	}
	public void createServer(String name, RuleList rules)
	{
		send(new CreateServerMessage(name, rules));
	}
	
	
	// must implement
	public abstract String getUsername(List<String> unusable);
	
	// must create new bot
	public abstract void joinServer(String ip, int port, String name, long id);
	
	// optional implementation
	public void receivedChat(String msg, String from){}
	public void refreshServerList(){}
	public void refreshUserList(){}
	public void receivedPM(String from, String message){}
	

}