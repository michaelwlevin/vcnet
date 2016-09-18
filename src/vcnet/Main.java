/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author micha
 */

package vcnet;

import java.io.IOException;
import vcnet.bot.DefaultBotClient;
import vcnet.bot.NoobBotClient;
import vcnet.client.Client;
import vcnet.client.MultiClient;
import vcnet.server.MultiServer;
import vcnet.server.Server;

public class Main 
{
    public static void main(String[] args) throws IOException
    {
        
        if(args.length > 0)
        {
            if(args[0].equalsIgnoreCase("singleplayer"))
            {
                SinglePlayerVC.main(args);
            }
            else if(args[0].equalsIgnoreCase("server"))
            {
                Server.main(args);
            }
            else if(args[0].equalsIgnoreCase("multiserver"))
            {
                MultiServer.main(args);
            }
            else if(args[0].equalsIgnoreCase("easybot"))
            {
                NoobBotClient.main(args);
            }
            else if(args[0].equalsIgnoreCase("bot"))
            {
                DefaultBotClient.main(args);
            }
            else if(args[0].equalsIgnoreCase("client"))
            {
                Client.main(args);
            }
            else if(args[0].equalsIgnoreCase("multiclient"))
            {
                MultiClient.main(args);
            }
        }
        else
        {
            SinglePlayerVC.main(args);
        }
    }
}
