package vcnet.net;

public class PopupMessage extends NetMessage
{
	private String title;
	private String msg;
	
	public PopupMessage(String t, String m)
	{
		title=t;
		msg=m;
	}
	
	public String getTitle()
	{
		return title;
	}
	public String getMessage()
	{
		return msg;
	}
	
	public byte getType()
	{
		return NetMessage.POPUP;
	}
}