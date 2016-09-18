package vcnet.server;

import java.io.*;
import java.net.*;
import vcnet.net.ChatMessage;
import vcnet.net.JoinMessage;
import vcnet.net.PrivateMessage;
import vcnet.net.ServerConnectMessage;

public class Connection2 extends Connection
{
	private MultiServer multi;

	public Connection2(Socket socket, MultiServer s)
	{
		super(socket, null);
		multi=s;
	}




	public void disconnect()
	{
		super.disconnect();

		if(getName()!=null)
		{
			multi.removeConnection(getName());
		}
	}

	public void handleChat(ChatMessage msg)
	{
		String text=msg.getMessage().trim();
		
		if(text.length()==0)
		{
			return;
		}
		
		if(msg.getFrom().equals(getName()))
		{
			multi.sendToAll(msg);
		}
	}
	public void handlePM(PrivateMessage msg)
	{
		String text=msg.getMessage().trim();
		
		if(text.length()==0)
		{
			return;
		}
		
		if(msg.getFrom().equals(getName()))
		{
			Connection2 c=multi.getConnection(msg.getTo());
			
			if(c!=null)
			{
				c.send(msg);
				send(msg);
			}
			else
			{
				send(new ChatMessage("[SERVER]", "Cannot find user \""+msg.getTo()+"\"."));
			}
		}
	}


	public void handleJoin(JoinMessage msg)
	{
		multi.addConnection(msg.getName(), msg.getID(), this);
	}
	public void handleCreateServer(CreateServerMessage msg)
	{
		SubServer s=multi.createServer(msg.getName(), msg.getPassword(), msg.getRules());

		send(new ServerConnectMessage(s.getID(), multi.getSubServerPort(), msg.getPassword()));
	}
}