package vcnet.player;

import java.util.*;
import vcnet.mech.Card;

public class BasicPlayer extends AbstractPlayer
{
	private int numCards;
	
	
	public BasicPlayer(String n)
	{
		this(n, 13);
	}
	public BasicPlayer(String n, int nu)
	{
		super(n);
		setNumCards(nu);
	}
	
	public void setNumCards(int n)
	{

		if(n<0)
		{
			n=0;
		}
		numCards=n;
	}
	
	public void clearCards()
	{
		setNumCards(0);
	}
	
	public int getNumCards()
	{
		return numCards;
	}
	
	public void playedCards(ArrayList<Card> cards)
	{
		setNumCards(getNumCards()-cards.size());
	}
}