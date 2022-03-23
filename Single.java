/**
 * This class is used to model a hand of Single
 * 
 * @author wingk
 */
public class Single extends Hand {
  private static final long serialVersionUID = 304049125823184020L;

  /**
	 * a constructor for building Single
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public Single(CardGamePlayer player, CardList cards){
		super(player, cards);
	}
	
	/**
	 * check if this hand is a Single
	 */
	public boolean isValid() {
		if (size() == 1)
			return true;
		return false;
	}
	
	/**
	 * a method for retrieving the top card of this hand
	 * 
	 * @return the top card of this hand
	 */
	public Card getTopCard() { return getCard(0); }
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public String getType() { return "Single"; }
}
