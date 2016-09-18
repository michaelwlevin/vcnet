package vcnet.mech;

import java.util.*;

public abstract class CardPlay implements java.io.Serializable
{
	

	
	
	public static CardPlay getCardPlay(ArrayList<Card> cards)
	{
		Collections.sort(cards);
		
		CardPlay output=null;
		
		output=new SinglePlay(cards);
		
		if(output.verify())
		{
			return output;
		}
		
		output=new DoublePlay(cards);
		
		if(output.verify())
		{
			return output;
		}
		
		output=new TriplePlay(cards);
		
		if(output.verify())
		{
			return output;
		}
		
		output=new BreakPlay(cards);
		
		if(output.verify())
		{
			return output;
		}
		
		output=new LockPlay(cards);
		
		if(output.verify())
		{
			return output;
		}
		
		output=new StraightPlay(cards);
		
		if(output.verify())
		{
			return output;
		}
		
		output=new BombPlay(cards);
		
		if(output.verify())
		{
			return output;
		}
		
		
		
		return null;
	}
	
	
	private ArrayList<Card> cards;
	
	public CardPlay()
	{
		cards=new ArrayList<Card>();
	}
	public CardPlay(List<Card> c)
	{
		cards=new ArrayList(c);
	}
	public CardPlay(Card[] c)
	{
		this(Arrays.asList(c));
	}
	
	protected void addCard(Card c)
	{
		cards.add(c);
		Collections.sort(cards);
	}
	
	public Card getHighestCard()
	{
		return cards.get(cards.size()-1);
	}
	public Card getLowestCard()
	{
		return cards.get(0);
	}
	
	public ArrayList<Card> getCards()
	{
		return cards;
	}
	
	public Card getCard(int loc)
	{
		return cards.get(loc);
	}
	
	public int getNumCards()
	{
		return cards.size();
	}
	
	public String toString()
	{
		String output=""+getName()+" [";
		
		for(Card c:getCards())
		{
			output+=c.showCard()+",";
		}	
		output=output.substring(0, output.length()-1);
		
		output+="]";
		
		return output;
	}
	
	public String getName()
	{
		String output=getClass().getName();
		
		output=output.substring(output.lastIndexOf('.')+1, output.length()-4);
		
		return output.toLowerCase();
	}
	
	public abstract boolean playable(CardPlay rhs, RuleList rules);
	
	public abstract boolean verify();
}