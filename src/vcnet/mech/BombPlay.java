package vcnet.mech;
import java.util.*;

// consecutive doubles bomb

public class BombPlay extends CardPlay
{
	public BombPlay(List<Card> cards)
	{
		super(cards);
	}
	
	public boolean playable(CardPlay rhs, RuleList rules)
	{
		if(rhs instanceof UniformCardPlay && !(rhs instanceof BreakPlay))
		{
			if(((UniformCardPlay)rhs).getStrValue().equals("2") && getNumCards()>=(6+(rhs.getNumCards()-1)*2))
			{
				return true;
			}
			
			
		}
		else if(rhs instanceof BombPlay)
		{
			return getNumCards()>=rhs.getNumCards() && getHighestCard().compareTo(rhs.getHighestCard())>0;
		}
		return false;
	}
	
	public boolean verify()
	{
		if(getNumCards()<6 || getNumCards()%2==1)
		{
			return false;
		}
		
		for(byte x=1; x<getNumCards(); x+=2)
		{
			if(getCard(x).getIntValue()!=getCard(x-1).getIntValue() || (x<getNumCards()-2 && (getCard(x).getIntValue()!=getCard(x+1).getIntValue()-1)))
			{
				return false;
			}
		}
		return true;
	}
}