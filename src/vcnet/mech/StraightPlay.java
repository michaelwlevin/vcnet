package vcnet.mech;

import java.util.*;

public class StraightPlay extends CardPlay
{
	public StraightPlay(List<Card> c)
	{
		super(c);
	}
	
	public boolean playable(CardPlay rhs, RuleList rules)
	{
		
		if(rhs instanceof StraightPlay)
		{
			if(rhs.getNumCards()!=getNumCards())
			{
				return false;
			}
			if(rhs instanceof LockPlay && rules.locksUsed())
			{
				return false;
			}
			
			
			return getHighestCard().compareTo(rhs.getHighestCard())>0;
		}
		return false;
	}
	
	public boolean verify()
	{
		
		if(getNumCards()<3)
		{
			return false;
		}
		
		
		for(byte x=1; x<getNumCards(); x++)
		{
			if(getCard(x).getIntValue()!=(getCard(x-1).getIntValue()+1) || getCard(x).getStrValue().equals("2"))
			{
				
				return false;
			}
		}
		
		return true;
		
	}
}