package vcnet.net;

public class PrivateMessage extends ChatMessage
{
	public byte getType()
	{
		return NetMessage.PM;
	}

	private String to;

	public PrivateMessage(String t, String f, String m)
	{
		super(f, m);
		to=t;
	}

	public String getTo()
	{
		return to;
	}
}