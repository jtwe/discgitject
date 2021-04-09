package discject.utils;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import discject.utils.PokerHand;

public class PokerAction {

	public static void retroDeal(MessageCreateEvent event) {
//		event.getChannel().sendMessage("10\u2660 9\u2661 8\u2662 7\u2663 6\u2664 5\u2665 4\u2666 3\u2667");
//		event.getChannel().sendMessage("Spades emoji \u2660. Hearts text \u2661. Diamonds text \u2662. Clubs emoji \u2663. Spades text \u2664. Hearts emoji \u2665. Diamonds emoji \u2666. Club text \u2667.");
		CompletableFuture<Message> fm = event.getChannel().sendMessage("Drawing cards...");
		Message m = fm.join();
		synchronized(m) {
			try {
				m.wait(5000);
				PokerHand ph = new PokerHand(5);
				EmbedBuilder embed = new EmbedBuilder()
						.setColor(Color.RED)
						.setDescription(ph.getHandDescription());
				m.edit(embed);

				m.wait(5000);
				ph.setNumCards(6);
				embed = new EmbedBuilder()
						.setColor(Color.YELLOW)
						.setDescription(ph.getHandDescription());
				m.edit(embed);

				m.wait(5000);
				ph.setNumCards(7);
				embed = new EmbedBuilder()
						.setColor(Color.GREEN)
						.setDescription(ph.getHandDescription() + " (" + ph.getHandRankPpStr() + ")");
				m.edit(embed);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static final Pattern pNumber = Pattern.compile("\\d+");
	private static final Pattern pNow = Pattern.compile("now");
	private static final Pattern pAll = Pattern.compile("all");

	public static void newDeal(MessageCreateEvent event) {
		String message = event.getMessageContent().toLowerCase();
		Long serverId = null;
		if (event.getServer().isPresent()) serverId = event.getServer().get().getId();

		int cards = 5;
		Matcher mNumber = pNumber.matcher(message);
		if (mNumber.find()) {
			cards = Integer.parseInt(mNumber.group());
		}
		if (cards<4) cards = 4;
		if (cards>11) cards = 11;

		boolean now = false;
		if (pNow.matcher(message).find()) now = true;

		if (now) {
			PokerHand ph = new PokerHand(cards);
			CompletableFuture<Message> fm = event.getChannel().sendMessage(ph.getHandCustomEmojis(serverId));
			CompletableFuture<Message> fm2 = event.getChannel().sendMessage(ph.getHandDescription() + " (" + ph.getHandRankPpStr() + ")");
		} else {
			Matcher mAll = pAll.matcher(message);

			CompletableFuture<Message> fm = event.getChannel().sendMessage("Shuffling...");

			Message m = fm.join();

			PokerHand ph = new PokerHand(cards-3);

			for (int i=(mAll.find()?1:(cards-3)); i<=cards; i++) {
				synchronized(m) {
					try {
						m.wait(1500);

						ph.setNumCards(i);
						m.edit(ph.getHandCustomEmojis(serverId));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
			event.getChannel().sendMessage(ph.getHandDescription() + " (" + ph.getHandRankPpStr() + ")");
		}
	}

}
