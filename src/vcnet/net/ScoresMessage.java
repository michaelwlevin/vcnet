package vcnet.net;
import java.util.Map;

public class ScoresMessage extends NetMessage
{
	private Map<String, Long> scores;
	
	public ScoresMessage(Map<String, Long> s)
	{
		scores=s;
	}
	
	public Map<String, Long> getScores()
	{
		return scores;
	}
	
	public byte getType()
	{
		return NetMessage.SCORES;
	}
}