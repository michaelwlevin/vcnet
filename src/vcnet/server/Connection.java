package vcnet.server;

import java.io.*;
import java.net.*;
import vcnet.server.Server;
import vcnet.mech.CardPlay;
import vcnet.net.ChatMessage;
import vcnet.net.JoinMessage;
import vcnet.net.MessageHandler;
import vcnet.net.PassMessage;
import vcnet.net.PlayCardMessage;
import vcnet.net.PopupMessage;

public class Connection extends MessageHandler
{
	private Server server;

	private String name;

	public Connection(Socket socket, Server s)
	{
		super(socket, true);
		server=s;

		start();
	}

	public Server getServer()
	{
		return server;
	}
	public void setServer(Server s)
	{
		server=s;
	}
	public void setName(String n)
	{
		name=n;
	}

	public String getName()
	{
		return name;
	}

	public void disconnect()
	{
		super.disconnect();

		if(server!=null)
		{
			server.removeConnection(this);
		}
	}

	public void handleChat(ChatMessage msg)
	{
		if(msg.getFrom().equals(getName()))
		{
			server.sendToAll(msg);
		}
	}

	public void handlePlayCard(PlayCardMessage msg)
	{
		if(!msg.getPlayer().equals(getName()))
		{
			send(new PopupMessage("Error", "Names do not match."));
		}
		server.play(CardPlay.getCardPlay(msg.getCards()), this);
	}
	public void handlePass(PassMessage msg)
	{
		if(!msg.getPlayer().equals(getName()))
		{
			send(new PopupMessage("Error", "Names do not match."));
		}
		server.pass(this);
	}
	public void handleJoin(JoinMessage msg)
	{
		server.addConnection(msg.getName(), this);
	}
}