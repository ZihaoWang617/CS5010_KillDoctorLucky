package killdoctorlucky.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import killdoctorlucky.model.occupants.Occupant;

/**
 * Represents the game board containing all rooms and their spatial
 * relationships. The board manages room connections, sight lines, and
 * visibility calculations for the Kill Doctor Lucky game world.
 */
public class Board {
  private final Map<String, Room> rooms;
  private final Map<String, List<String>> sightLines;

  /**
   * Creates a new game board with empty room and sight line collections. Rooms
   * and connections must be added separately after construction.
   */
  public Board() {
    this.rooms = new HashMap<>();
    this.sightLines = new HashMap<>();
  }

  /**
   * Adds a room to the board with the specified name.
   * 
   * @param room the room to add to the board
   * @throws IllegalArgumentException if room is null or a room with the same name
   *                                  already exists
   */
  public void addRoom(Room room) {
    if (room == null) {
      throw new IllegalArgumentException("Room cannot be null");
    }
    if (rooms.containsKey(room.getName())) {
      throw new IllegalArgumentException("Room with name '" + room.getName() + "' already exists");
    }
    rooms.put(room.getName(), room);
  }

  /**
   * Gets a room by its name.
   * 
   * @param name the name of the room to retrieve
   * @return the room with the specified name, or null if no such room exists
   * @throws IllegalArgumentException if name is null or empty
   */
  public Room getRoom(String name) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Room name cannot be null or empty");
    }
    return rooms.get(name.trim());
  }

  /**
   * Gets all rooms that are directly connected to the specified room. Connected
   * rooms are those that share a border and allow direct movement.
   * 
   * @param room the room to find connections for
   * @return a list of rooms connected to the specified room
   * @throws IllegalArgumentException if room is null or not on this board
   */
  public List<Room> getAdjacentRooms(Room room) {
    if (room == null) {
      throw new IllegalArgumentException("Room cannot be null");
    }
    if (!rooms.containsValue(room)) {
      throw new IllegalArgumentException("Room is not on this board");
    }

    return room.getConnections();
  }

  /**
   * Gets all rooms on this board.
   * 
   * @return a collection containing all rooms on the board
   */
  public Collection<Room> getAllRooms() {
    return new ArrayList<>(rooms.values());
  }

  /**
   * Checks if movement is valid between two rooms. Movement is valid if the rooms
   * are directly connected.
   * 
   * @param from the room to move from
   * @param to   the room to move to
   * @return true if movement is valid, false otherwise
   * @throws IllegalArgumentException if either room is null
   */
  public boolean isValidMove(Room from, Room to) {
    if (from == null) {
      throw new IllegalArgumentException("From room cannot be null");
    }
    if (to == null) {
      throw new IllegalArgumentException("To room cannot be null");
    }

    return from.getConnections().contains(to);
  }

  /**
   * Calculates whether one occupant can see another occupant. Visibility is based
   * on being in the same room or having line of sight through connected rooms
   * based on the sight line configuration.
   * 
   * @param viewer the occupant trying to see
   * @param target the occupant being looked for
   * @return true if the viewer can see the target, false otherwise
   * @throws IllegalArgumentException if viewer or target is null
   */
  public boolean calculateVisibility(Occupant viewer, Occupant target) {
    if (viewer == null) {
      throw new IllegalArgumentException("Viewer cannot be null");
    }
    if (target == null) {
      throw new IllegalArgumentException("Target cannot be null");
    }

    Room viewerRoom = viewer.getCurrentRoom();
    Room targetRoom = target.getCurrentRoom();

    // Same room - always visible
    if (viewerRoom.equals(targetRoom)) {
      return true;
    }

    // Check sight lines between rooms
    String viewerRoomName = viewerRoom.getName();
    List<String> visibleRoomNames = sightLines.get(viewerRoomName);

    if (visibleRoomNames != null) {
      return visibleRoomNames.contains(targetRoom.getName());
    }

    // Default: can only see occupants in connected rooms
    return viewerRoom.getConnections().contains(targetRoom);
  }

  /**
   * Establishes a connection between two rooms, allowing movement between them.
   * This creates a bidirectional connection - both rooms can reach each other.
   * 
   * @param room1 the first room to connect
   * @param room2 the second room to connect
   * @throws IllegalArgumentException if either room is null or not on this board
   */
  public void connectRooms(Room room1, Room room2) {
    if (room1 == null || room2 == null) {
      throw new IllegalArgumentException("Rooms cannot be null");
    }
    if (!rooms.containsValue(room1) || !rooms.containsValue(room2)) {
      throw new IllegalArgumentException("Both rooms must be on this board");
    }

    room1.addConnection(room2);
    room2.addConnection(room1);
  }

  /**
   * Sets up sight lines between rooms for visibility calculations. Sight lines
   * determine which rooms can see into other rooms beyond direct connections.
   * 
   * @param fromRoom     the room that can see
   * @param visibleRooms list of room names that can be seen from the fromRoom
   * @throws IllegalArgumentException if fromRoom is null or not on board
   */
  public void setSightLines(String fromRoom, List<String> visibleRooms) {
    if (fromRoom == null || fromRoom.trim().isEmpty()) {
      throw new IllegalArgumentException("From room name cannot be null or empty");
    }
    if (!rooms.containsKey(fromRoom)) {
      throw new IllegalArgumentException("From room must exist on this board");
    }
    if (visibleRooms == null) {
      throw new IllegalArgumentException("Visible rooms list cannot be null");
    }

    sightLines.put(fromRoom, new ArrayList<>(visibleRooms));
  }

  /**
   * Gets the number of rooms on this board.
   * 
   * @return the total number of rooms
   */
  public int getRoomCount() {
    return rooms.size();
  }
  
  /** Returns the list of rooms visible from the given room name (copy). 
   * @param fromRoom the name of the room whose visible rooms should be returned
   * @return a list of room names visible from the specified room (never null)
   * @throws IllegalArgumentException if {@code fromRoom} is null or empty
   */
  public List<String> getSightLinesFrom(String fromRoom) {
    if (fromRoom == null || fromRoom.trim().isEmpty()) {
      throw new IllegalArgumentException("From room name cannot be null or empty");
    }
    List<String> v = sightLines.get(fromRoom.trim());
    return v == null ? new ArrayList<>() : new ArrayList<>(v);
  }

  /**
   * Returns a read-only snapshot of all sight lines in the board.
   * Each key is a room name, and each value is the list of rooms visible from it.
   *
   * @return a map of room names to their visible rooms (deep copy)
   */
  public Map<String, List<String>> getAllSightLines() {
    Map<String, List<String>> copy = new HashMap<>();
    for (Map.Entry<String, List<String>> e : sightLines.entrySet()) {
      copy.put(e.getKey(), new ArrayList<>(e.getValue()));
    }
    return copy;
  }

  @Override
  public String toString() {
    return String.format("Board{rooms=%d, sightLines=%d}", rooms.size(), sightLines.size());
  }
  
  /**
   * Checks if a room is blocked by the pet, making it invisible to neighboring rooms.
   * A room with the pet cannot be seen into by players in adjacent rooms.
   * 
   * @param room the room to check
   * @param pet the pet that may be blocking visibility
   * @return true if the pet is in the room (making it invisible to neighbors)
   * @throws IllegalArgumentException if room or pet is null
   */
  public boolean isRoomBlockedByPet(Room room, killdoctorlucky.model.occupants.Pet pet) {
    if (room == null) {
      throw new IllegalArgumentException("Room cannot be null");
    }
    if (pet == null) {
      throw new IllegalArgumentException("Pet cannot be null");
    }

    // A room is blocked if the pet is currently in it
    return pet.getCurrentRoom().equals(room);
  }

  /**
   * Gets all rooms that are neighbors of the specified room.
   * This is an alias for getAdjacentRooms for clarity in pet-related logic.
   * 
   * @param room the room to get neighbors for
   * @return list of neighboring rooms
   * @throws IllegalArgumentException if room is null
   */
  public List<Room> getNeighboringRooms(Room room) {
    return getAdjacentRooms(room);
  }

  /**
   * Checks if a player in fromRoom can see into toRoom.
   * Takes into account pet location - if pet is in toRoom, it cannot be seen into.
   * 
   * @param fromRoom the room the viewer is in
   * @param toRoom the room being looked into
   * @param pet the pet that may block visibility
   * @return true if toRoom can be seen from fromRoom
   * @throws IllegalArgumentException if any parameter is null
   */
  public boolean canSeeIntoRoom(Room fromRoom, Room toRoom, 
      killdoctorlucky.model.occupants.Pet pet) {
    if (fromRoom == null || toRoom == null) {
      throw new IllegalArgumentException("Rooms cannot be null");
    }
    if (pet == null) {
      throw new IllegalArgumentException("Pet cannot be null");
    }

    // Same room - always can see
    if (fromRoom.equals(toRoom)) {
      return true;
    }

    // If pet is in the target room, it's blocked from neighbors
    if (isRoomBlockedByPet(toRoom, pet)) {
      return false;
    }

    // Otherwise check if rooms are adjacent
    return fromRoom.getConnections().contains(toRoom);
  }
  
}