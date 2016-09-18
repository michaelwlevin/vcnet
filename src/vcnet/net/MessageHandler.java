package vcnet.net;

import vcnet.server.CreateServerMessage;
import java.net.*;
import java.io.*;

public class MessageHandler implements Runnable
{
	private ObjectInputStream in;
	private ObjectOutputStream out;

	private Socket socket;



	public MessageHandler(Socket s)
	{
		this(s, false);
	}
	public MessageHandler(Socket s, boolean reversed)
	{
		socket=s;

		if(reversed)
		{
			try
			{
				out=new ObjectOutputStream(socket.getOutputStream());
				in=new ObjectInputStream(socket.getInputStream());
			}
			catch(IOException e){}
		}
		else
		{
			try
			{
				in=new ObjectInputStream(socket.getInputStream());
				out=new ObjectOutputStream(socket.getOutputStream());
			}
			catch(IOException e){}
		}
	}
	public MessageHandler(String ip, int port) throws IOException
	{
		this(new Socket(ip, port));
	}


	public void start()
	{


		Thread t=new Thread(this, getClass().getName());
		t.start();
	}

	public void run()
	{


		while(true)
		{
			try
			{
				NetMessage msg=getNextMessage();

				if(msg==null)
				{
					//System.err.println("Msg is null.");
					break;
				}

				//System.out.println("Received "+msg.getClass().getName());

				handleMessage(msg);
			}
			catch(Exception e)
			{
				e.printStackTrace(System.err);
			}

		}
		disconnect();
	}
	public void disconnect()
	{

		try
		{
			out.close();
			in.close();
			socket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);
		}

		System.out.println("[CONNECT] Disconnected "+getIP());
	}

	public synchronized NetMessage getNextMessage()
	{
		try
		{
			return (NetMessage)in.readObject();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public boolean send(NetMessage msg)
	{
		//System.out.println("Sending "+msg.getClass().getName());

		try
		{
			Thread.sleep(10);
		}
		catch(InterruptedException e){}

		try
		{
			out.writeObject(msg);

			//System.out.println("Sent "+msg.getClass().getName());

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.err);

			return false;
		}
	}
	public int getPort()
	{
		return socket.getPort();
	}
	public String getIP()
	{
		String temp=socket.getInetAddress().toString();

		if(temp.indexOf("/")>=0)
		{
			temp=temp.substring(temp.indexOf("/")+1);
		}

		return temp;
	}

	public void handleMessage(NetMessage raw)
	{
		switch(raw.getType())
		{
			case NetMessage.TEXT: handleText((TextMessage)raw); break;
			case NetMessage.POPUP: handlePopup((PopupMessage)raw); break;
			case NetMessage.CARDPLAY: handleCardPlay((CardPlayMessage)raw); break;
			case NetMessage.PASS: handlePass((PassMessage)raw); break;
			case NetMessage.CONTROL: handleControl((ControlMessage)raw); break;
			case NetMessage.TURN_CHANGE: handleTurnChange((TurnChangeMessage)raw); break;
			case NetMessage.CHAT: handleChat((ChatMessage)raw); break;
			case NetMessage.JOIN: handleJoin((JoinMessage)raw); break;
			case NetMessage.JOIN_SUCCESS: handleJoinSuccess((JoinSuccessMessage)raw); break;
			case NetMessage.END_GAME: handleEndGame((EndGameMessage)raw); break;
			case NetMessage.PLAY_CARDS: handlePlayCard((PlayCardMessage)raw); break;
			case NetMessage.PLAYER_OUT: handlePlayerOut((PlayerOutMessage)raw); break;
			case NetMessage.PLAYER_INFO: handlePlayerInfo((PlayerInfoMessage)raw); break;
			case NetMessage.CONNECT: handleConnect((ConnectMessage)raw); break;
			case NetMessage.SCORES: handleScores((ScoresMessage)raw); break;
			case NetMessage.JOIN2: handleJoin2((Join2Message)raw); break;
			case NetMessage.PM: handlePM((PrivateMessage)raw); break;
			case NetMessage.USER_LIST: handleUserList((UserListMessage)raw); break;
			case NetMessage.SERVER_CONNECT: handleServerConnect((ServerConnectMessage)raw); break;
			case NetMessage.SERVER_LIST: handleServerList((ServerListMessage)raw); break;
			case NetMessage.CREATE_SERVER: handleCreateServer((CreateServerMessage)raw); break;
			case NetMessage.REMOVE_SERVER_LIST: handleRemoveServerList((RemoveServerListMessage)raw); break;
		}
	}


	public void handlePM(PrivateMessage msg){}
	public void handleJoin2(Join2Message msg){}
	public void handleText(TextMessage msg){}
	public void handlePopup(PopupMessage msg){}
	public void handleCardPlay(CardPlayMessage msg){}
	public void handlePass(PassMessage msg){}
	public void handleControl(ControlMessage msg){}
	public void handleTurnChange(TurnChangeMessage msg){}
	public void handleChat(ChatMessage msg){}
	public void handleJoin(JoinMessage msg){}
	public void handleJoinSuccess(JoinSuccessMessage msg){}
	public void handleEndGame(EndGameMessage msg){}
	public void handlePlayCard(PlayCardMessage msg){}
	public void handlePlayerOut(PlayerOutMessage msg){}
	public void handlePlayerInfo(PlayerInfoMessage msg){}
	public void handleConnect(ConnectMessage msg){}
	public void handleScores(ScoresMessage msg){}
	public void handleUserList(UserListMessage msg){}
	public void handleServerConnect(ServerConnectMessage msg){}
	public void handleServerList(ServerListMessage msg){}
	public void handleCreateServer(CreateServerMessage msg){}
	public void handleRemoveServerList(RemoveServerListMessage msg){}
}