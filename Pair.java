/**
 * This class is used to model a hand of Pair
 * 
 * @author wingk
 */
public class Pair extends Hand {
  private static final long serialVersionUID = -7735787877198652522L;

  /**
	 * a constructor for building Single
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public Pair(CardGamePlayer player, CardList cards){
		super(player, cards);
	}
	
	/**
	 * check if this hand is a Pair
	 */
	public boolean isValid() {
		// 2 card and same rank
		if (size() == 2 && getCard(0).getRank() == getCard(1).getRank())
			return true;
		return false;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public String getType() { return "Pair"; }
}
