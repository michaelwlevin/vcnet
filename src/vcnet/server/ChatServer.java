package vcnet.server;

import java.io.*;
import java.net.*;
import java.util.*;
import vcnet.net.ChatMessage;

public class ChatServer extends Server
{
	private int port;

	private HashMap<String, Connection> connections;

	public ChatServer(int p)
	{
		super(null, p);

		port=p;
		connections=new HashMap<String, Connection>();

		start();
	}
	public boolean checkExit()
	{
		return false;
	}
	public boolean checkStart()
	{
		return false;
	}
	public boolean addConnection(String n, Connection c)
	{
		if(!connections.containsKey(n))
		{
			connections.put(n, c);
			return true;
		}
		else
		{
			return false;
		}
	}
	public Connection getConnection(String n)
	{
		return connections.get(n);
	}
	public void sendToAll(ChatMessage msg)
	{
		for(String x:connections.keySet())
		{
			connections.get(x).send(msg);
		}
	}


}