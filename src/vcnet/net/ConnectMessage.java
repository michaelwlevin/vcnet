package vcnet.net;

public class ConnectMessage extends NetMessage
{
	private String ip;
	private int port;
	
	public ConnectMessage(String i, int p)
	{
		ip=i;
		port=p;
	
	}
	
	public byte getType()
	{
		return NetMessage.CONNECT;
	}
	
	public String getIP()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
	}
}