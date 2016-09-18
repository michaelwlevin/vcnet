package vcnet.net;

public class PassMessage extends TurnMessage
{

	
	public PassMessage(String p)
	{
		super(p);
	}
	
	
	public byte getType()
	{
		return NetMessage.PASS;
	}
}