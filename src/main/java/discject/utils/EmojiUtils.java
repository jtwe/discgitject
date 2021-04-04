package discject.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.emoji.KnownCustomEmoji;

import com.vdurmont.emoji.EmojiParser;

public class EmojiUtils {
	
	private static String[] emojiNames = {
			"black_10", 
			"black_2", 
			"black_3", 
			"black_4", 
			"black_5", 
			"black_6", 
			"black_7", 
			"black_8", 
			"black_9", 
			"black_a", 
			"black_j", 
			"black_k", 
			"black_q", 
			"joker_black_left", 
			"joker_black_right", 
			"joker_red_left", 
			"joker_red_right", 
			"red_10", 
			"red_2", 
			"red_3", 
			"red_4", 
			"red_5", 
			"red_6", 
			"red_7", 
			"red_8", 
			"red_9", 
			"red_a", 
			"red_j", 
			"red_k", 
			"red_q", 
			"suit_clubs", 
			"suit_diamonds", 
			"suit_hearts", 
			"suit_spades", 
	};
	
	private static Map<Long, Map<String, Long>> customEmojiMap = new HashMap<Long, Map<String, Long>>();
	
	public static void loadEmojis(DiscordApi api) {
		System.out.println("Looking for my emojis");
		for (String emojiName : emojiNames) {
			System.out.println(" Looking for " + emojiName);
			Collection<KnownCustomEmoji> emojis = api.getCustomEmojisByName(emojiName);
			for (KnownCustomEmoji kce : emojis) {
				System.out.println("  Found: " + kce);
				Long serverId = kce.getServer().getId();
				Long emojiId = kce.getId();
				Map<String, Long> serverMap = customEmojiMap.get(serverId);
				if (serverMap==null) {
					serverMap = new HashMap<String, Long>();
					customEmojiMap.put(serverId, serverMap);
				}
				serverMap.put(emojiName, emojiId);
			}
		}
	}

	public static Long getCustomEmojiIdForServer(Long serverId, String emojiName) {
		Map<String, Long> serverMap = customEmojiMap.get(serverId);
		if (serverMap==null) return null;
		return serverMap.get(emojiName);
	}
	
	public static String getCustomEmojiTagForServer(Long serverId, String emojiName) {
		Map<String, Long> serverMap = customEmojiMap.get(serverId);
		if (serverMap==null) return null;
		Long emojiId = serverMap.get(emojiName);
		if (emojiId==null) return null;
		return "<:" + emojiName + ":" + emojiId + ">";
	}
	
	public String toAliases(String string) {
		return EmojiParser.parseToAliases(string);
	}
	
	public static void main() {
		
	}
}
