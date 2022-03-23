/**
 * This class is used to model a hand of Triple
 * 
 * @author wingk
 */
public class Triple extends Hand {
  private static final long serialVersionUID = 2109395258622002051L;

  /**
	 * a constructor for building Triple
	 * 
	 * @param player who plays this hand
	 * @param cards hand that player plays
	 */
	public Triple(CardGamePlayer player, CardList cards) {
		super(player, cards);
	}
	
	/**
	 * check if this hand is a Triple
	 */
	public boolean isValid() {
		if (size() == 3 && getCard(0).getRank() == getCard(1).getRank()
				&& getCard(1).getRank() == getCard(2).getRank())
			return true;
		return false;
	}
	
	/**
	 * a method for returning a string specifying the type of this hand
	 * 
	 * @return type of this hand
	 */
	public String getType() { return "Triple"; }
}
