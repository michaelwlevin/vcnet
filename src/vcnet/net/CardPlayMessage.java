package vcnet.net;

import vcnet.mech.CardPlay;

public class CardPlayMessage extends PassMessage
{
	private CardPlay cards;
	
	public CardPlayMessage(String p, CardPlay c)
	{
		super(p);
		cards=c;
	}

	public CardPlay getCardPlay()
	{
		return cards;
	}
	
	public byte getType()
	{
		return NetMessage.CARDPLAY;
	}
}