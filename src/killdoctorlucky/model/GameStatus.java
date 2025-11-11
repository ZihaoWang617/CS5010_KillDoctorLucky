package killdoctorlucky;

/**
 * Enumeration representing the current lifecycle state of a Kill Doctor Lucky
 * game. The game status controls which actions are valid and how the game logic
 * should behave during different phases of gameplay.
 */
public enum GameStatus {
  /**
   * Game is being set up, players added but not started. During this phase,
   * initial cards are dealt and starting positions are established.
   */
  SETUP,
  /*
   * Game is actively being played All normal gameplay actions such as movement,
   * card playing, and murder attempts are valid.
   */
  IN_PROGRESS,
  /**
   * Game has ended (someone won or game terminated) No further gameplay actions
   * are allowed and a winner may be declared.
   */
  FINISHED
}
