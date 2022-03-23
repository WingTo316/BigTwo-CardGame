/**
 * This class is used to model a hand of cards
 * 
 * @author wingk
 */
public abstract class Hand extends CardList {
  private static final long serialVersionUID = 3708415018664975012L;
  private CardGamePlayer player; // the player who plays this hand
	
	/**
	 * a constructor for building a hand
	 * with the specified player and list of cards
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public Hand(CardGamePlayer player, CardList cards) {
		this.player = player;
		removeAllCards();
		for (int i=0; i<cards.size(); i++) {
			addCard(cards.getCard(i));
		}
	}
	
	/**
	 * a method for retrieving the player of this hand
	 * 
	 * @return the player of this hand
	 */
	public CardGamePlayer getPlayer() { return player; }
	
	/**
	 * a method for retrieving the top card of this hand
	 * 
	 * @return the top card of this hand
	 */
	public Card getTopCard() {
		sort();
		return getCard(size()-1);
	}
	
	/**
	 * a method for checking if this hand beats a specified hand
	 * 
	 * @param hand a specified hand
	 * @return if  this hand beats a specified hand
	 */
	public boolean beats(Hand hand) {
		// if comparable
		if (size() == hand.size()) {
			// case Single, Pair, Triple
			if (size() == 1 || size() == 2 || size() == 3) {
				if (getTopCard().compareTo(hand.getTopCard()) == 1)
					return true;
				
			} else {
				// both are Straight
				if (getType() == "Straight" && hand.getType() == "Straight") {					
					if (getTopCard().compareTo(hand.getTopCard()) == 1)
						return true;
				
				// this is Flush
				} else if (getType() == "Flush") {
					
					// Flush always beats Straight
					if (hand.getType() == "Straight") {
						return true;
					// both are Flush
					} else if (hand.getType() == "Flush") {
						if (getTopCard().compareTo(hand.getTopCard()) == 1)
							return true;
					}
				
				// this is FullHouse
				} else if (getType() == "FullHouse") {
					
					// FullHouse always beats Straight, Flush
					if (hand.getType() == "Straight" || hand.getType() == "Flush") {
						return true;
					// both are FullHouse
					} else if (hand.getType() == "FullHouse") {
						if (getTopCard().compareTo(hand.getTopCard()) == 1)
							return true;
					}
				
				// this is Quad
				} else if (getType() == "Quad") {
					
					// Quad always beats Straight, Flush, FullHouse
					if (hand.getType() == "Straight" || hand.getType() == "Flush"
							|| hand.getType() == "FullHouse") {
						return true;
					// both are Quad
					} else if (hand.getType() == "Quad") {
						if (getTopCard().compareTo(hand.getTopCard()) == 1)
							return true;
					}
				
				// this is StraightFlush
				} else if (getType() == "StraightFlush") {
					
					// StraightFlush always beats others
					if (hand.getType() == "Straight" || hand.getType() == "Flush"
							|| hand.getType() == "FullHouse"
							|| hand.getType() == "Quad") {
						return true;
					// both are StraightFlush
					} else if (hand.getType() == "StraightFlush") {
						if (getTopCard().compareTo(hand.getTopCard()) == 1)
							return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * a method for checking if this is a valid hand
	 * 
	 * @return if this is a valid hand
	 */
	public abstract boolean isValid();
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public abstract String getType();
}
