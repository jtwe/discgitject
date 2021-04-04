package discject.listeners;

import java.util.Optional;

import org.javacord.api.entity.Nameable;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import discject.utils.PokerAction;

public class PingListener implements MessageCreateListener {

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		if (event.getMessage().getAuthor().isBotUser()) return;

		String message = event.getMessageContent().toLowerCase();
		if (!message.startsWith("!")) message = "!" + message;
		
		if (message.startsWith("!retrodeal")) {
			PokerAction.retroDeal(event);
		} else if (message.startsWith("!deal")) {
			PokerAction.newDeal(event);
		} else if (message.startsWith("!ping")) {
			System.out.println("Ping!");
			event.getChannel().sendMessage("Ponk.");
//			event.getChannel().sendMessage("Punk. :two: :three: :four: :five: :six: :seven: :eight: :nine: :keycap_ten: :diving_mask: :woman_mage: :crown: :black_joker: :spades: :hearts: :diamonds: :clubs:");
		} else if (message.startsWith("!echo")) {
			System.out.println("Processing: " + event.getMessageContent());

//			discject.utils.ClassAnalysis.analyzeClass(event, "Event");
			
			MessageAuthor author = event.getMessageAuthor();
//			discject.utils.ClassAnalysis.analyzeClass(author, "Author");
			
			Optional<Server> server = event.getServer();
			Nameable channel = null;
			try {
				channel = (Nameable)event.getChannel();
			} catch (ClassCastException e) {
				channel = null;
			}
			
			event.getChannel().sendMessage("Received " + event.getMessageContent().substring(1) + " from **" + event.getMessageAuthor().getDisplayName() + "** in ||" + (channel==null?"nothing":channel.getName()) + "|| in __" + (server.isPresent()?server.get().getName():"nowhere") + "__");

			if (EmojiManager.containsEmoji(event.getMessageContent())) {
				System.out.println("Found emoji: " );
				System.out.println(" " + event.getMessageContent());
				System.out.println(" " + EmojiParser.parseToAliases(event.getMessageContent()));
				event.getChannel().sendMessage("With emojis: " + EmojiParser.parseToAliases(event.getMessageContent().substring(1)));
			}
		} else if (event.getMessage().isPrivateMessage()) {
			event.getChannel().sendMessage("Options: \n deal - deal poker hand (# = number of cards, min 4, max 11; all = deal all one at a time; now = deal all immediately) \n !ping - alive message \n !echo - echo message");
		} else {
			System.out.println("Ignoring: " + event.getMessageContent() );
		}
		
	}  

}
