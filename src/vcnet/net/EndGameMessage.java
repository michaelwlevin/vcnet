package vcnet.net;

public class EndGameMessage extends NetMessage
{
	private String[] placings;
	
	public EndGameMessage(String[] p)
	{
		placings=p;
	}
	
	public String[] getPlacings()
	{
		return placings;
	}
	
	public byte getType()
	{
		return NetMessage.END_GAME;
	}
}