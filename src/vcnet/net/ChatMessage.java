package vcnet.net;

public class ChatMessage extends NetMessage
{
	private String from;
	private String msg;
	
	public ChatMessage(String f, String m)
	{
		from=f;
		msg=m;
	}
	
	public String getFrom()
	{
		return from;
	}
	public String getMessage()
	{
		return msg;
	}
	
	public byte getType()
	{
		return NetMessage.CHAT;
	}
}