package vcnet.net;

public class TurnChangeMessage extends TurnMessage
{
	public TurnChangeMessage(String p)
	{
		super(p);
	}
	
	public byte getType()
	{
		return NetMessage.TURN_CHANGE;
	}
}