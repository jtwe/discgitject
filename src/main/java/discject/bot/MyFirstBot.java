package discject.bot;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import discject.utils.EmojiUtils;
import discject.listeners.*;

public class MyFirstBot {

    public static void main(String[] args) {
    	if (args.length>0) {
            // Insert your bot's token here
    		String token = args[0];

    		DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

    		EmojiUtils.loadEmojis(api);

    		api.addListener(new PingListener());
//    		api.addListener(new ReactionListener());
//    		api.addListener(new PokerListener());

    		// Print the invite url of your bot
    		System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    	}
    }
}
