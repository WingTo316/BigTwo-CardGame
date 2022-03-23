/**
 * This class is used to model a hand of Flush
 * 
 * @author wingk
 */
public class Flush extends Hand {
  private static final long serialVersionUID = -1176872676059568463L;

  /**
	 * a constructor for building Flush
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public Flush(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this hand is a Flush
	 */
	public boolean isValid() {
		sort();
		// 5 card and all same suit
		if (size() == 5 && getCard(0).getSuit() == getCard(1).getSuit()
				&& getCard(1).getSuit() == getCard(2).getSuit()
				&& getCard(2).getSuit() == getCard(3).getSuit()
				&& getCard(3).getSuit() == getCard(4).getSuit()) {
			// not a StraightFlush
			// case [3,4,5,6,7]...[9,0,J,Q,K]
			if (getCard(0).getRank() >= 2
					&& getCard(0).getRank() == getCard(1).getRank()-1
					&& getCard(1).getRank() == getCard(2).getRank()-1
					&& getCard(2).getRank() == getCard(3).getRank()-1
					&& getCard(3).getRank() == getCard(4).getRank()-1) {
				return false;
							
			// case [0,J,Q,K,A]
			} else if (getCard(0).getRank() == 9 && getCard(1).getRank() == 10
					&& getCard(2).getRank() == 11 && getCard(3).getRank() == 12
					&& getCard(4).getRank() == 0) {
				return false;
							
			// case [J,Q,K,A,2]
			} else if (getCard(0).getRank() == 10 && getCard(1).getRank() == 11
					&& getCard(2).getRank() == 12 && getCard(3).getRank() == 0
					&& getCard(4).getRank() == 1) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public String getType() { return "Flush"; }
}
