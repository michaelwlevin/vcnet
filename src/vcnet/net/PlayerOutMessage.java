package vcnet.net;

public class PlayerOutMessage extends TurnMessage
{
	private byte rank;
	
	public PlayerOutMessage(String p, byte r)
	{
		super(p);
		rank=r;
	}
	
	public byte getRank()
	{
		return rank;
	}
	
	public byte getType()
	{
		return NetMessage.PLAYER_OUT;
	}
	
	
}