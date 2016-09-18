package vcnet.mech;
import vcnet.mech.UniformCardPlay;
import java.util.*;

// 4 of a kind

public class BreakPlay extends UniformCardPlay
{
	public BreakPlay(Card c1, Card c2, Card c3, Card c4)
	{
		addCard(c1);
		addCard(c2);
		addCard(c3);
		addCard(c4);
	}
	public BreakPlay(List<Card> cards)
	{
		super(cards);
	}
	
	public boolean playable(CardPlay rhs, RuleList rules)
	{
		if(rhs instanceof BreakPlay)
		{
			return getHighestCard().compareTo(rhs.getHighestCard())>0;
		}
		
		
		if(rules.universalBreaks())
		{
			return true;
		}
		
		return (rhs instanceof UniformCardPlay) && ((UniformCardPlay)rhs).getStrValue().equals("2");
	}
	
	public boolean verify()
	{
		if(getNumCards()!=4)
		{
			return false;
		}
		
		for(byte x=1; x<4; x++)
		{
			if(getCard(x).getIntValue()!=getCard(x-1).getIntValue())
			{
				return false;
			}
		}
		return true;
	}
	
}