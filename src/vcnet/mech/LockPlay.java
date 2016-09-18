package vcnet.mech;

import java.util.*;

public class LockPlay extends StraightPlay
{
	public LockPlay(List<Card> cards)
	{
		super(cards);
	}
	
	public boolean verify()
	{
		if(getNumCards()<3)
		{
			return false;
		}
		
		
		
		
		for(byte x=1; x<getNumCards(); x++)
		{
			if(getCard(x).getIntValue()!=(getCard(x-1).getIntValue()+1) || getCard(x).getStrValue().equals("2") || getCard(x).getIntSuit()!=getCard(x-1).getIntSuit())
			{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean playable(CardPlay rhs, RuleList rules)
	{
		
		if(rhs instanceof StraightPlay)
		{
			if(rhs.getNumCards()!=getNumCards())
			{
				return false;
			}
			
			return getHighestCard().compareTo(rhs.getHighestCard())>0;
		}
		return false;
	}
}