package vcnet.net;

public class ServerListMessage extends NetMessage
{
	public byte getType()
	{
		return NetMessage.SERVER_LIST;

	}

	private ServerListing listing;

	public ServerListMessage(ServerListing l)
	{
		listing=l;
	}

	public ServerListing getListing()
	{
		return listing;
	}
}