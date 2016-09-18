package vcnet.server;

import vcnet.net.ServerListing;
import vcnet.mech.RuleList;

public class SubServer extends Server
{
	private String name;
	private MultiServer multi;
	private long id;
	private String password;

	public SubServer(String n, String p, long i, RuleList r, MultiServer m)
	{
		super(r, 0);
		name=n;
		password=p;
		multi=m;
		id=i;
	}

	public void run()
	{
		//do nothing
	}
	/*
	public boolean checkExit()
	{
		return false;
	}
	*/
	public void exit()
	{
		multi.removeServer(this);
		stop();
	}
	public String getName()
	{
		return name;
	}
	public String getPassword()
	{
		return password;
	}
	public long getID()
	{
		return id;
	}
	public ServerListing getServerListing()
	{
		return new ServerListing(getName(), getNumPlayers(), getID(), !password.equals(""));
	}
	public boolean addConnection(String x, Connection c)
	{
		boolean output;

		if((output=super.addConnection(x, c)))
		{
			multi.update(this);
		}

		return output;
	}
	public void removeConnection(Connection c)
	{
		super.removeConnection(c);
		multi.update(this);
	}
	public void showServerStartup()
	{
		// do nothing
	}
}