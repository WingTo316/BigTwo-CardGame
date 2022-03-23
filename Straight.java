/**
 * This class is used to model a hand of Straight
 * 
 * @author wingk
 */
public class Straight extends Hand {
  private static final long serialVersionUID = 4735239255262917363L;

  /**
	 * a constructor for building Triple
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public Straight(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this hand is a Straight
	 */
	public boolean isValid() {
		sort();
		// 5 card and not all same suit
		if (size() == 5 && (getCard(0).getSuit() != getCard(1).getSuit()
				|| getCard(1).getSuit() != getCard(2).getSuit()
				|| getCard(2).getSuit() != getCard(3).getSuit()
				|| getCard(3).getSuit() != getCard(4).getSuit())) {
			
			// case [3,4,5,6,7]...[9,0,J,Q,K]
			if (getCard(0).getRank() >= 2
					&& getCard(0).getRank() == getCard(1).getRank()-1
					&& getCard(1).getRank() == getCard(2).getRank()-1
					&& getCard(2).getRank() == getCard(3).getRank()-1
					&& getCard(3).getRank() == getCard(4).getRank()-1) {
				return true;
				
			// case [0,J,Q,K,A]
			} else if (getCard(0).getRank() == 9 && getCard(1).getRank() == 10
					&& getCard(2).getRank() == 11 && getCard(3).getRank() == 12
					&& getCard(4).getRank() == 0) {
				return true;
				
			// case [J,Q,K,A,2]
			} else if (getCard(0).getRank() == 10 && getCard(1).getRank() == 11
					&& getCard(2).getRank() == 12 && getCard(3).getRank() == 0
					&& getCard(4).getRank() == 1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public String getType() { return "Straight"; }
}
