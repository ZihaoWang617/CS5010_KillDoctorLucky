package killdoctorlucky.model.occupants;

import java.util.ArrayList;
import java.util.List;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.interfaces.Movable;

/**
 * Represents Doctor Lucky, the target character in the Kill Doctor Lucky game.
 * Doctor Lucky moves automatically through rooms in a predetermined sequence
 * and can be murdered by players under the right conditions.
 */
public class DoctorLucky implements Movable, Occupant {
  private Room currentRoom;
  private List<Room> movementSequence;
  private int sequenceIndex;

  /**
   * Creates Doctor Lucky in the specified starting room with a movement sequence.
   * Doctor Lucky will move through the provided sequence of rooms automatically.
   * 
   * @param startingRoom the room where Doctor Lucky begins
   * @throws IllegalArgumentException if startingRoom is null
   */
  public DoctorLucky(Room startingRoom) {
    if (startingRoom == null) {
      throw new IllegalArgumentException("Starting room cannot be null");
    }

    this.currentRoom = startingRoom;
    this.movementSequence = new ArrayList<>();
    this.movementSequence.add(startingRoom);
    this.sequenceIndex = 0;

    // Add Doctor Lucky to the starting room
    startingRoom.addOccupant(this);
  }

  /**
   * Gets the room where Doctor Lucky is currently located.
   * 
   * @return Doctor Lucky's current room, never null
   */
  @Override
  public Room getCurrentRoom() {
    return currentRoom;
  }

  /**
   * Moves Doctor Lucky to the next room in the predetermined sequence. This
   * method is called automatically during each game turn. If no movement sequence
   * is set, Doctor Lucky remains in the current room.
   */
  public void moveNext() {
    if (movementSequence == null || movementSequence.isEmpty()) {
      return; // No movement sequence defined
    }

    // Move to next room in sequence
    sequenceIndex = (sequenceIndex + 1) % movementSequence.size();
    Room nextRoom = movementSequence.get(sequenceIndex);

    // Update room occupancy
    currentRoom.removeOccupant(this);
    nextRoom.addOccupant(this);
    currentRoom = nextRoom;
  }

  /**
   * Sets the movement sequence for Doctor Lucky. Doctor Lucky will move through
   * these rooms in order.
   * 
   * @param sequence the ordered list of rooms to visit
   * @throws IllegalArgumentException if sequence is null or empty
   */
  public void setMovementSequence(List<Room> sequence) {
    if (sequence == null || sequence.isEmpty()) {
      throw new IllegalArgumentException("Movement sequence cannot be null or empty");
    }
    this.movementSequence = new ArrayList<>(sequence);

    // Find current room's position in the sequence
    for (int i = 0; i < this.movementSequence.size(); i++) {
      if (this.movementSequence.get(i).equals(currentRoom)) {
        this.sequenceIndex = i;
        return;
      }
    }

    // If current room not found, start from beginning
    this.sequenceIndex = 0;
  }

  /**
   * Attempts to move Doctor Lucky to a specific room. This is used for special
   * game mechanics or direct placement.
   * 
   * @param destination the room to move Doctor Lucky to
   * @return true if the move was successful, false otherwise
   * @throws IllegalArgumentException if destination is null
   */
  @Override
  public boolean moveToRoom(Room destination) {
    if (destination == null) {
      throw new IllegalArgumentException("Destination room cannot be null");
    }

    // Remove from current room and add to destination
    currentRoom.removeOccupant(this);
    destination.addOccupant(this);
    this.currentRoom = destination;

    return true;
  }

  /**
   * Checks if Doctor Lucky is alone with a specific player. This is used to
   * determine if a murder attempt is possible. Doctor Lucky is alone with a
   * player if they are the only two occupants in the room.
   * 
   * @param player the player to check for isolation with
   * @return true if Doctor Lucky and the player are alone together, false
   *         otherwise
   * @throws IllegalArgumentException if player is null
   */
  public boolean isAloneWith(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    // Check if they're in the same room
    if (!currentRoom.equals(player.getCurrentRoom())) {
      return false;
    }

    // Check if there are exactly 2 occupants (Doctor Lucky and the player)
    List<Occupant> occupants = currentRoom.getOccupants();
    return occupants.size() == 2 && occupants.contains(this) && occupants.contains(player);
  }

  /**
   * Determines if Doctor Lucky can be seen by another occupant. Visibility
   * follows the same rules as other occupants.
   * 
   * @param other the occupant trying to see Doctor Lucky
   * @param board the game board for visibility calculations
   * @return true if Doctor Lucky is visible to the other occupant, false
   *         otherwise
   * @throws IllegalArgumentException if other or board is null
   */
  @Override
  public boolean canBeSeenBy(Occupant other, Board board) {
    if (other == null) {
      throw new IllegalArgumentException("Other occupant cannot be null");
    }
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }

    // If in the same room, always visible
    if (this.getCurrentRoom().equals(other.getCurrentRoom())) {
      return true;
    }

    // Otherwise, use board's visibility calculation
    return board.calculateVisibility(other, this);
  }

  @Override
  public String toString() {
    return String.format("DoctorLucky{room='%s', sequenceIndex=%d}", currentRoom.getName(),
        sequenceIndex);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    // Doctor Lucky is a singleton, so instance equality is sufficient
    return false; // Only one Doctor Lucky exists per game
  }

  @Override
  public int hashCode() {
    return "DoctorLucky".hashCode(); // Consistent hash for singleton
  }
}