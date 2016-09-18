package vcnet.bot;

import graphicutils.GraphicUtils;
import graphicutils.MessageBox;
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import graphicutils.*;
import vcnet.mech.Card;
import vcnet.mech.CardPlay;

public class DefaultBotClient extends BotClient
{
	public static void main(String[] args)
	{

		final JTextField ip=new JTextField(12);
		final JTextField port=new JTextField(5);

		String name="";

		try
		{
			Scanner file=new Scanner(new File("options/ConnectTo.dat"));

			ip.setText(file.nextLine().trim());
			port.setText(file.nextLine().trim());
		}
		catch(Exception e){}

		JButton join=new JButton("Join Server");

		JPanel p=new JPanel();
		p.setLayout(new GridBagLayout());

		GraphicUtils.constrain(p, new JLabel("IP: "), 0, 0, 1, 1);
		GraphicUtils.constrain(p, ip, 1, 0, 1, 1);
		GraphicUtils.constrain(p, new JLabel("Port: "), 0, 1, 1, 1);
		GraphicUtils.constrain(p, port, 1, 1, 1, 1);
		GraphicUtils.constrain(p, join, 0, 2, 2, 1, GridBagConstraints.CENTER);

		final JFrame frame=new JFrame("Join server");

		ActionListener action=new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if(ip.getText().trim().equals(""))
					{
						ip.setText("");
						ip.requestFocus();
						return;
					}

					new DefaultBotClient(ip.getText().trim(), Integer.parseInt(port.getText().trim()));

					try
					{
						PrintWriter fileout=new PrintWriter(new File("options/ConnectTo.dat"));
						fileout.println(ip.getText().trim());
						fileout.println(port.getText().trim());
						fileout.close();
					}
					catch(IOException e3){}
					frame.setVisible(false);

				}
				catch(NumberFormatException n)
				{
					port.setText("");
					port.requestFocus();
				}
				catch(IOException e2)
				{
					new MessageBox("Error", "Could not connect to server.");
					ip.setText("");
					port.setText("");
					ip.requestFocus();
				}
			}
		};

		ip.addActionListener(action);
		port.addActionListener(action);
		join.addActionListener(action);


		frame.add(p);
		frame.pack();
		frame.setResizable(false);

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(1);
			}
		});

		frame.setVisible(true);
	}

	private int loc;

	private ArrayList<Card> bombs; // will only keep 1 bomb
	private ArrayList<ArrayList<Card>> straights;
	private ArrayList<Card> triples;
	private ArrayList<Card> doubles;
	private ArrayList<Card> singles;

	private int numCards;

	private boolean has3;


	public DefaultBotClient(String ip, int port) throws IOException
	{
		super(ip, port);

		numCards=13;
	}

	public String getUsername(java.util.List<String> u)
	{
		return "bot "+(loc++);
	}

	public void I_PlayedCards(CardPlay cards)
	{
		numCards-=cards.getNumCards();

		//System.out.println(getName()+" played "+cards.getCards());
	}
	public void ImOut(int rank)
	{
		//System.out.println(getName()+" got "+rank);
	}



	public void startGame()
	{
		bombs=new ArrayList<Card>();
		straights=new ArrayList<ArrayList<Card>>();
		triples=new ArrayList<Card>();
		doubles=new ArrayList<Card>();
		singles=new ArrayList<Card>();

		numCards=13;


		sortCards();
	}

	public int getNumSelected(ArrayList<Boolean> check)
	{
		int output=0;

		for(Boolean b:check)
		{
			if(b)
			{
				output++;
			}
		}

		return output;
	}
	public void deselectAll(ArrayList<Boolean> check)
	{
		for(int x=0; x<check.size(); x++)
		{
			check.set(x, false);
		}
	}
	public void sortCards(){


		ArrayList<Card> cards=getCards();
		ArrayList<Boolean> cardsSelected=new ArrayList<Boolean>();

		if(cards.get(0).getStrValue().equals("3") && cards.get(0).getStrSuit().equals("Spades")){
			has3=true;
		}

		for(int x=0; x<13; x++)
		{
			cardsSelected.add(false);
		}


		// finds 4bombs
		for(int x=0; x<cards.size()-3; x++){
			if(cards.get(x).getStrValue().equals(cards.get(x+1).getStrValue()) &&
			cards.get(x).getStrValue().equals(cards.get(x+2).getStrValue()) &&
			cards.get(x).getStrValue().equals(cards.get(x+3).getStrValue()))
			{
				for(int y=0; y<4; y++){
					bombs.add(cards.get(x));
					cards.remove(x);
					cardsSelected.remove(x);
				}
				break;
			}
		}
		// 2bombs will be found from straights
		for(int x=0; x<cards.size()-3; x++){
				cardsSelected.set(x, true);

				int loc=x;
				for(int y=x+1; y<cards.size(); y++){
					if(cards.get(y).getIntValue()==cards.get(loc).getIntValue()+1 && !cards.get(y).getStrValue().equals("2")){
						cardsSelected.set(y, true);
						loc=y;
					}
				}
				// if straight is selected, stop searching
				if(getNumSelected(cardsSelected)>=3){
					ArrayList<Card> straight=new ArrayList<Card>();
					/*
					for(int a=0; a<cardsPlayed.size(); a++){
						if(cardsSelected.get(a)){
							straight.add(cards.get(a));
							cardsSelected.remove(a);
							cards.remove(a);
						}
					}
					*/
					int a=0;
					while(a<cards.size()){
						if(cardsSelected.get(a)){
							straight.add(cards.get(a));
							cardsSelected.remove(a);
							cards.remove(a);
						}
						else{
							a++;
						}
					}
					straights.add(straight);
				}
				else{
					deselectAll(cardsSelected);
				}
		}
		// finds 2bombs from straights
		if(bombs.size()==0){
			for(int x=0; x<straights.size()-1; x++){
				boolean match=true;
				for(int y=0; y<straights.get(x).size(); y++){
					try{
						if(!straights.get(x).get(y).getStrValue().equals(straights.get(x+1).get(y).getStrValue())){
							match=false;
							break;
						}
					}catch(IndexOutOfBoundsException e){
						match=false;
						break;
					};
				}
				if(match){
					for(int y=0; y<straights.get(x).size(); y++){
						bombs.add(straights.get(x).get(y));
						bombs.add(straights.get(x+1).get(y));
					}
					straights.remove(x);
					straights.remove(x);
					break;
				}
			}
		}
		// finds triples
		for(int x=0; x<cards.size()-2; x++){
			if(cards.get(x).getStrValue().equals(cards.get(x+1).getStrValue()) &&
			cards.get(x).getStrValue().equals(cards.get(x+2).getStrValue()))
			{
				for(int y=0; y<3; y++){
					triples.add(cards.get(x));
					cards.remove(x);
					cardsSelected.remove(x);
				}
				break;
			}
		}
		// finds doubles
		for(int x=0; x<cards.size()-1; x++){
			if(cards.get(x).getStrValue().equals(cards.get(x+1).getStrValue())){
				for(int y=0; y<2; y++){
					doubles.add(cards.get(x));
					cards.remove(x);
					cardsSelected.remove(x);
				}
				break;
			}
		}
		/*
		// finds 2bombs from doubles
		if(bombs.size()==0){
			for(int x=0; x<doubles.size()-4; x+=2){
				int numSelected=1;
				int start=x;
				try{
					while(doubles.get(start).intValue()==doubles.get(start+2).intValue()+1){
						start+=2;
						numSelected++;
					}
				}catch(IndexOutOfBoundsException e){};
				if(numSelected>=3){
					int count=0;
					while(count<numSelected*2){
						count++;
						bombs.add(doubles.get(x));
						doubles.remove(x);
					}
					break;
				}
			}
		}
		*/
		/*
		for(int x=0; x<cards.size()-3; x++){
			int numSelected=1;
			int start=x;
			try{
				while(cards.get(start).intValue()==cards.get(start+1).intValue()+1){
					start++;
					numSelected++;
				}
			}catch(IndexOutOfBoundsException e){};
			if(numSelected>=3){
				int count=0;
				ArrayList<Card> newStraight=new ArrayList<Card>();
				while(count<numSelected*2){
					count++;
					newStraight.add(cards.get(x));
					cards.remove(x);
				}
				straights.add(newStraight);
			}
		}
		*/


		while(!cards.isEmpty()){
			singles.add(cards.remove(0));

		}
		cards.clear();

		//out.println(straights.size());

		/*
		System.out.println(getName());
		System.out.println(singles);
		System.out.println(doubles);
		System.out.println(triples);
		System.out.println(straights);
		System.out.println(bombs);

		*/
	}

	public ArrayList<Card> playNew(){
		String playType="pass";

		ArrayList<Card> cardsPlayed=new ArrayList<Card>();
		if(!has3){
			try
			{

			if(straights.size()>0){
				cardsPlayed=straights.get(0);
				straights.remove(0);
				playType="lock";

				String suit=cardsPlayed.get(0).getStrSuit();
				for(int x=1; x<cardsPlayed.size(); x++){
					if(!cardsPlayed.get(x).getStrSuit().equals(suit)){
						playType="straight";
						break;
					}

				}
				return cardsPlayed;
			}
			else if(triples.size()>0){
				cardsPlayed.add(triples.get(0));
				triples.remove(0);
				cardsPlayed.add(triples.get(0));
				triples.remove(0);
				cardsPlayed.add(triples.get(0));
				triples.remove(0);
				playType="triples";
			}
			else if(doubles.size()>0){
				cardsPlayed.add(doubles.get(0));
				doubles.remove(0);
				cardsPlayed.add(doubles.get(0));
				doubles.remove(0);
				playType="doubles";
			}
			else if(bombs.size()>0){
				while(bombs.size()>0){
					cardsPlayed.add(bombs.get(0));
					bombs.remove(0);
				}
				if(cardsPlayed.size()==4){
					playType="4bomb";
				}
				else{
					playType="2bomb";
				}
			}
			else{
				cardsPlayed.add(singles.get(0));
				singles.remove(0);
				playType="single";
				return cardsPlayed;
			}

			}
			catch(Exception e)
			{
				System.out.println("EXCEPTION!");
				e.printStackTrace(System.err);
				System.out.println(getName());
				System.out.println(singles);
				System.out.println(doubles);
				System.out.println(triples);
				System.out.println(straights);
				System.out.println(bombs);
				System.out.println(numCards);

				System.exit(1);
			}
		}
		else{

			Card lowest=new Card(0, 0);
			if(straights.size()>0 && straights.get(0).get(0).equals(lowest))  {
				cardsPlayed=straights.get(0);
				straights.remove(0);
				playType="lock";

				String suit=cardsPlayed.get(0).getStrSuit();
				for(int x=1; x<cardsPlayed.size(); x++){
					if(!cardsPlayed.get(x).getStrSuit().equals(suit)){
						playType="straight";
						break;
					}

				}
			}
			else if(triples.size()>0 && triples.get(0).equals(lowest)){
				cardsPlayed.add(triples.get(0));
				triples.remove(0);
				cardsPlayed.add(triples.get(0));
				triples.remove(0);
				cardsPlayed.add(triples.get(0));
				triples.remove(0);
				playType="triples";
			}
			else if(doubles.size()>0 && doubles.get(0).equals(lowest)){
				cardsPlayed.add(doubles.get(0));
				doubles.remove(0);
				cardsPlayed.add(doubles.get(0));
				doubles.remove(0);
				playType="doubles";
			}
			else if(bombs.size()>0 && bombs.get(0).equals(lowest)){
				while(bombs.size()>0){
					cardsPlayed.add(bombs.get(0));
					bombs.remove(0);
				}
				if(cardsPlayed.size()==4){
					playType="4bomb";
				}
				else{
					playType="2bomb";
				}
			}
			else{
				cardsPlayed.add(singles.get(0));
				singles.remove(0);
				playType="single";
			}

		}

		has3=false;
		return cardsPlayed;
	}

	public CardPlay myControl()
	{
		delay();

		/*
		ArrayList<Card> cards=playNew();
		CardPlay temp=CardPlay.getCardPlay(cards);

		if(temp==null)
		{
			if(cards.size()>0)
			{
				System.out.println("null: "+getName()+" "+cards);
			}
		}
		else if(!temp.verify())
		{
			System.out.println("unverifiable: "+getName()+" "+temp.getCards());
		}
		else if(!temp.playable(getLastPlay(), getRules()) && getLastPlay()!=null)
		{
			System.out.println(getLastPlay().getCards());
			System.out.println("unplayable: "+getName()+" "+temp.getCards());
		}

		return temp;
		*/

		return CardPlay.getCardPlay(playNew());
	}

	public CardPlay myTurn()
	{
		
		delay();


		/*
		ArrayList<Card> cards=playCards(getLastPlay().getCards(), getLastPlay().getName());
		CardPlay temp=CardPlay.getCardPlay(cards);

		if(temp==null)
		{
			if(cards.size()>0)
			{
				System.out.println("null: "+getName()+" "+cards);
			}
		}
		else if(!temp.verify())
		{
			System.out.println("unverifiable: "+getName()+" "+temp.getCards());
		}
		else if(!temp.playable(getLastPlay(), getRules()))
		{
			System.out.println("unplayable: "+getName()+" "+temp.getCards());
		}
		return temp;
		*/

		return CardPlay.getCardPlay(playCards(getLastPlay().getCards(), getLastPlay().getName()));
	}

	public ArrayList<Card> playCards(ArrayList<Card> last, String lastType){
		return playCards(last, lastType, false);
	}
	public ArrayList<Card> playCards(ArrayList<Card> last, String lastType, boolean cardsLeft){

		
		// boolean cardsLeft refers to how many cards are left for the next player- if it's 1, true
		String playType="pass";
		ArrayList<Card> cardsPlayed=new ArrayList<Card>();

		if(lastType.equals("single")){
			if(last.get(0).getStrValue().equals("2")){
				if(bombs.size()==4){
					for(int x=0; x<bombs.size(); x++){
						cardsPlayed.add(bombs.get(x));
					}
					bombs.clear();
					playType="4bomb";
					return cardsPlayed;
				}
				else if(bombs.size()>=6){
					for(int x=0; x<bombs.size(); x++){
						cardsPlayed.add(bombs.get(x));
					}
					bombs.clear();
					playType="2bomb";
					return cardsPlayed;
				}

			}
			if(!cardsLeft){
				if(singles.size()>0){
					for(int x=0; x<singles.size(); x++){
						if(singles.get(x).compareTo(last.get(0))>0){
							if((singles.get(x).getIntValue()<10 || singles.size()==1)
								){
								cardsPlayed.add(singles.get(x));
								singles.remove(x);
								playType="single";
							}
							return cardsPlayed;
						}
					}
				}
				/*
				if(doubles.size()>0){
					for(int x=0; x<doubles.size(); x+=2){
						if(doubles.get(x+1).compareTo(last.get(0))>0){
							cardsPlayed.add(doubles.get(x+1));
							doubles.remove(x+1);
							singles.add(doubles.get(x));
							doubles.remove(x);
							Collections.sort(singles);
							playType="single";
							return;
						}
					}
				}*/
				return cardsPlayed;

			}
			else{
				if(singles.size()>0 && singles.get(singles.size()-1).compareTo(last.get(0))>0){
					cardsPlayed.add(singles.get(singles.size()-1));
					singles.remove(singles.size()-1);
					playType="single";
					return cardsPlayed;
				}
				if(doubles.size()>0 && doubles.get(doubles.size()-1).compareTo(last.get(0))>0){
					cardsPlayed.add(doubles.get(doubles.size()-1));
					doubles.remove(doubles.size()-1);
					singles.add(doubles.get(doubles.size()-1));
					doubles.remove(doubles.size()-1);
					Collections.sort(singles);
					playType="single";
					return cardsPlayed;
				}
			}
			return cardsPlayed;
		}
		else if(lastType.equals("double") && doubles.size()>0){
			if(last.get(0).getStrValue().equals("2")){
				if(bombs.size()==4){
					for(int x=0; x<bombs.size(); x++){
						cardsPlayed.add(bombs.get(x));
					}
					bombs.clear();
					playType="4bomb";
					return cardsPlayed;
				}
				else if(bombs.size()>=8){
					for(int x=0; x<bombs.size(); x++){
						cardsPlayed.add(bombs.get(x));
					}
					bombs.clear();
					playType="2bomb";
					return cardsPlayed;
				}

			}

			for(int x=0; x<doubles.size(); x+=2){
				if(doubles.get(x+1).compareTo(last.get(1))>0){
					cardsPlayed.add(doubles.get(x));
					doubles.remove(x);
					cardsPlayed.add(doubles.get(x));
					doubles.remove(x);
					playType="doubles";
					return cardsPlayed;
				}
			}
		}
		else if(lastType.equals("triple") && triples.size()>0){
			if(last.get(0).getStrValue().equals("2")){
				if(bombs.size()==4){
					for(int x=0; x<bombs.size(); x++){
						cardsPlayed.add(bombs.get(x));
					}
					bombs.clear();
					playType="4bomb";
					return cardsPlayed;
				}
				else if(bombs.size()>=10){
					for(int x=0; x<bombs.size(); x++){
						cardsPlayed.add(bombs.get(x));
					}
					bombs.clear();
					playType="2bomb";
					return cardsPlayed;
				}

			}

			for(int x=0; x<triples.size(); x+=3){
				if(triples.get(x+2).compareTo(last.get(2))>0){
					cardsPlayed.add(triples.get(x));
					triples.remove(x);
					cardsPlayed.add(triples.get(x));
					triples.remove(x);
					cardsPlayed.add(triples.get(x));
					triples.remove(x);
					playType="triples";
					return cardsPlayed;
				}
			}
		}
		else if(lastType.equals("bomb")){
			if(bombs.size()==4){
				while(bombs.size()>0){
					cardsPlayed.add(bombs.get(0));
					bombs.remove(0);
				}
				playType="4bomb";
			}
			else if(bombs.size()>=last.size()){
				for(int x=last.size()-2; x<bombs.size(); x+=2){
					if(bombs.get(x+1).compareTo(last.get(last.size()-1))>0){
						int y=x+2-last.size();
						while(cardsPlayed.size()<last.size()){
							cardsPlayed.add(bombs.get(y));
							bombs.remove(y);
						}

						while(bombs.size()>0){
							doubles.add(bombs.get(0));
							bombs.remove(0);
						}

						playType="2bomb";
						return cardsPlayed;
					}
				}
			}
		}
		else if(lastType.equals("break") && bombs.size()==4){
			if(bombs.get(3).compareTo(last.get(3))>0){
				while(bombs.size()>0){
					cardsPlayed.add(bombs.get(0));
					bombs.remove(0);

				}
				playType="4bomb";
				return cardsPlayed;
			}
		}
		else if(lastType.equals("lock") || lastType.equals("straight")){
			/*
			for(int x=0; x<straights.size(); x++){
				if(straights.get(x).size()<last.size()){
					continue;
				}
				String suit=straights.get(x).get(straights.get(x).size()-last.size()).getSuit();
				boolean isLock=true;
				for(int a=straights.get(x).size()-last.size()+1; a<straights.get(x).size(); a++){
					if(!straights.get(x).get(a).getSuit().equals(suit)){
						isLock=false;
						break;
					}
				}
				if(!isLock && lastType.equals("lock")){
					continue;
				}
				if(straights.get(x).get(straights.get(x).size()-1).compareTo(last.get(last.size()-1))>0){
					int y=straights.get(x).size()-last.size();
					while(cardsPlayed.size()<last.size()){
						cardsPlayed.add(straights.get(x).get(y));
						straights.get(x).remove(y);
					}

					if(straights.get(x).size()<3){
						// move all cards in x to singles
						for(int a=0; a<straights.get(x).size(); x++){
							singles.add(straights.get(x).get(a));
						}
						Collections.sort(singles);
						straights.remove(x);
					}
					if(isLock){
						playType="lock";
					}
					else{
						playType="straight";
					}

					return;
				}
			}
			*/
			for(ArrayList<Card> x:straights){
				int loc2=straights.indexOf(x);
				ArrayList<Card> temp=new ArrayList<Card>();
				int loc=0;
				while(loc+last.size()<x.size()){
					temp.clear();
					for(int a=loc; a<loc+last.size(); a++){
						temp.add(x.get(a));
					}
					if(temp.get(temp.size()-1).compareTo(last.get(last.size()-1))>0){
						if(isLock(temp)){
							playType="lock";
						}
						else{
							if(lastType.equals("lock")){
								loc++;
								continue;
							}
							playType="straight";
						}
					}
					else{
						loc++;
						continue;
					}
					// put cards into played
					for(Card b:temp){
						cardsPlayed.add(b);
					}

					// remove cards played and put extras into singles
					for(int a=loc; a<loc+last.size(); a++){
						x.remove(loc);
					}
					while(x.size()>0){
						singles.add(x.get(0));
						x.remove(0);
					}
					straights.remove(loc2);
					Collections.sort(singles);
					return cardsPlayed;
				}
			}
		}
		if(playType.equals("")){
			playType="pass";

			return null;
		}

		return cardsPlayed;
	}


	public boolean isLock(ArrayList<Card> straight){
		for(Card x:straight){
			if(!x.getStrSuit().equals(straight.get(0).getStrSuit())){
				return false;
			}
		}
		return true;
	}




}