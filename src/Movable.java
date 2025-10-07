/**
 * Interface for entites that can move between rooms in the game.
 */

public interface Movable {
  /**
   * Move this entity to the specified destination room.
   * @param destination the room to move to
   * @return true if moved successfully, false otherwise
   */
  boolean moveToRoom(Room destination);
  
  /**
   * get the current room where this eneity is located.
   * @return room's name
   */
  Room getCurrentRoom();
}
