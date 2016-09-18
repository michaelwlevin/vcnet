package vcnet.net;

public class RemoveServerListMessage extends NetMessage
{
	public byte getType()
	{
		return NetMessage.REMOVE_SERVER_LIST;
	}
	
	private long id;
	
	public RemoveServerListMessage(long i)
	{
		id=i;
	}
	public long getID()
	{
		return id;
	}
}