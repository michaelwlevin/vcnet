package vcnet.mech;
import java.util.*;

public abstract class UniformCardPlay extends CardPlay
{
	public UniformCardPlay()
	{
		super();
	}
	public UniformCardPlay(List<Card> cards)
	{
		super(cards);
	}
	
	public boolean playable(CardPlay rhs, RuleList rules)
	{
		if(rhs instanceof UniformCardPlay)
		{
			return getNumCards()==rhs.getNumCards() && getHighestCard().compareTo(rhs.getHighestCard())>0;
		}
		return false;
	}
	
	public int getIntValue()
	{
		return getHighestCard().getIntValue();
	}
	public String getStrValue()
	{
		return getHighestCard().getStrValue();
	}
}