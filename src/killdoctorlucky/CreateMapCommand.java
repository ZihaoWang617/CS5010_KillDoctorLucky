package killdoctorlucky;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.imageio.ImageIO;
/**
 * Command to create a graphical representation of the world map and save it as PNG.
 */

public class CreateMapCommand implements Command {
  
  private final String filename;
  private final int scale; // pixels per coordinate unit
  private final int padding = 50; // padding around the map
  
  /**
   * Creates a command to generate map image.
   * 
   * @param outputFilename the name of the PNG file to create
   */
  public CreateMapCommand(String outputFilename) {
    if (outputFilename == null || outputFilename.trim().isEmpty()) {
      throw new IllegalArgumentException("Filename cannot be null or empty");
    }
    this.filename = outputFilename.endsWith(".png") ? outputFilename : outputFilename + ".png";
    this.scale = 30; // Default scale factor
  }
  
  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output cannot be null");
    }
    
    try {
      Board board = game.getBoard();
      Collection<Room> rooms = board.getAllRooms();
      
      // Find map dimensions
      int maxX = 0;
      int maxY = 0;
      
      // Since we don't have coordinates stored in Room, we need to get them from WorldParser
      // For now, we'll use a simple grid layout based on room connections
      Map<Room, Point> roomPositions = calculateRoomPositions(rooms);
      
      for (Point p : roomPositions.values()) {
        maxX = Math.max(maxX, p.x);
        maxY = Math.max(maxY, p.y);
      }
      
      // Create image
      int width = (maxX * scale) + (padding * 2);
      int height = (maxY * scale) + (padding * 2);
      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = image.createGraphics();
      
      // Set rendering hints for better quality
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      
      // White background
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, width, height);
      
      // Draw rooms
      drawRooms(g2d, rooms, roomPositions, game);
      
      // Draw legend
      drawLegend(g2d, width, height);
      
      // Save to file
      File outputFile = new File(filename);
      ImageIO.write(image, "png", outputFile);
      
      out.append("Map saved to: ").append(filename).append("\n");
      out.append("Map dimensions: ").append(String.valueOf(width))
         .append("x").append(String.valueOf(height)).append(" pixels\n");
      
    } catch (IOException e) {
      try {
        out.append("Error creating map: ").append(e.getMessage()).append("\n");
      } catch (IOException ioException) {
        throw new IllegalStateException("Error writing error message", ioException);
      }
    }
  }
  
  /**
   * Calculate positions for rooms in a grid layout.
   * This is simplified - in reality you'd parse coordinates from the world file.
   */
  private Map<Room, Point> calculateRoomPositions(Collection<Room> rooms) {
    Map<Room, Point> positions = new HashMap<>();
    int gridSize = (int) Math.ceil(Math.sqrt(rooms.size()));
    int x = 0;
    int y = 0;
    
    for (Room room : rooms) {
      positions.put(room, new Point(x * 5 + 2, y * 4 + 2));
      x++;
      if (x >= gridSize) {
        x = 0;
        y++;
      }
    }
    
    return positions;
  }
  
  /**
   * Draw all rooms on the map.
   */
  private void drawRooms(Graphics2D g2d, Collection<Room> rooms, 
                         Map<Room, Point> positions, Game game) {
    // Use different colors for different room types
    Color[] roomColors = {
      new Color(200, 230, 255), // Light blue
      new Color(255, 230, 200), // Light orange  
      new Color(230, 255, 200), // Light green
      new Color(255, 200, 230)  // Light pink
    };
    
    Random rand = new Random(42); // Fixed seed for consistent colors
    
    for (Room room : rooms) {
      Point pos = positions.get(room);
      int x = padding + (pos.x * scale);
      int y = padding + (pos.y * scale);
      int roomWidth = scale * 4;
      int roomHeight = scale * 3;
      
      // Draw room rectangle
      Color roomColor = roomColors[rand.nextInt(roomColors.length)];
      g2d.setColor(roomColor);
      g2d.fillRect(x, y, roomWidth, roomHeight);
      
      // Draw room border
      g2d.setColor(Color.BLACK);
      g2d.setStroke(new BasicStroke(2));
      g2d.drawRect(x, y, roomWidth, roomHeight);
      
      // Draw room name
      g2d.setFont(new Font("Arial", Font.BOLD, 12));
      FontMetrics fm = g2d.getFontMetrics();
      String roomName = room.getName();
      int textWidth = fm.stringWidth(roomName);
      int textX = x + (roomWidth - textWidth) / 2;
      int textY = y + 20;
      g2d.drawString(roomName, textX, textY);
      
      // Draw items in room
      if (!room.getItems().isEmpty()) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(Color.DARK_GRAY);
        int itemY = textY + 15;
        for (Item item : room.getItems()) {
          String itemText = "• " + item.getName() + " (" + item.getDamage() + ")";
          g2d.drawString(itemText, x + 5, itemY);
          itemY += 12;
        }
      }
      
      // Draw occupants
      drawOccupants(g2d, room, x, y + roomHeight - 25);
      
      // Draw connections (simplified)
      g2d.setColor(Color.GRAY);
      g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, 
                                     BasicStroke.JOIN_MITER, 10.0f, 
                                     new float[]{5.0f}, 0.0f));
      for (Room neighbor : room.getConnections()) {
        Point neighborPos = positions.get(neighbor);
        if (neighborPos != null) {
          int nx = padding + (neighborPos.x * scale) + (roomWidth / 2);
          int ny = padding + (neighborPos.y * scale) + (roomHeight / 2);
          g2d.drawLine(x + roomWidth / 2, y + roomHeight / 2, nx, ny);
        }
      }
    }
  }
  
  /**
   * Draw occupants in a room.
   */
  private void drawOccupants(Graphics2D g2d, Room room, int x, int y) {
    int iconSize = 15;
    int spacing = 20;
    int currentX = x + 5;
    
    for (Occupant occupant : room.getOccupants()) {
      if (occupant instanceof DoctorLucky) {
        // Draw Doctor Lucky as red circle
        g2d.setColor(Color.RED);
        g2d.fillOval(currentX, y, iconSize, iconSize);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(currentX, y, iconSize, iconSize);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("D", currentX + 5, y + 11);
      } else if (occupant instanceof Player) {
        // Draw players as blue circles
        g2d.setColor(Color.BLUE);
        g2d.fillOval(currentX, y, iconSize, iconSize);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String initial = ((Player) occupant).getName().substring(0, 1).toUpperCase();
        g2d.drawString(initial, currentX + 5, y + 11);
      }
      currentX += spacing;
    }
  }
  
  /**
   * Draw a legend explaining the map symbols.
   */
  private void drawLegend(Graphics2D g2d, int width, int height) {
    int legendX = width - 180;
    int legendY = height - 120;
    int legendWidth = 160;
    int legendHeight = 100;
    
    // Legend background
    g2d.setColor(new Color(240, 240, 240));
    g2d.fillRect(legendX, legendY, legendWidth, legendHeight);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(legendX, legendY, legendWidth, legendHeight);
    
    // Legend title
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString("Legend", legendX + 10, legendY + 20);
    
    // Legend items
    g2d.setFont(new Font("Arial", Font.PLAIN, 11));
    int itemY = legendY + 40;
    
    // Doctor Lucky
    g2d.setColor(Color.RED);
    g2d.fillOval(legendX + 10, itemY - 10, 12, 12);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Doctor Lucky", legendX + 30, itemY);
    
    // Players
    itemY += 20;
    g2d.setColor(Color.BLUE);
    g2d.fillOval(legendX + 10, itemY - 10, 12, 12);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Players", legendX + 30, itemY);
    
    // Items
    itemY += 20;
    g2d.drawString("• Items (damage)", legendX + 10, itemY);
  }
  
  @Override
  public String getDescription() {
    return "Creates a graphical map of the game world and saves it as a PNG file";
  }
}