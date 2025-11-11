package killdoctorlucky.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;

/**
 * Parses world specification files to create game worlds. Reads the mansion
 * layout from a text file and constructs the corresponding Board.
 */
public class WorldParser {

  /**
   * Parses a world specification from a Readable source.
   * 
   * @param readable the source containing world specification
   * @return a WorldData object containing the board and ordered room list
   * @throws IllegalArgumentException if readable is null or format is invalid
   * @throws IOException              if reading fails
   */
  public static WorldData parseWorld(Readable readable) throws IOException {
    if (readable == null) {
      throw new IllegalArgumentException("Readable cannot be null");
    }

    BufferedReader reader = new BufferedReader(
        readable instanceof Reader ? (Reader) readable : new Reader() {
          @Override
          public int read(char[] cbuf, int off, int len) throws IOException {
            return ((Readable) readable).read(java.nio.CharBuffer.wrap(cbuf, off, len));
          }

          @Override
          public void close() throws IOException {
            // Readable doesn't have close method
          }
        });

    try {
      // Step 1: Parse world dimensions and name
      String worldLine = reader.readLine();
      if (worldLine == null) {
        throw new IllegalArgumentException("Empty world specification");
      }
      String[] worldParts = worldLine.trim().split("\\s+", 3);
      if (worldParts.length < 3) {
        throw new IllegalArgumentException("Invalid world line format");
      }

      Integer.parseInt(worldParts[0]); // rows
      Integer.parseInt(worldParts[1]); // cols
      // String worldName = worldParts[2]; // will be used later

      // Step 2: Parse target character (Doctor Lucky)
      String targetLine = reader.readLine();
      if (targetLine == null) {
        throw new IllegalArgumentException("Missing target character line");
      }
      String[] targetParts = targetLine.trim().split("\\s+", 2);
      if (targetParts.length < 2) {
        throw new IllegalArgumentException("Invalid target format");
      }
      final int targetHealth = Integer.parseInt(targetParts[0]);
      
      // Step 2.5: Parse pet (NEW for Milestone 3)
      String petLine = reader.readLine();
      String petName = null;
      if (petLine != null && !petLine.trim().isEmpty()) {
        petName = petLine.trim();
      }

      // Step 3: rooms count
      String roomCountLine = reader.readLine();
      if (roomCountLine == null) {
        throw new IllegalArgumentException("Missing room count");
      }
      int numRooms = Integer.parseInt(roomCountLine.trim());

      // Step 4: rooms
      List<RoomData> roomDataList = new ArrayList<>();
      for (int i = 0; i < numRooms; i++) {
        String roomLine = reader.readLine();
        if (roomLine == null) {
          throw new IllegalArgumentException("Missing room data at index " + i);
        }
        String[] parts = roomLine.trim().split("\\s+", 5);
        if (parts.length < 5) {
          throw new IllegalArgumentException("Invalid room format: " + roomLine);
        }

        int row1 = Integer.parseInt(parts[0]);
        int col1 = Integer.parseInt(parts[1]);
        int row2 = Integer.parseInt(parts[2]);
        int col2 = Integer.parseInt(parts[3]);
        String roomName = parts[4];

        roomDataList.add(new RoomData(row1, col1, row2, col2, roomName));
      }

      // Step 5: items count
      String itemCountLine = reader.readLine();
      if (itemCountLine == null) {
        throw new IllegalArgumentException("Missing item count");
      }
      int numItems = Integer.parseInt(itemCountLine.trim());

      // Step 6: items
      List<ItemData> itemDataList = new ArrayList<>();
      for (int i = 0; i < numItems; i++) {
        String itemLine = reader.readLine();
        if (itemLine == null) {
          throw new IllegalArgumentException("Missing item data at index " + i);
        }

        String[] itemParts = itemLine.trim().split("\\s+", 3);
        if (itemParts.length < 3) {
          throw new IllegalArgumentException("Invalid item format: " + itemLine);
        }

        int roomIndex = Integer.parseInt(itemParts[0]);
        int damage = Integer.parseInt(itemParts[1]);
        String itemName = itemParts[2];

        itemDataList.add(new ItemData(roomIndex, itemName, damage));
      }

      // Step 7: build board & rooms
      Board board = new Board();
      List<Room> rooms = new ArrayList<>();

      for (RoomData data : roomDataList) {
        Room room = new Room(data.name, true);
        // Bind geometry for map rendering (inclusive corners).
        room.setGeometryByCorners(data.row1, data.col1, data.row2, data.col2);
        rooms.add(room);
        board.addRoom(room);
      }

      // Step 8: connect neighboring rooms (share edge)
      for (int i = 0; i < roomDataList.size(); i++) {
        for (int j = i + 1; j < roomDataList.size(); j++) {
          RoomData a = roomDataList.get(i);
          RoomData b = roomDataList.get(j);
          if (areNeighbors(a, b)) {
            board.connectRooms(rooms.get(i), rooms.get(j));
          }
        }
      }

      // Step 9: place items
      for (ItemData itemData : itemDataList) {
        if (itemData.roomIndex < 0 || itemData.roomIndex >= rooms.size()) {
          throw new IllegalArgumentException(
              "Invalid room index " + itemData.roomIndex + " for item " + itemData.name);
        }
        Item item = new Item(itemData.name, itemData.damage);
        rooms.get(itemData.roomIndex).addItem(item);
      }
      reader.mark(4096);
      String maybeSight = reader.readLine();
      if (maybeSight != null && maybeSight.trim().equalsIgnoreCase("SIGHT")) {
        String line;
        while ((line = reader.readLine()) != null) {
          String trimmed = line.trim();
          if ("END".equalsIgnoreCase(trimmed)) {
            break;
          }
          if (trimmed.isEmpty()) {
            continue;
          }

          String[] pair = trimmed.split(":");
          if (pair.length != 2) {
            // skip malformed line, but continue parsing the rest
            continue;
          }
          String from = pair[0].trim();
          String[] toParts = pair[1].split(",");
          List<String> toRooms = new ArrayList<>();
          for (String t : toParts) {
            String name = t.trim();
            if (!name.isEmpty()) {
              toRooms.add(name);
            }
          }
          if (!toRooms.isEmpty()) {
            board.setSightLines(from, toRooms);
          }
        }
      } else {
        // No SIGHT block — rewind so callers can read anything after items if needed
        reader.reset();
      }

      return new WorldData(board, rooms, targetHealth, petName);

    } finally {
      reader.close();
    }
  }

  /**
   * Container for parsed world data including the board and room sequence.
   * This allows Doctor Lucky to be initialized with the correct movement order.
   */
  public static class WorldData {
    public final Board board;
    public final List<Room> roomsInOrder;
    public final int targetHealth;
    public final String petName;

    /**
     * Creates a new WorldData container.
     *
     * @param gameBoard the game board with all rooms and connections
     * @param orderedRooms the ordered list of rooms as they appear in the file
     * @param health Doctor Lucky's initial health
     * @param pet the pet's name (can be null if no pet)
     */
    public WorldData(Board gameBoard, List<Room> orderedRooms, int health, String pet) {
      this.board = gameBoard;
      this.roomsInOrder = orderedRooms;
      this.targetHealth = health;
      this.petName = pet;
    }
  }

  /**
   * Determines if two rooms are neighbors (share a wall). Two rooms are neighbors
   * if they share at least one edge.
   */
  private static boolean areNeighbors(RoomData room1, RoomData room2) {
    // Share a vertical edge?
    boolean shareVertical = (room1.col2 + 1 == room2.col1
        || room1.col1 == room2.col2 + 1)
        && !(room1.row2 < room2.row1 || room2.row2 < room1.row1);

    // Share a horizontal edge?
    boolean shareHorizontal = (room1.row2 + 1 == room2.row1
        || room1.row1 == room2.row2 + 1)
        && !(room1.col2 < room2.col1 || room2.col2 < room1.col1);

    return shareVertical || shareHorizontal;
  }

  /** Helper for room coordinate data. */
  private static class RoomData {
    final int row1;
    final int col1;
    final int row2;
    final int col2;
    final String name;

    RoomData(int firstRow, int firstColumn, int secondRow, int secondColumn,
        String roomName) {
      this.row1 = firstRow;
      this.col1 = firstColumn;
      this.row2 = secondRow;
      this.col2 = secondColumn;
      this.name = roomName;
    }
  }

  /** Helper for item data. */
  private static class ItemData {
    final int roomIndex;
    final String name;
    final int damage;

    ItemData(int roomsIndex, String itemName, int itemDamage) {
      this.roomIndex = roomsIndex;
      this.name = itemName;
      this.damage = itemDamage;
    }
  }
}