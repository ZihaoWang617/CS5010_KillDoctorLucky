package killdoctorlucky.model.occupants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.interfaces.Movable;

/**
 * Represents the target character's pet in the Kill Doctor Lucky game.
 * The pet moves through the world following a depth-first traversal pattern,
 * making any space it occupies invisible to neighboring spaces.
 */
public class Pet implements Occupant, Movable {
  private final String name;
  private Room currentRoom;
  private List<Room> dfsPath;
  private int pathIndex;

  /**
   * Creates a new pet with the specified name in the given starting room.
   * The pet is automatically added to the starting room's occupants.
   * 
   * @param petName the name of the pet (e.g., "Fortune the Cat")
   * @param startingRoom the room where the pet begins
   * @throws IllegalArgumentException if name is null/empty or startingRoom is null
   */
  public Pet(String petName, Room startingRoom) {
    if (petName == null || petName.trim().isEmpty()) {
      throw new IllegalArgumentException("Pet name cannot be null or empty");
    }
    if (startingRoom == null) {
      throw new IllegalArgumentException("Starting room cannot be null");
    }

    this.name = petName.trim();
    this.currentRoom = startingRoom;
    this.dfsPath = new ArrayList<>();
    this.pathIndex = 0;


    startingRoom.addOccupant(this);
  }

  /**
   * Gets the name of this pet.
   * 
   * @return the pet's name, never null or empty
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the room where this pet is currently located.
   * 
   * @return the pet's current room, never null
   */
  @Override
  public Room getCurrentRoom() {
    return currentRoom;
  }

  /**
   * Moves the pet to the specified destination room.
   * This is used for player-controlled pet movement.
   * 
   * @param destination the room to move the pet to
   * @return true if the move was successful
   * @throws IllegalArgumentException if destination is null
   */
  @Override
  public boolean moveToRoom(Room destination) {
    if (destination == null) {
      throw new IllegalArgumentException("Destination room cannot be null");
    }

    // Remove from current room
    currentRoom.removeOccupant(this);

    // Add to destination room
    destination.addOccupant(this);
    this.currentRoom = destination;

    return true;
  }

  /**
   * Determines if this pet can be seen by another occupant.
   * Pets follow standard visibility rules - same room or line of sight.
   * 
   * @param other the occupant trying to see this pet
   * @param board the game board for visibility calculations
   * @return true if this pet is visible to the other occupant
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

    return board.calculateVisibility(other, this);
  }

  /**
   * Initializes the depth-first search path for wandering.
   * This should be called once at the start of the game with all rooms.
   * 
   * @param board the game board containing all rooms
   * @throws IllegalArgumentException if board is null
   */
  public void initializeDfsPath(Board board) {
    if (board == null) {
      throw new IllegalArgumentException("Board cannot be null");
    }
    
    this.dfsPath = calculateDfsPath(board, currentRoom);
    this.pathIndex = 0;
  }

  /**
   * Moves the pet to the next room in its depth-first traversal pattern.
   * If the DFS path hasn't been initialized, this method does nothing.
   * The pet cycles through the path continuously.
   */
  public void wanderNext() {
    
    pathIndex = (pathIndex + 1) % dfsPath.size();
    Room nextRoom = dfsPath.get(pathIndex);
    
    currentRoom.removeOccupant(this);
    nextRoom.addOccupant(this);
    this.currentRoom = nextRoom;
  }

  /**
   * Calculates a depth-first search path through all rooms starting from
   * the given starting room. This creates the wandering pattern for the pet.
   * 
   * @param board the game board
   * @param start the starting room for DFS
   * @return a list of rooms in DFS order
   */
  private List<Room> calculateDfsPath(Board board, Room start) {
    
    List<Room> path = new ArrayList<>();
    Set<Room> visited = new HashSet<>();
    Stack<Room> stack = new Stack<>();
    
    stack.push(start);
    
    while (!stack.isEmpty()) {
      Room current = stack.pop();
        
      if (visited.contains(current)) {
        continue;
      }
        
      visited.add(current);
      path.add(current);
        
      List<Room> neighbors = board.getAdjacentRooms(current);
        
      for (int i = neighbors.size() - 1; i >= 0; i--) {
        Room neighbor = neighbors.get(i);
        if (!visited.contains(neighbor)) {
          stack.push(neighbor);
        }
      }
    }
    return path;
  }

  @Override
  public String toString() {
    return String.format("Pet{name='%s', room='%s'}", name, currentRoom.getName());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Pet pet = (Pet) obj;
    return name.equals(pet.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}