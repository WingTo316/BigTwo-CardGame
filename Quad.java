/**
 * This class is used to model a hand of Quad
 * 
 * @author wingk
 */
public class Quad extends Hand {
  private static final long serialVersionUID = -6513678899946621301L;

  /**
	 * a constructor for building Quad
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public Quad(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this hand is a Quad
	 */
	public boolean isValid() {
		sort(); // hand become [X,X,X,X,Y] or [Y,X,X,X,X]
		if (size() == 5) {
			// case [X,X,X,X,Y]
			if (getCard(0).getRank() == getCard(1).getRank()
					&& getCard(1).getRank() == getCard(2).getRank()
					&& getCard(2).getRank() == getCard(3).getRank()) {
				return true;
			// case [Y,X,X,X,X]
			} else if (getCard(1).getRank() == getCard(2).getRank()
					&& getCard(2).getRank() == getCard(3).getRank()
					&& getCard(3).getRank() == getCard(4).getRank()) {
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
		// case [X,X,X,X,Y]
		if (getCard(0).getRank() == getCard(1).getRank()
				&& getCard(1).getRank() == getCard(2).getRank()
				&& getCard(2).getRank() == getCard(3).getRank())
			return getCard(3);
		// case [Y,X,X,X,X]
		else
			return getCard(4);
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public String getType() { return "Quad"; }
}
