package killdoctorlucky;

/**
 * Interface for game objects that can be played as cards in the game. Playable
 * objects represent cards that player can execute to affect the game state,
 * such as weapon cards, movement cards, or special ability cards.
 */
public interface Playable {
  /**
   * execute this card's effect on the game state. this method is called when a
   * player plays thsi card during their turn.
   * 
   * @param game   the current game instance that will be affected.
   * @param player the playe who is playing this card.
   * @throws IllegalArgumentException if game or player is null
   */
  void execute(Game game, Player player);

  /**
   * get the name of the card.
   * 
   * @return card's name, never null or empty
   */
  String getName();

  /**
   * get a detailed description of this card's effect.
   * 
   * @return card's description explaining what it does, never null
   */
  String getDescription();
}
