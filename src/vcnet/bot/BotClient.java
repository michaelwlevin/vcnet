package vcnet.bot;

import java.util.*;
import java.io.*;
import graphicutils.MessageBox;
import vcnet.mech.Card;
import vcnet.mech.CardPlay;
import vcnet.mech.RuleList;
import vcnet.net.CardPlayMessage;
import vcnet.net.ChatMessage;
import vcnet.net.ControlMessage;
import vcnet.net.EndGameMessage;
import vcnet.net.JoinMessage;
import vcnet.net.JoinSuccessMessage;
import vcnet.net.MessageHandler;
import vcnet.net.PassMessage;
import vcnet.net.PlayCardMessage;
import vcnet.net.PlayerInfoMessage;
import vcnet.net.PlayerOutMessage;
import vcnet.net.TurnChangeMessage;
import vcnet.player.AbstractPlayer;
import vcnet.player.BasicPlayer;
import vcnet.player.ClientPlayer;

public abstract class BotClient extends MessageHandler
{
	private static boolean delay=true;
	
	public static boolean useDelay()
	{
		return delay;
	}
	public static void setUseDelay(boolean d)
	{
		delay=d;
	}
	
	private RuleList rules;

	private ClientPlayer player;

	private ArrayList<AbstractPlayer> players;

	private HashMap<Byte, String> rankings;

	private ArrayList<CardPlay> lastPlayed;

	private ArrayList<String> usedNames;

	private String name;

	private String turn;

	private boolean hasControl;

	private ArrayList<String> messages;


	public BotClient(String ip, int port) throws IOException
	{
		super(ip, port);

		players=new ArrayList<AbstractPlayer>();
		rankings=new HashMap<Byte, String>();



		start();


		try
		{
			Thread.sleep(125);
		}
		catch(InterruptedException e){}

		usedNames=new ArrayList<String>();
		showBotStart();

		send(new JoinMessage(getUsername(usedNames), 0));
	}
	public void showBotStart()
	{
		new MessageBox(getClass().getName(), "Press to exit")
		{
			public void close()
			{
				System.exit(1);
			}
		};
	}

	public void setName(String n)
	{
		send(new JoinMessage(n, 0));
	}

	public void save(File file) throws IOException
	{
		PrintWriter fileout=new PrintWriter(file);

		for(String x:messages)
		{
			fileout.println(x);
		}

		fileout.close();
	}

	public void addGameMsg(String msg)
	{
		messages.add(msg);
	}

	public ArrayList<String> getRecords()
	{
		return messages;
	}
	public RuleList getRules()
	{
		return rules;
	}

	public String getRank(int i)
	{
		return rankings.get((byte)i);
	}

	public ArrayList<CardPlay> getLastPlayedList()
	{
		return lastPlayed;
	}
	public CardPlay getLastPlay()
	{
		try
		{
			synchronized(lastPlayed)
			{
				return lastPlayed.get(lastPlayed.size()-1);
			}
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public ArrayList<Card> getCards()
	{
		return player.getCards();
	}

	public AbstractPlayer getPlayerByName(String name)
	{
		synchronized(players)
		{
			for(AbstractPlayer p:players)
			{
				if(p.getName().equals(name))
				{
					return p;
				}
			}
		}
		return null;
	}

	public String getName()
	{
		return name;
	}


	public abstract CardPlay myTurn();
	public abstract CardPlay myControl();
	public abstract String getUsername(List<String> unusable);

	public void startGame(){}
	public void endGame(){}
	public void playerPassed(String name){}
	public void playerHasControl(String name){}
	public void playerPlayedCards(String name, CardPlay cards){}
	public void I_PlayedCards(CardPlay c){}
	public void playerOut(String player, int rank){}
	public void ImOut(int rank){}

	public void joinSuccess(){}
	public void receiveChat(String msg){}

	public void delay()
	{
		if(useDelay())
		{
		
			try
			{
				Thread.sleep(3000);
			}
			catch(InterruptedException e){}
		}
	}
	public void playCards(CardPlay cards)
	{
		send(new PlayCardMessage(cards.getCards(), name));
		//System.out.println(cards.getCards());
	}
	public void pass()
	{
		send(new PassMessage(name));
	}


	public void sendChat(String msg)
	{
		send(new ChatMessage(name, msg));
	}

	public Card getLowestCard()
	{
		try
		{
			return getCards().get(0);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			return null;
		}
	}
	public Card getHighestCard()
	{
		try
		{
			return getCards().get(getNumCards()-1);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
			return null;
		}
	}

	public int getNumCards()
	{
		return player.getNumCards();
	}

	public void handleJoinSuccess(JoinSuccessMessage msg)
	{
		if(msg.isSuccessful())
		{
			name=msg.getName();
			usedNames=null;
		}
		else
		{
			usedNames.add(msg.getName());

			try
			{
				Thread.sleep(250);
			}
			catch(InterruptedException e){}

			send(new JoinMessage(getUsername(usedNames), 0));
		}
	}
	public void handleChatMessage(ChatMessage msg)
	{
		if(!msg.getFrom().equals(name))
		{
			receiveChat(msg.getMessage());
		}
	}
	public void handleControl(ControlMessage msg)
	{
		turn=msg.getPlayer();

		lastPlayed.clear();

		if(turn.equals(name))
		{
			CardPlay c=null;

			do
			{
				c=myControl();
			}
			while(c==null || !c.verify());

			playCards(c);
		}
		else
		{
			playerHasControl(turn);
		}
	}
	public void handleTurnChange(TurnChangeMessage msg)
	{
		turn=msg.getPlayer();

		if(turn.equals(name))
		{
			CardPlay c=myTurn();

			if(c==null || !c.verify() || !c.playable(getLastPlay(), getRules()))
			{
				pass();
			}
			else
			{
				playCards(c);
			}
		}
	}
	public void handleCardPlay(CardPlayMessage msg)
	{
		lastPlayed.add(msg.getCardPlay());

		getPlayerByName(msg.getPlayer()).playedCards(msg.getCardPlay().getCards());

		if(!msg.getPlayer().equals(name))
		{
			playerPlayedCards(msg.getPlayer(), msg.getCardPlay());
		}
		else
		{
			I_PlayedCards(msg.getCardPlay());
		}
	}
	public void handlePass(PassMessage msg)
	{
		if(!msg.getPlayer().equals(name))
		{
			playerPassed(msg.getPlayer());
		}
	}
	public void handleEndGame(EndGameMessage msg)
	{
		for(byte x=0; x<msg.getPlacings().length; x++)
		{
			rankings.put((byte)(x+1), msg.getPlacings()[x]);
		}

		endGame();
	}
	public void handlePlayerInfo(PlayerInfoMessage msg)
	{
		players.clear();

		Collections.sort(msg.getCards());

		player=new ClientPlayer(name, msg.getCards());
		players.add(player);

		rules=msg.getRules();

		players.add(new BasicPlayer(msg.getPlayer2()));
		players.add(new BasicPlayer(msg.getPlayer3()));
		players.add(new BasicPlayer(msg.getPlayer4()));

		lastPlayed=new ArrayList<CardPlay>();

		rankings.clear();

		startGame();
	}
	public void handlePlayerOut(PlayerOutMessage msg)
	{
		if(msg.getPlayer().equals(getName()))
		{
			ImOut(msg.getRank());
		}
		else
		{
			playerOut(msg.getPlayer(), msg.getRank());
		}
	}
}