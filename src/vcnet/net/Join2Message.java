package vcnet.net;

public class Join2Message extends JoinMessage
{

	public Join2Message(String n, long i)
	{
		super(n,i);
	}

	public byte getType()
	{
		return NetMessage.JOIN2;
	}


}