package vcnet.net;

import java.util.ArrayList;
import vcnet.mech.Card;

public class PlayCardMessage extends TurnMessage
{
	private ArrayList<Card> cards;
	
	public PlayCardMessage(ArrayList<Card> c, String n)
	{
		super(n);
		cards=c;
	}
	
	public ArrayList<Card> getCards()
	{
		return cards;
	}
	
	public byte getType()
	{
		return NetMessage.PLAY_CARDS;
	}
}