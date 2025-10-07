package killdoctorlucky;

/**
 * Interface for game entities that can move between different rooms in the
 * world. This interface provides movement capabilities for objects like players
 * and NPCs that need to navigate through the game's room-based world structure.
 */

public interface Movable {
  /**
   * Attempts to move this entity to the specified destination room. The move may
   * fail if the destination is invalid, unreachable, or violates game rules such
   * as movement restrictions.
   * 
   * @param destination the room where this entity should move
   * @return true if the move was successful and the entity is now in the
   *         destination room, false if the move failed for any reason
   * @throws IllegalArgumentException if destination is null
   */
  boolean moveToRoom(Room destination);

  /**
   * get the current room where this eneity is located.
   * 
   * @return the current room containing this entity, never null
   */
  Room getCurrentRoom();
}
