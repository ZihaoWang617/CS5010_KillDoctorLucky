package killdoctorlucky;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a room in the Kill Doctor Lucky game world. Each room has a name,
 * coordinates, and can contain occupants and items.
 */
public class Room {
  private final String name;
  private final boolean isNamedRoom;
  private final List<Room> connectedRooms;
  private final List<Occupant> occupants;
  private final List<Item> items;

  /**
   * Creates a new room with the specified name and type.
   * 
   * @param roomName    the name of the room
   * @param isNamed true if this is a named room, false if it's a hallway
   * @throws IllegalArgumentException if name is null or empty
   */
  public Room(String roomName, boolean isNamed) {
    if (roomName == null || roomName.trim().isEmpty()) {
      throw new IllegalArgumentException("Room name cannot be null or empty");
    }
    this.name = roomName.trim();
    this.isNamedRoom = isNamed;
    this.connectedRooms = new ArrayList<>();
    this.occupants = new ArrayList<>();
    this.items = new ArrayList<>();
  }

  /**
   * Gets the name of this room.
   * 
   * @return the room's name, never null
   */
  public String getName() {
    return name;
  }

  /**
   * Checks if this is a named room or a hallway/corridor.
   * 
   * @return true if this is a named room, false if it's a hallway
   */
  public boolean isNamedRoom() {
    return isNamedRoom;
  }

  /**
   * Adds an occupant to this room. The occupant will be added to the room's
   * occupant list if not already present.
   * 
   * @param occupant the occupant to add to this room
   * @throws IllegalArgumentException if occupant is null
   */
  public void addOccupant(Occupant occupant) {
    if (occupant == null) {
      throw new IllegalArgumentException("Occupant cannot be null");
    }
    if (!occupants.contains(occupant)) {
      occupants.add(occupant);
    }
  }

  /**
   * Removes an occupant from this room. If the occupant is not in this room, no
   * action is taken.
   * 
   * @param occupant the occupant to remove from this room
   * @throws IllegalArgumentException if occupant is null
   */
  public void removeOccupant(Occupant occupant) {
    if (occupant == null) {
      throw new IllegalArgumentException("Occupant cannot be null");
    }
    occupants.remove(occupant);
  }

  /**
   * Gets a copy of the list of occupants currently in this room. Modifying the
   * returned list will not affect the room's occupant list.
   * 
   * @return a new list containing all occupants in this room
   */
  public List<Occupant> getOccupants() {
    return new ArrayList<>(occupants);
  }

  /**
   * Checks if a specific occupant is currently in this room.
   * 
   * @param occupant the occupant to check for
   * @return true if the occupant is in this room, false otherwise
   * @throws IllegalArgumentException if occupant is null
   */
  public boolean isOccupiedBy(Occupant occupant) {
    if (occupant == null) {
      throw new IllegalArgumentException("Occupant cannot be null");
    }
    return occupants.contains(occupant);
  }

  /**
   * Gets a copy of the list of rooms connected to this room. Connected rooms are
   * those that players can move between directly.
   * 
   * @return a new list containing all connected rooms
   */
  public List<Room> getConnections() {
    return new ArrayList<>(connectedRooms);
  }

  /**
   * Counts the number of players currently in this room. This excludes non-player
   * occupants like Doctor Lucky.
   * 
   * @return the number of Player objects in this room
   */
  public int getPlayerCount() {
    int playerCount = 0;
    for (Occupant occupant : occupants) {
      if (occupant instanceof Player) {
        playerCount++;
      }
    }
    return playerCount;
  }

  /**
   * Gets a copy of the list of items currently in this room. Modifying the
   * returned list will not affect the room's item list.
   * 
   * @return a new list containing all items in this room
   */
  public List<Item> getItems() {
    return new ArrayList<>(items);
  }

  /**
   * Adds an item to this room. The item will be placed in this room and its
   * location will be updated.
   * 
   * @param item the item to add to this room
   * @throws IllegalArgumentException if item is null
   */
  public void addItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null");
    }
    if (!items.contains(item)) {
      items.add(item);
      item.setRoom(this);
    }
  }

  /**
   * Removes an item from this room.
   * 
   * @param item the item to remove
   * @return true if the item was removed, false if it wasn't in this room
   * @throws IllegalArgumentException if item is null
   */
  public boolean removeItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null");
    }
    boolean removed = items.remove(item);
    if (removed) {
      item.setRoom(null);
    }
    return removed;
  }

  /**
   * Adds a connection between this room and another room. This is typically
   * called during world initialization to set up room adjacencies.
   * 
   * @param room the room to connect to this room
   * @throws IllegalArgumentException if room is null or is the same as this room
   */
  public void addConnection(Room room) {
    if (room == null) {
      throw new IllegalArgumentException("Connected room cannot be null");
    }
    if (room == this) {
      throw new IllegalArgumentException("Room cannot connect to itself");
    }
    if (!connectedRooms.contains(room)) {
      connectedRooms.add(room);
    }
  }

  @Override
  public String toString() {
    return String.format("Room{name='%s', occupants=%d, connections=%d}", name, occupants.size(),
        connectedRooms.size());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    Room room = (Room) obj;
    return name.equals(room.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}