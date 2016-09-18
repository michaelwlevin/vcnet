package vcnet.player;

import java.util.*;
import vcnet.mech.Card;

public class ClientPlayer extends AbstractPlayer
{
	private ArrayList<Card> cards;
	
	private boolean passed;
	
	
	public ClientPlayer(String n, List<Card> c)
	{
		super(n);
		
		cards=new ArrayList<Card>(c);
		
		Collections.sort(cards);
	}
	
	public int getNumCards()
	{
		return cards.size();
	}
	
	public void playedCards(ArrayList<Card> cards2)
	{
		for(Card c:cards2)
		{
			cards.remove(c);
		}
	}
	
	public ArrayList<Card> getCards()
	{
		return cards;
	}
	
	public boolean hasCard(Card c)
	{
		return cards.contains(c);
	}
	public void removeCard(Card c)
	{
		cards.remove(c);
	}
	
	public void clearCards()
	{
		cards.clear();
	}
	
	
	public void setPassed(boolean p)
	{
		passed=p;
	}
	public boolean getPassed()
	{
		return passed;
	}

}