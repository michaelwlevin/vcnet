package vcnet.mech;
import vcnet.mech.UniformCardPlay;
import java.util.*;

public class DoublePlay extends UniformCardPlay
{
	public DoublePlay(Card c1, Card c2)
	{
		addCard(c1);
		addCard(c2);
	}
	public DoublePlay(List<Card> cards)
	{
		super(cards);
	}
	
	public boolean verify()
	{
		return getNumCards()==2 && getCard(0).getIntValue()==getCard(1).getIntValue();
	}
}