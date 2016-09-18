package vcnet.player;

import java.util.*;
import vcnet.mech.Card;
import vcnet.mech.CardPlay;

public abstract class AbstractPlayer
{
	private String name;
	
	private byte rank;
	
	public AbstractPlayer(String n)
	{
		setName(n);
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		name=n;
	}
	
	public void setRank(byte b)
	{
		rank=b;
	}
	public byte getRank()
	{
		return rank;
	}
	
	public abstract int getNumCards();
	public abstract void playedCards(ArrayList<Card> cards);
	public abstract void clearCards();
	
	public void playedCards(CardPlay cards)
	{
		playedCards(cards.getCards());
	}
}