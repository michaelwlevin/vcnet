package vcnet.net;

public class ControlMessage extends TurnChangeMessage
{
	
	public ControlMessage(String p)
	{
		super(p);
	}
	
	public byte getType()
	{
		return NetMessage.CONTROL;
	}
}