package killdoctorlucky;

/**
 * Interface for entities that can occupy rooms and interact with the visibility
 * system. Occupants are game objects that have a physical presence in rooms and
 * can potentially be seen by other occupants based on line-of-sight rules and
 * room connections.
 */
public interface Occupant {
  /**
   * Get the current room where this occupant is located.
   * 
   * @return the current room containing this occupant, never null
   */
  Room getCurrentRoom();

  /**
   * Determine if this occupant can be seen by another occupant. Visibility is
   * calculated based on room connections, line-of-sight rules, and the current
   * positions of both occupants on the game board.
   * 
   * @param other the other occupant trying to see this one
   * @param board the game board for line of sight calculations
   * @return true if this occupant can be seen by the other, false otherwise
   * @throws IllegalArgumentException if other or board is null
   */
  boolean canBeSeenBy(Occupant other, Board board);
}