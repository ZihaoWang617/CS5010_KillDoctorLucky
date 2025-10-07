package killdoctorlucky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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

      // Validate but don't store yet (will be used in future milestones)
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

      // Validate but don't store yet (will be used in future milestones)
      Integer.parseInt(targetParts[0]); // targetHealth
      // String targetName = targetParts[1]; // will be used later

      // Step 3: Parse number of rooms
      String roomCountLine = reader.readLine();
      if (roomCountLine == null) {
        throw new IllegalArgumentException("Missing room count");
      }
      int numRooms = Integer.parseInt(roomCountLine.trim());

      // Step 4: Parse each room
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

      // Step 5: Parse number of items
      String itemCountLine = reader.readLine();
      if (itemCountLine == null) {
        throw new IllegalArgumentException("Missing item count");
      }
      int numItems = Integer.parseInt(itemCountLine.trim());

      // Step 6: Parse each item (store data, don't add to rooms yet)
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

      // Step 7: Build Board and create all rooms
      Board board = new Board();
      List<Room> rooms = new ArrayList<>();

      // Create all rooms
      for (RoomData data : roomDataList) {
        Room room = new Room(data.name, true);
        rooms.add(room);
        board.addRoom(room);
      }

      // Step 8: Establish connections between neighboring rooms
      for (int i = 0; i < roomDataList.size(); i++) {
        for (int j = i + 1; j < roomDataList.size(); j++) {
          RoomData room1 = roomDataList.get(i);
          RoomData room2 = roomDataList.get(j);

          if (areNeighbors(room1, room2)) {
            board.connectRooms(rooms.get(i), rooms.get(j));
          }
        }
      }

      // Step 9: Add items to their corresponding rooms
      for (ItemData itemData : itemDataList) {
        Item item = new Item(itemData.name, itemData.damage);
        if (itemData.roomIndex >= 0 && itemData.roomIndex < rooms.size()) {
          rooms.get(itemData.roomIndex).addItem(item);
        }
      }

      return new WorldData(board, rooms);

    } finally {
      reader.close();
    }
  }

  /**
   * Container for parsed world data including the board and room sequence. This
   * allows Doctor Lucky to be initialized with the correct movement sequence.
   */
  public static class WorldData {
    public final Board board;
    public final List<Room> roomsInOrder;

    /**
     * Creates a new WorldData container.
     * 
     * @param gameBoard        the game board with all rooms and connections
     * @param orderedRooms the ordered list of rooms as they appear in the
     *                     specification
     */
    public WorldData(Board gameBoard, List<Room> orderedRooms) {
      this.board = gameBoard;
      this.roomsInOrder = orderedRooms;
    }
  }

  /**
   * Determines if two rooms are neighbors (share a wall). Two rooms are neighbors
   * if they share at least one edge.
   */
  private static boolean areNeighbors(RoomData room1, RoomData room2) {
    // Check if rooms share a vertical edge
    boolean shareVerticalEdge = (room1.col2 + 1 == room2.col1 || room1.col1 == room2.col2 + 1)
        && !(room1.row2 < room2.row1 || room2.row2 < room1.row1);

    // Check if rooms share a horizontal edge
    boolean shareHorizontalEdge = (room1.row2 + 1 == room2.row1 || room1.row1 == room2.row2 + 1)
        && !(room1.col2 < room2.col1 || room2.col2 < room1.col1);

    return shareVerticalEdge || shareHorizontalEdge;
  }

  /**
   * Helper class to store room coordinate data during parsing.
   */
  private static class RoomData {
    final int row1;
    final int col1;
    final int row2;
    final int col2;
    final String name;

    RoomData(int firstRow, int firstColumn, int secondRow, int secondColumn, String roomName) {
      this.row1 = firstRow;
      this.col1 = firstColumn;
      this.row2 = secondRow;
      this.col2 = secondColumn;
      this.name = roomName;
    }
  }

  /**
   * Helper class to store item data during parsing.
   */
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