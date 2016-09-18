package vcnet.net;

public class ServerConnectMessage extends NetMessage
{
	public byte getType()
	{
		return NetMessage.SERVER_CONNECT;
	}

	private long id;
	private int port;
	private String password;

	public ServerConnectMessage(long i, int p, String w)
	{
		port=p;
		id=i;
		password=w;
	}

	public String getPassword()
	{
		return password;
	}
	public long getID()
	{
		return id;
	}
	public int getPort()
	{
		return port;
	}
}