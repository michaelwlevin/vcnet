package vcnet.server;

import vcnet.mech.RuleList;
import vcnet.net.NetMessage;

public class CreateServerMessage extends NetMessage
{
	public byte getType()
	{
		return NetMessage.CREATE_SERVER;
	}

	private String name;
	private String password;
	private RuleList rules;

	public CreateServerMessage(String n, RuleList r)
	{
		this(n, "", r);
	}
	public CreateServerMessage(String n, String p, RuleList r)
	{
		name=n;
		password=p;
		rules=r;
	}

	public String getPassword()
	{
		return password;
	}
	public String getName()
	{
		return name;
	}
	public RuleList getRules()
	{
		return rules;
	}
}