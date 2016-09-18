package vcnet.server;

import java.net.*;
import vcnet.net.JoinMessage;

public class Connection3 extends Connection
{
	private MultiServer multi;

	public Connection3(Socket s, MultiServer se)
	{
		super(s, null);
		multi=se;
	}
	public void handleJoin(JoinMessage msg)
	{
		multi.joinServer(msg.getName(), msg.getPassword(), msg.getID(), this);

	}
	public void setServer(Server s)
	{
		super.setServer(s);
		multi=null;
	}
}