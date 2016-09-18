package vcnet.net;

public abstract class TurnMessage extends NetMessage
{
	private String player;
	
	public TurnMessage(String p)
	{
		player=p;
	}
	
	public String getPlayer()
	{
		return player;
	}
}