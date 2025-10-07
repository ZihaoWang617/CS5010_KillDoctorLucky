/**
 * Interface for entities that can occupy rooms and be seen by others.
 */
public interface Occupant {
  /**
   *Get the current room where this occupant is located.
   * @return the current room
   */
  Room getCurrentRoom();
  /**
   * Determine if this occupant can be seen by another occupant.
   * @param other the other occupant trying to see this one
   * @param board the game board for line of sight calculations
   * @return true if this occupant can be seen by the other, false otherwise
   */
  boolean canBeSeenBy(Occupant other, Board board);
}