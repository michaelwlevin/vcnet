package vcnet.mech;
import vcnet.mech.UniformCardPlay;
import java.util.*;

public class TriplePlay extends UniformCardPlay
{
	
	public TriplePlay(Card c1, Card c2, Card c3)
	{
		addCard(c1);
		addCard(c2);
		addCard(c3);
	}
	public TriplePlay(List<Card> cards)
	{
		super(cards);
	}
	
	public boolean verify()
	{
		return getNumCards()==3 && getCard(0).getIntValue()==getCard(1).getIntValue() && getCard(0).getIntValue()==getCard(2).getIntValue();
	}
}