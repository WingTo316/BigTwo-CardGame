/**
 * This class used to model a card used in a Big Two card game
 * 
 * @author wingk
 */
public class BigTwoCard extends Card {
  private static final long serialVersionUID = -5822018182891811204L;

  /**
	 * a constructor for building a card with the specified suit and rank
	 * 
	 * @param suit suit of card
	 * @param rank rank of card
	 */
	public BigTwoCard(int suit, int rank) {
		super(suit, rank);
	}
	
	/**
	 * method for comparing the order of this card with the specified card
	 * 
	 * @param card targeted card
	 * 
	 * @return 1 if this card is greater than
	 * 		   0 if equal
	 * 		   -1 if less than
	 */
	public int compareTo(Card card) {
		if (getRank() == 0) { // this is 'A'
			if (card.getRank() == 0) {
				if (getSuit() > card.getSuit()) { // both 'A' but greater suit
					return 1;
				} else if (getSuit() == card.getSuit()) { // same rank same suit
					return 0;
				}
			} else if (card.getRank() == 1) { // if this is 'A' and that is '2'
				return -1;
			} else { // that card smaller than 'A'
				return 1;
			}
		} else if (getRank() == 1) { // this is '2'
			if (card.getRank() == 1) {
				if (getSuit() > card.getSuit()) { // both '2' but greater suit
					return 1;
				} else if (getSuit() == card.getSuit()) { // same rank same suit
					return 0;
				}
			} else { // that card smaller than '2'
				return 1;
			}
		} else {
			if (card.getRank() != 0 && card.getRank() != 1) { // that card not 'A' or '2'
				if (this.getRank() > card.getRank()) { // greater rank
					return 1;
				} else if (this.getRank() == card.getRank()) { // same rank
					if (this.getSuit() > card.getSuit()) { // greater suit
						return 1;
					} else if (this.getSuit() == card.getSuit()) { // same card
						return 0;
					}
				}
			}
		}
		return -1; // smaller rank or same rank with smaller suit
	}
}
