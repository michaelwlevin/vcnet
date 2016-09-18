package vcnet.net;

public class ServerListing implements Comparable<ServerListing>, java.io.Serializable
{
	private String name;
	private byte numPlayers;
	private long id;
	private boolean hasPass;

	public ServerListing(String n, byte p, long i, boolean h)
	{
		name=n;
		numPlayers=p;
		id=i;
		hasPass=h;
	}

	public boolean equals(Object o)
	{
		try
		{
			return ((ServerListing)o).getName().equals(getName());
		}
		catch(ClassCastException e)
		{
			return false;
		}
	}

	public boolean hasPassword()
	{
		return hasPass;
	}
	public String getName()
	{
		return name;
	}
	public byte getNumPlayers()
	{
		return numPlayers;
	}
	public long getID()
	{
		return id;
	}
	public String toString()
	{
		return getName()+" ("+getNumPlayers()+")";
	}
	public int hashCode()
	{
		return ((Long)getID()).hashCode();
	}
	public int compareTo(ServerListing rhs)
	{
		return getName().compareTo(rhs.getName());
	}

	public void setName(String n)
	{
		name=n;
	}
	public void setNumPlayers(byte b)
	{
		numPlayers=b;
	}
	public void setID(long l)
	{
		id=l;
	}
}