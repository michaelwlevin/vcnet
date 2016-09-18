package vcnet.net;

public class JoinMessage extends NetMessage
{
	private String name;
	private String password;
	private long id;

	public JoinMessage(String n, long i)
	{
		this(n, "", i);
	}
	public JoinMessage(String n, String p, long i)
	{
		name=n;
		id=i;
		password=p;
	}

	public byte getType()
	{
		return NetMessage.JOIN;
	}

	public String getPassword()
	{
		return password;
	}
	public String getName()
	{
		return name;
	}
	public long getID()
	{
		return id;
	}
}