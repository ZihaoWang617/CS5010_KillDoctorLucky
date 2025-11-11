package killdoctorlucky.model;

import java.util.ArrayList;
import java.util.List;
import killdoctorlucky.model.occupants.Occupant;


/**
 * Represents a room in the Kill Doctor Lucky game.
 * Each room has a name, may contain occupants and items,
 * and can be connected to neighboring rooms.
 */
public class Room {

  private final String name;
  private final boolean isNamedRoom;
  private final List<Room> connectedRooms;
  private final List<Occupant> occupants;
  private final List<Item> items = new ArrayList<>();
  private int colIndex = 0;
  private int rowIndex = 0;
  private int width = 4;
  private int height = 3;

  /**
   * Constructs a room with the given name.
   *
   * @param roomName  the room name
   * @param isNamed   whether this is a named room
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
   * Adds a connected neighboring room.
   *
   * @param other the neighboring room to connect to
   */
  public void addConnection(Room other) {
    if (other == null || other == this || connectedRooms.contains(other)) {
      return;
    }
    connectedRooms.add(other);
  }

  /**
   * Gets the list of connected neighboring rooms.
   *
   * @return a copy of connected rooms list
   */
  public List<Room> getConnections() {
    return new ArrayList<>(connectedRooms);
  }

  /**
   * Adds an occupant (player or Doctor Lucky) to this room.
   *
   * @param occ the occupant to add
   */
  public void addOccupant(Occupant occ) {
    if (occ != null && !occupants.contains(occ)) {
      occupants.add(occ);
    }
  }

  /**
   * Removes an occupant from this room.
   *
   * @param occ the occupant to remove
   */
  public void removeOccupant(Occupant occ) {
    occupants.remove(occ);
  }

  /**
   * Gets a list of occupants currently in this room.
   *
   * @return a copy of occupants list
   */
  public List<Occupant> getOccupants() {
    return new ArrayList<>(occupants);
  }

  /**
   * Returns a copy of items currently in this room.
   *
   * @return list of items
   */
  public List<Item> getItems() {
    return new ArrayList<>(items);
  }

  /**
   * Adds an item to this room.
   *
   * @param item the item to add
   */
  public void addItem(Item item) {
    if (item == null) {
      return;
    }
    items.add(item);
    item.setRoom(this);
  }

  /**
   * Removes the first item with the given name (case-sensitive) from this room.
   *
   * @param itemName the name of the item to remove
   * @return the removed Item, or null if not found
   * @throws IllegalArgumentException if itemName is null or blank
   */
  public Item removeItem(String itemName) {
    if (itemName == null || itemName.trim().isEmpty()) {
      throw new IllegalArgumentException("itemName must not be null or blank");
    }
    for (int i = 0; i < items.size(); i++) {
      Item it = items.get(i);
      if (itemName.equals(it.getName())) {
        items.remove(i);
        it.setRoom(null);
        return it;
      }
    }
    return null;
  }

  /**
   * Checks whether this is a named room.
   *
   * @return true if this is a named room
   */
  public boolean isNamedRoom() {
    return isNamedRoom;
  }

  /**
   * Returns the number of occupants currently in this room.
   *
   * @return number of occupants
   */
  public int getPlayerCount() {
    return occupants.size();
  }
  
  /**
   * Checks if the specified occupant is in this room.
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
   * Sets the room geometry using world coordinates.
   * Treats {@code x} as column and {@code y} as row (top-left origin).
   *
   * @param xcord      left/top column index
   * @param ycord      top/left row index
   * @param widthMap  width in world units (must be &gt; 0)
   * @param heightMap height in world units (must be &gt; 0)
   * @throws IllegalArgumentException if width or height are not positive
   */
  public void setGeometry(int xcord, int ycord, int widthMap, int heightMap) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Room width/height must be positive");
    }
    this.colIndex = xcord;
    this.rowIndex = ycord;
    this.width = widthMap;
    this.height = heightMap;
  }

  /**
   * Sets the room geometry using inclusive corner coordinates as defined
   * by the world file: {@code (row1, col1)} (top-left) and
   * {@code (row2, col2)} (bottom-right), both inclusive.
   * <p>Width/height are computed as {@code (col2 - col1 + 1)} and
   * {@code (row2 - row1 + 1)} respectively.</p>
   *
   * @param row1 top row (inclusive)
   * @param col1 left column (inclusive)
   * @param row2 bottom row (inclusive)
   * @param col2 right column (inclusive)
   * @throws IllegalArgumentException if computed width/height are not positive
   */
  public void setGeometryByCorners(int row1, int col1, int row2, int col2) {
    int w = (col2 - col1) + 1;
    int h = (row2 - row1) + 1;
    setGeometry(col1, row1, w, h); // x = col, y = row
  }

  /**
   * Returns the left/top column index (x).
   *
   * @return the x coordinate in world units
   */
  public int getX() {
    return colIndex;
  }

  /**
   * Returns the top/left row index (y).
   *
   * @return the y coordinate in world units
   */
  public int getY() {
    return rowIndex;
  }

  /**
   * Returns the room width in world units.
   *
   * @return width in world units
   */
  public int getWidth() {
    return width;
  }

  /**
   * Returns the room height in world units.
   *
   * @return height in world units
   */
  public int getHeight() {
    return height;
  }


  @Override
  public String toString() {
    return "Room{" + name + ", occupants=" + occupants.size() + ", items=" + items.size() + "}";
  }
}