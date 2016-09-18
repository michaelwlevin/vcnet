package vcnet.net;
import java.io.*;

public abstract class NetMessage implements Serializable
{
	public static final byte TEXT=0;
	public static final byte POPUP=1;
	public static final byte CARDPLAY=2;
	public static final byte PASS=3;
	public static final byte CONTROL=4;
	public static final byte TURN_CHANGE=5;
	public static final byte CHAT=6;
	public static final byte JOIN=7;
	public static final byte JOIN_SUCCESS=8;
	public static final byte PLAYER_INFO=9;
	public static final byte END_GAME=10;
	public static final byte PLAY_CARDS=11;
	public static final byte PLAYER_OUT=12;
	public static final byte CONNECT=13;
	public static final byte SCORES=14;
	public static final byte JOIN2=15;
	public static final byte PM=16;
	public static final byte USER_LIST=17;
	public static final byte SERVER_CONNECT=18;
	public static final byte SERVER_LIST=19;
	public static final byte CREATE_SERVER=20;
	public static final byte REMOVE_SERVER_LIST=21;



	public abstract byte getType();


}