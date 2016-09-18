package vcnet.mech;

import java.awt.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;

public class Card implements Comparable<Card>, java.io.Serializable, Cloneable
{
	public static final String[] VALUES={"3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A", "2"};
	public static final String[] SUITS={"Spades", "Clubs", "Diamonds", "Hearts"};
	public static final String[] SYMBOLS={"♠","♣","♦","♥"};
	
	private static boolean useSymbols=false;
	
	public static void setUseSymbols(boolean b)
	{
		useSymbols=b;
	}
	public static boolean getUseSymbols()
	{
		return useSymbols;
	}
	
	public static final Card THREE_OF_SPADES=new Card((byte)0, (byte)00);
	
	private static Image side, back;
	
	//private static ImagesLoader imLoader=new ImagesLoader("imsInfo.txt");
	
	public static void loadStaticImages()
	{
		try
		{
			side=ImageIO.read(new File("Images/CardSide.jpg"));
		}
		catch(IOException e)
		{
			//System.err.println("Could not find file CardSide.jpg");
		}
		
		try
		{
			back=ImageIO.read(new File("Images/CardBack.jpg"));
		}
		catch(IOException e)
		{
			//System.err.println("Could not find file CardBack.jpg");
		}
	}
	public static void drawSide(Graphics window, int x, int y, int width, int height)
	{
		/*
		window.setColor(Color.cyan);
		window.fillRect(x, y, width, height);
		window.setColor(Color.black);
		window.drawRect(x, y, width, height);
		*/
		window.drawImage(side, x, y, width, height, null);
	}
	public static void drawBack(Graphics window, int x, int y, int width, int height)
	{
		/*
		window.setColor(Color.cyan);
		window.fillRect(x, y, width, height);
		window.setColor(Color.black);
		window.drawRect(x, y, width, height);
		*/
		window.drawImage(side, x, y, width, height, null);
	}
	
	public static Card getCard(String v, String s)
	{
		v=v.trim();
		s=s.trim();
		
		byte value=-1;
		
		for(byte x=0; x<VALUES.length; x++)
		{
			if(v.equalsIgnoreCase(VALUES[x]))
			{
				value=x;
				break;
			}
		}
		
		byte suit=-1;
		
		for(byte x=0; x<SUITS.length; x++)
		{
			if(s.equalsIgnoreCase(SUITS[x]))
			{
				suit=x;
				break;
			}
		}
		
		if(value>=0 && suit>=0)
		{
			return new Card(value, suit);
		}
		return null;
	}
	
	private byte value;
	private byte suit;
	
	public Card(String v, String s)
	{
		this(getCard(v, s));
	}
	public Card(Card rhs)
	{
		this(rhs.getIntValue(), rhs.getIntSuit());
		
	}
	public Card(int v, int s)
	{
		this((byte)v, (byte)s);
	}
	public Card(byte v, byte s)
	{
		if(v<0 || v>12)			
		{
			throw(new IllegalArgumentException("value is "+v));
		}
		else if(s<0 || s>3)
		{
			throw(new IllegalArgumentException("suit is "+s));
		}
		value=v;
		suit=s;
		
		//loadStaticImages();
		
	}
	
	public String toString()
	{
		if(getUseSymbols())
		{
			return getStrValue()+getSymbolSuit();
		}
		else
		{
			return getStrValue()+" of "+getStrSuit();
		}
	}
	public String showCard()
	{
		if(!getUseSymbols())
		{
			return toString();
		}
		return getStrValue()+getSymbolSuit();
	}
	public byte getIntValue()
	{
		return value;
	}
	public byte getIntSuit()
	{
		return suit;
	}
	
	public String getStrValue()
	{
		return VALUES[value];
	}
	public String getStrSuit()
	{
		return SUITS[suit];
	}
	public String getSymbolSuit()
	{
		return SYMBOLS[suit];
	}
	
	public String getImageName()
	{
		return "Images/"+getStrSuit()+getStrValue()+".jpg";
	}
	
	public Image getImage()
	{
		try
		{
			return ImageIO.read(new File(getImageName()));
		}
		catch(IOException e)
		{
			//System.err.println("Could not find file "+getImageName());
			return null;
		}
	}
	
	public boolean draw(Graphics window, int x, int y, int width, int height)
	{
		try
		{
			window.drawImage(getImage(), x, y, width, height, null);
			
			return true;
		}
		catch(NullPointerException e)
		{
			return false;
		}
	}
	
	public byte getHolisticValue()
	{
		return (byte)(getIntValue()*4+getIntSuit());
	}
	

	
	public int compareTo(Card rhs)
	{
		return getHolisticValue()-rhs.getHolisticValue();
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof Card)
		{
			Card rhs=(Card)o;
			
			return rhs.getIntSuit()==getIntSuit() && rhs.getIntValue()==getIntValue();
		}
		return false;
	}
	
	public Object clone()
	{
		return new Card(this);
	}
}