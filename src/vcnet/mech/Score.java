package vcnet.mech;

public class Score implements java.io.Serializable, Comparable<Score>
{
	private String name;
	
	private long score;
	
	public Score(String n, long s)
	{
		setName(n);
		setScore(s);
	}
	
	public void setName(String n)
	{
		name=n;
	}
	public void setScore(long s)
	{
		score=s;
	}
	
	public long getScore()
	{
		return score;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int compareTo(Score rhs)
	{
		if(rhs.getScore()!=getScore())
		{
			return (int)(rhs.getScore()-getScore());
		}
		else
		{
			return getName().compareTo(rhs.getName());
		}
		
	}
	public boolean equals(Object o)
	{
		try
		{
			return compareTo((Score)o)==0;
		}
		catch(ClassCastException e)
		{
			return false;
		}
	}
}