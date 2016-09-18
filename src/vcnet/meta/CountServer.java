package vcnet.meta;
import java.util.*;

import vcnet.mech.RuleList;
import vcnet.server.Server;

public class CountServer extends Server
{
	private volatile int cnt;

	private int numGames;

	private MetaServer server;



	public CountServer(RuleList rules, int port, int n, MetaServer s)
	{
		super(rules, port);

		numGames=n;

		server=s;

		setTime(60); // each player has 1 minute to move.
	}

	public void endGame()
	{
		server.addPlacings(new String[]{getPlace((byte)1), getPlace((byte)2), getPlace((byte)3), getPlace((byte)4)});
		super.endGame();
	}

	public boolean checkExit()
	{
		return false;
	}

	public boolean checkRestart()
	{
		if(cnt<numGames)
		{
			cnt++;
			return true;
		}
		else
		{

			server.serverFinished();
			disconnectAll();
			return false;
		}
	}
	public void showServerStartup()
	{
		// override do nothing
	}


}