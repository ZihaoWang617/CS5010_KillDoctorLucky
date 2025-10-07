/**
 * Interface for objects that can be played as cards in the game.
 */
public interface Playable {
/**
 * 
 */
  void execute(Game game, Player player);
  
  /**
   * get the name of the card
   * @return card's name
   */
  String getName();
  
  /**
   * get description of card
   * @return card's description
   */
  String getDescription();
}
