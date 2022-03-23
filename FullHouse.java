/**
 * This class is used to model a hand of FullHouse
 * 
 * @author wingk
 */
public class FullHouse extends Hand {
  private static final long serialVersionUID = 4622772461907486829L;

  /**
	 * a constructor for building FullHouse
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public FullHouse(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this hand is a FullHouse
	 */
	public boolean isValid() {
		sort(); // hand become [X,X,X,Y,Y] or [Y,Y,X,X,X]
		if (size() == 5) { // 5 card
			// case [X,X,X,Y,Y]
			if (getCard(0).getRank() == getCard(1).getRank()
					&& getCard(1).getRank() == getCard(2).getRank()) {
				if (getCard(3).getRank() == getCard(4).getRank())
					return true;
			// case [Y,Y,X,X,X]
			} else if (getCard(2).getRank() == getCard(3).getRank()
					&& getCard(3).getRank() == getCard(4).getRank()) {
				if (getCard(0).getRank() == getCard(1).getRank())
					return true;
			}
		}
		return false;
	}
	
	/**
	 * a method for retrieving the top card of this hand
	 * 
	 * @return the top card of this hand
	 */
	public Card getTopCard() {
		sort();
		// case [X,X,X,Y,Y]
		if (getCard(0).getRank() == getCard(1).getRank()
				&& getCard(1).getRank() == getCard(2).getRank())
			return getCard(2);
		// case [Y,Y,X,X,X]
		else
			return getCard(4);
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public String getType() { return "FullHouse"; }
}
