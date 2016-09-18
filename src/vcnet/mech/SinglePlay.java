package vcnet.mech;
import vcnet.mech.UniformCardPlay;
import java.util.*;

public class SinglePlay extends UniformCardPlay
{
	public SinglePlay(Card c)
	{
		addCard(c);
	}
	public SinglePlay(List<Card> cards)
	{
		super(cards);
	}
	
	public boolean verify()
	{
		return getNumCards()==1;
	}
}