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
  private int health;           // NEW for Milestone 3
  private final int maxHealth;  // NEW for Milestone 3

  /**
   * Creates Doctor Lucky in the specified starting room with initial health.
   * Doctor Lucky will move through the provided sequence of rooms automatically.
   * 
   * @param startingRoom the room where Doctor Lucky begins
   * @param initialHealth the starting health points
   * @throws IllegalArgumentException if startingRoom is null or health <= 0
   */
  public DoctorLucky(Room startingRoom, int initialHealth) {
    if (startingRoom == null) {
      throw new IllegalArgumentException("Starting room cannot be null");
    }
    if (initialHealth <= 0) {
      throw new IllegalArgumentException("Initial health must be positive");
    }

    this.currentRoom = startingRoom;
    this.movementSequence = new ArrayList<>();
    this.movementSequence.add(startingRoom);
    this.sequenceIndex = 0;
    this.health = initialHealth;
    this.maxHealth = initialHealth;

    // Add Doctor Lucky to the starting room
    startingRoom.addOccupant(this);
  }

  /**
   * Creates Doctor Lucky with default health of 50.
   * This maintains backward compatibility with existing code.
   * 
   * @param startingRoom the room where Doctor Lucky begins
   * @throws IllegalArgumentException if startingRoom is null
   */
  public DoctorLucky(Room startingRoom) {
    this(startingRoom, 50); // Default health
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
      return;
    }
    sequenceIndex = (sequenceIndex + 1) % movementSequence.size();
    Room nextRoom = movementSequence.get(sequenceIndex);
    
    moveToRoom(nextRoom);
   
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

  // ===== NEW METHODS FOR MILESTONE 3 =====

  /**
   * Reduces Doctor Lucky's health by the specified damage amount.
   * Health cannot go below zero.
   * 
   * @param damage the amount of damage to inflict
   * @throws IllegalArgumentException if damage is negative
   */
  public void takeDamage(int damage) {
    if (damage < 0) {
      throw new IllegalArgumentException("Damage cannot be negative");
    }
    this.health = Math.max(0, this.health - damage);
  }

  /**
   * Gets Doctor Lucky's current health points.
   * 
   * @return the current health, always non-negative
   */
  public int getHealth() {
    return health;
  }

  /**
   * Gets Doctor Lucky's maximum health points.
   * 
   * @return the maximum health
   */
  public int getMaxHealth() {
    return maxHealth;
  }

  /**
   * Checks if Doctor Lucky is still alive.
   * 
   * @return true if health > 0, false otherwise
   */
  public boolean isAlive() {
    return health > 0;
  }

  /**
   * Checks if Doctor Lucky is alone with a player considering the board's
   * visibility rules and pet location.
   * 
   * @param player the player attempting the murder
   * @param board the game board for visibility checks
   * @return true if alone with the player (no other players can see)
   * @throws IllegalArgumentException if player or board is null
   */
  public boolean isAloneWithPlayer(Player player, Board board) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }

    // Must be in the same room
    if (!currentRoom.equals(player.getCurrentRoom())) {
      return false;
    }

    // This will be checked more thoroughly in Game.attemptMurder()
    // considering other players' visibility
    return true;
  }

  @Override
  public String toString() {
    return String.format("DoctorLucky{room='%s', health=%d/%d, sequenceIndex=%d}", 
        currentRoom.getName(), health, maxHealth, sequenceIndex);
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