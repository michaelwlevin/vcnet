package vcnet.mech;

public class RuleList implements java.io.Serializable
{
	private boolean[] rules;
	
	public RuleList()
	{
		rules=new boolean[4];
	}
	public RuleList(boolean[] r)
	{
		rules=r;
	}
	
	public boolean locksUsed()
	{
		return rules[0];
	}
	public void setLocksUsed(boolean y)
	{
		rules[0]=y;
	}
	
	public boolean universalBreaks()
	{
		return rules[1];
	}
	public void setUniversalBreaks(boolean y)
	{
		rules[1]=y;
	}
	
	public boolean winnerStarts()
	{
		return rules[2];
	}
	public void setWinnerStarts(boolean y)
	{
		rules[2]=y;
	}
	
	public boolean playAfterPass()
	{
		return rules[3];
	}
	public void setPlayAfterPass(boolean y)
	{
		rules[3]=y;
	}
}