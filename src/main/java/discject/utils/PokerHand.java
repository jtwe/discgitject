package discject.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PokerHand {
	private int handRanking;
	private int numCards;
	private ArrayList<Integer> deck;

	private static String[] rankStr = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
	private static String[] suitStr = {"C", "D", "H", "S"};
	private static String[] suitFullStr = {"clubs", "diamonds", "hearts", "spades"};
	private static String[] handRankNames = {
		"nothing", // 0
		"ace high", // 1
		"pair", // 2
		"jacks or better", // 3  
		"two pair", // 4
		"three of a kind", // 5 
		"straight", // 6
		"flush", // 7
		"full house", // 8
		"four of a kind", // 9 
		"five of a kind", // 10
		"straight flush", // 11
		"royal flush" // 12
	};
	private static int[] handRankPp = {0, 2, 3, 4, 5, 6, 8, 10, 10, 15, 15, 15, 15};
	private static String[] handRankPpStr = {
			"0", // "nothing", // 0
			"2", // "ace high", // 1
			"3", // "pair", // 2
			"4", // "jacks or better", // 3  
			"5", // "two pair", // 4
			"6", // "three of a kind", // 5 
			"8", // "straight", // 6
			"10", // "flush", // 7
			"10 and double", // "full house", // 8
			"15 and double", // "four of a kind", // 9 
			"15 and double and add modifiers", // "five of a kind", // 10
			"15 and double and add modifiers and conviction", // "straight flush", // 11
			"15 and double and add modifiers and conviction and holy crap", // "royal flush" // 12
	};
	
	private static String getCardName(int card) {
		if (card<52) {
			int rank = card/4;
			int suit = card%4;
			return rankStr[rank] + suitStr[suit];
		} else {
			if (card==52) return "JOKER"; 
			return "joker";
		}
	}

	public PokerHand(int numCards) { 
		this(numCards, false);
	}
	
	public PokerHand(int numCards, boolean verbose) {
		this.numCards = numCards;
		deck = new ArrayList<Integer>();
		for (int i=0; i<54; i++) deck.add(i);
		Collections.shuffle(deck);

		setHandRanking(verbose);
	}
	
	public void setNumCards(int numCards) {
		this.numCards = numCards;
		this.setHandRanking();
	}
	
	private void setHandRanking() {
		setHandRanking(false);
	}
	
	private void setHandRanking(boolean verbose) {
		ArrayList<Integer> rankCounts = new ArrayList<Integer>();
		ArrayList<Integer> suitCounts = new ArrayList<Integer>();
		boolean[] cardsSeen = new boolean[52];

		int[] ranks = new int[13];
		int[] suits = new int[4];
		int jokers = 0;
		for (int i=0; i<numCards; i++) {
			if (deck.get(i)<52) {
				ranks[deck.get(i)/4]++;
				suits[deck.get(i)%4]++;
				cardsSeen[deck.get(i)] = true;
			} else {
				jokers++;
			}
			if (verbose) System.out.println("Card #" + deck.get(i) + ", " + getCardName(deck.get(i)));
		}

		for (int i=0; i<13; i++) if (ranks[i]>0) rankCounts.add(ranks[i]);
		for (int i=0; i<4; i++) if (suits[i]>0) suitCounts.add(suits[i]);

		Collections.sort(rankCounts);
		Collections.reverse(rankCounts);
		Collections.sort(suitCounts);
		Collections.reverse(suitCounts);
		
		if (verbose) System.out.println("Ranks: " + rankCounts + ", Suits: " + suitCounts + ", Jokers = " + jokers);
		
		boolean isStraight = false, isFlush = false;
		int straightFlush = -1;
		
		if ( suitCounts.size()>0 && (suitCounts.get(0) + jokers) >= 5) isFlush = true;
		if (verbose) System.out.println("Max suit = " + ( ( suitCounts.size()>0?suitCounts.get(0):0) + jokers) + ", flush = " + isFlush);
		
		for (int i=-1; i<9; i++) {
			int seen = 0;
			if (verbose) System.out.println("Looking for " + rankStr[(i+13)%13] + " low straight");
			for (int j=0; j<5; j++) {
				if (ranks[(i+j+13)%13]>0) {
					seen++;
					if (verbose) System.out.println("  Found " + rankStr[(i+j+13)%13]);
				} 
			}
			if (seen+jokers>=5) {
				isStraight = true;
				if (verbose) System.out.println("  Straight!");
			}
		}
		
		for (int i=-1; i<9; i++) {
			for (int k=0; k<4; k++) {
				int seen = 0;
				if (verbose) System.out.println("Looking for " + rankStr[(i+13)%13] + " low straight in " + suitStr[k]);
				for (int j=0; j<5; j++) {
					int cardId = 4*((i+j+13)%13) + k;
					if (cardsSeen[cardId]) {
						seen++;
						if (verbose) System.out.println("  Found " + getCardName(cardId));
					}
				}
				if (seen+jokers>=5) {
					straightFlush = (i+1);
					if (verbose) System.out.println("  Straight Flush!!");
				}
			}
		}

		this.handRanking = 0;
//		"ace high", // 1
		if (ranks[12]>0 || jokers>0) this.handRanking = 1;
		
//		"pair", // 2
		if ( (rankCounts.size()>0?rankCounts.get(0):0) + jokers>=2) this.handRanking = 2;
		
//		"jacks or better", // 3  
		for (int i=9; i<=12; i++) if (ranks[i]+jokers>=2) this.handRanking = 3;

//		"two pair", // 4
		if (rankCounts.size()>1 && rankCounts.get(1)>=2) this.handRanking = 4;
		
//		"three of a kind", // 5 
		if ( (rankCounts.size()>0?rankCounts.get(0):0) +jokers>=3) this.handRanking = 5;
		
//		"straight", // 6
		if (isStraight) this.handRanking = 6;
		
//		"flush", // 7
		if (isFlush) this.handRanking = 7;
		
//		"full house", // 8
		if ( (rankCounts.size()>0?rankCounts.get(0):0) +jokers>=3 && rankCounts.size()>1 && rankCounts.get(1)>=2) this.handRanking = 8;
		
//		"four of a kind", // 9 
		if ( (rankCounts.size()>0?rankCounts.get(0):0) +jokers>=4) this.handRanking = 9;
		
//		"five of a kind", // 10
		if ( (rankCounts.size()>0?rankCounts.get(0):0) +jokers>=5) this.handRanking = 10;

//		"straight flush", // 11
		if (straightFlush>=0) this.handRanking = 11;
		
//		"royal flush" // 12
		if (straightFlush==9) this.handRanking = 12;
	}

	public int getHandRanking() {
		return this.handRanking;
	}

	public String getHandDescription() {
		StringBuffer toRet = new StringBuffer();
		
		for (int i=0; i<this.numCards; i++) {
			toRet.append(getCardName(this.deck.get(i)) + " ");
		}
		toRet.append("- ");
		toRet.append(handRankNames[this.handRanking]);

		return toRet.toString();
	}
	
	public int getHandRankPp() {
		return handRankPp[this.handRanking];
	}

	public String getHandRankPpStr() {
		return handRankPpStr[this.handRanking];
	}

	public String getHandCustomEmojis(Long serverId) {
		StringBuffer toRet = new StringBuffer();
		
		for (int i=0; i<this.numCards; i++) {
			int card = this.deck.get(i);
			if (card<52) {
				int rank = card/4;
				int suit = card%4;
				String color = "red";
				if (suit==0 || suit==3) color = "black";
				toRet.append(EmojiUtils.getCustomEmojiTagForServer(serverId, color + "_" + rankStr[rank].toLowerCase()));
				toRet.append(EmojiUtils.getCustomEmojiTagForServer(serverId, "suit_" + suitFullStr[suit]));
			} else {
				if (card==52) {
					toRet.append(EmojiUtils.getCustomEmojiTagForServer(serverId, "joker_red_left"));
					toRet.append(EmojiUtils.getCustomEmojiTagForServer(serverId, "joker_red_right"));
				} else {
					toRet.append(EmojiUtils.getCustomEmojiTagForServer(serverId, "joker_black_left"));
					toRet.append(EmojiUtils.getCustomEmojiTagForServer(serverId, "joker_black_right"));
				}
			}
		}
		
		return toRet.toString();
	}
	
	public static void main(String[] args) {
//		PokerHand ph = new PokerHand(50, true);
//		System.out.println(ph.getHandDescription());
/*		
		for (int i=0; i<100; i++) {
			PokerHand ph = new PokerHand(7);
			System.out.println(ph.getDescription());
		}
*/
/*
		Map<Integer, Integer> rankCounts = new HashMap<Integer, Integer>();
		for (int i=0; i<1000000; i++) {
			PokerHand ph = new PokerHand(6);
			int rank = ph.getHandRanking();
			Integer rankCount = rankCounts.get(rank);
			if (rankCount==null) {
				System.out.println(ph.getHandDescription());
				rankCount = 0;
			}
			rankCounts.put(rank, 1+rankCount);
		}
		for (int i=0; i<=12; i++) {
			System.out.println(i + "\t" + handRankNames[i] + "\t" + rankCounts.get(i));
		}
*/
		PokerHand ph = new PokerHand(5);
		System.out.println(ph.getHandDescription());
		ph.setNumCards(6);
		System.out.println(ph.getHandDescription());
		ph.setNumCards(7);
		System.out.println(ph.getHandDescription());
	}
}
