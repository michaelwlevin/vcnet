package vcnet.net;

public class JoinSuccessMessage extends JoinMessage
{
	private boolean success;

	public JoinSuccessMessage(boolean s, String n)
	{
		super(n, 0);
		success=s;
	}

	public boolean isSuccessful()
	{
		return success;
	}

	public byte getType()
	{
		return NetMessage.JOIN_SUCCESS;
	}

}