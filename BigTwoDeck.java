/**
 * This class is used to model a deck of cards used in a Big Two card game
 * 
 * @author wingk
 */
public class BigTwoDeck extends Deck {	
  private static final long serialVersionUID = 8254481884293570270L;

  /**
	 * Initialize the deck of cards
	 */
	public void initialize() {
		removeAllCards();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 13; j++)
				addCard(new BigTwoCard(i, j));
	}

}
