package vcnet.meta;

import java.net.*;
import vcnet.net.JoinMessage;
import vcnet.net.JoinSuccessMessage;
import vcnet.net.MessageHandler;

public class MetaConnection extends MessageHandler
{
	private MetaServer server;

	public MetaConnection(Socket s, MetaServer se)
	{
		super(s, true);

		server=se;

		start();
	}
	public void handleJoin(JoinMessage msg)
	{
		if(server.setName(msg.getName(), this))
		{
			send(new JoinSuccessMessage(true, msg.getName()));
		}
		else
		{
			send(new JoinSuccessMessage(false, msg.getName()));
		}
	}

	public MetaServer getServer()
	{
		return server;
	}
	public void disconnect()
	{
		super.disconnect();

		server.removeConnection(this);
	}
}