package vcnet.net;

public class UserListMessage extends NetMessage
{
	private boolean add;
	private String name;

	public UserListMessage(String n, boolean a)
	{
		name=n;
		add=a;
	}

	public byte getType()
	{
		return NetMessage.USER_LIST;
	}

	public String getName()
	{
		return name;
	}
	public boolean getAdd()
	{
		return add;
	}
}