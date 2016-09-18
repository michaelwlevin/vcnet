package vcnet.net;

public class TextMessage extends NetMessage
{
	private String text;
	
	public TextMessage(String t)
	{
		text=t;
	}
	
	public String getText()
	{
		return text;
	}
	
	public byte getType()
	{
		return NetMessage.TEXT;
	}
}