package vcnet.client;

import java.net.*;
import vcnet.net.JoinMessage;

public class Client2 extends Client
{
	public Client2(Socket s, String n, String p, long id)
	{
		super(s);

		send(new JoinMessage(n, p, id));
	}
	public void login()
	{
		// do nothing
	}
}