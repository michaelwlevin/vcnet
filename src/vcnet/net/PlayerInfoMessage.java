package vcnet.net;
import java.util.ArrayList;

import vcnet.mech.Card;
import vcnet.mech.RuleList;

public class PlayerInfoMessage extends NetMessage
{
	private String p2, p3, p4;
	
	private RuleList rules;
	
	private ArrayList<Card> cards;
	
	public PlayerInfoMessage(RuleList r, ArrayList<Card> c, String p2, String p3, String p4)
	{
		this.p2=p2;
		this.p3=p3;
		this.p4=p4;
		
		rules=r;
		cards=c;
	}
	
	public String getPlayer2()
	{
		return p2;
	}
	public String getPlayer3()
	{
		return p3;
	}
	public String getPlayer4()
	{
		return p4;
	}
	public RuleList getRules()
	{
		return rules;
	}
	public ArrayList<Card> getCards()
	{
		return cards;
	}
	
	public byte getType()
	{
		return NetMessage.PLAYER_INFO;
	}
	
}