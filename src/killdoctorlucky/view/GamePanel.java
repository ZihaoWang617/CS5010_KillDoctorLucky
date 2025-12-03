package killdoctorlucky.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import killdoctorlucky.controller.Features;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.GameState;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;

/**
 * Panel that displays the graphical representation of the game world.
 * Shows rooms in a grid layout based on their actual coordinates,
 * with players, Doctor Lucky, and the pet overlaid on top.
 */
public class GamePanel extends JPanel {
  
  private static final long serialVersionUID = 1L;
  
  // Scale factor for drawing (pixels per world unit)
  private static final int SCALE = 30;
  
  // Colors
  private static final Color ROOM_COLOR = new Color(230, 245, 255);
  private static final Color ROOM_BORDER_COLOR = Color.BLACK;
  private static final Color DOCTOR_COLOR = Color.RED;
  private static final Color PLAYER_COLOR = Color.BLUE;
  private static final Color PET_COLOR = new Color(255, 165, 0); // Orange
  
  private Features features;
  private GameState currentState;
  private Board board;
  private Map<String, Rectangle> roomClickAreas;
  
  /**
   * Constructs a new GamePanel.
   */
  public GamePanel() {
    setBackground(Color.WHITE);
    setPreferredSize(new Dimension(800, 600));
    roomClickAreas = new HashMap<>();
    
    // Add mouse listener for room clicks
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        handleMouseClick(e.getX(), e.getY());
      }
    });
  }
  
  /**
   * Sets the features (controller callbacks) for this panel.
   * 
   * @param featuresCallback the controller
   */
  public void setFeatures(Features featuresCallback) {
    this.features = featuresCallback;
  }
  
  /**
   * Updates the game state and repaints the panel.
   * 
   * @param state the current game state
   */
  public void updateGameState(GameState state) {
    this.currentState = state;
    repaint();
  }
  
  /**
   * Sets the board for rendering rooms with actual coordinates.
   * 
   * @param gameBoard the game board
   */
  public void setBoard(Board gameBoard) {
    this.board = gameBoard;
    updatePreferredSize();
    repaint();
  }
  
  /**
   * Updates preferred size based on world dimensions.
   */
  private void updatePreferredSize() {
    if (board == null) {
      return;
    }
    
    // Calculate world bounds
    int maxX = 0;
    int maxY = 0;
    for (Room room : board.getAllRooms()) {
      maxX = Math.max(maxX, room.getX() + room.getWidth());
      maxY = Math.max(maxY, room.getY() + room.getHeight());
    }
    
    int width = maxX * SCALE + 100;
    int height = maxY * SCALE + 100;
    setPreferredSize(new Dimension(width, height));
    revalidate();
  }
  
  /**
   * Resets the panel to initial state.
   */
  public void reset() {
    this.currentState = null;
    this.board = null;
    this.roomClickAreas.clear();
    repaint();
  }
  
  /**
   * Handles mouse clicks on the panel.
   * 
   * @param x the x coordinate of the click
   * @param y the y coordinate of the click
   */
  private void handleMouseClick(int x, int y) {
    if (features == null || currentState == null) {
      return;
    }
    
    // First check if clicked on a player
    String clickedPlayer = getPlayerAtPoint(x, y);
    if (clickedPlayer != null) {
      showPlayerInfo(clickedPlayer);
      return;
    }
    
    // Otherwise check if clicked on a room
    for (Map.Entry<String, Rectangle> entry : roomClickAreas.entrySet()) {
      if (entry.getValue().contains(x, y)) {
        String roomName = entry.getKey();
        features.handleRoomClick(roomName);
        break;
      }
    }
  }
  
  /**
   * Gets the player at the given point, if any.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   * @return player name if found, null otherwise
   */
  private String getPlayerAtPoint(int x, int y) {
    if (board == null || currentState == null) {
      return null;
    }
    
    final int padding = 50;
    final int iconSize = 14;
    
    for (Room room : board.getAllRooms()) {
      int roomX = padding + room.getX() * SCALE;
      int roomY = padding + room.getY() * SCALE;
      int roomHeight = room.getHeight() * SCALE;
      
      int occupantX = roomX + 5;
      int occupantY = roomY + roomHeight - 20;
      
      // Check each player in this room
      for (GameState.PlayerInfo player : currentState.getPlayerInfos()) {
        if (player.getCurrentRoom().equals(room.getName())) {
          Rectangle playerBounds = new Rectangle(occupantX, occupantY, iconSize, iconSize);
          if (playerBounds.contains(x, y)) {
            return player.getName();
          }
          occupantX += 18; // Move to next player position
        }
      }
    }
    
    return null;
  }
  
  /**
   * Shows detailed player information in a dialog.
   * 
   * @param playerName the player's name
   */
  private void showPlayerInfo(String playerName) {
    if (currentState == null) {
      return;
    }
    
    // Find player info
    GameState.PlayerInfo playerInfo = null;
    for (GameState.PlayerInfo p : currentState.getPlayerInfos()) {
      if (p.getName().equals(playerName)) {
        playerInfo = p;
        break;
      }
    }
    
    if (playerInfo == null) {
      return;
    }
    
    // Build info message
    StringBuilder info = new StringBuilder();
    info.append("Player: ").append(playerInfo.getName()).append("\n");
    info.append("Type: ").append(playerInfo.isComputer() ? "Computer" : "Human").append("\n");
    info.append("Location: ").append(playerInfo.getCurrentRoom()).append("\n\n");
    
    info.append("Items:\n");
    if (playerInfo.getItems().isEmpty()) {
      info.append("  (none)");
    } else {
      for (String item : playerInfo.getItems()) {
        info.append("  - ").append(item).append("\n");
      }
    }
    
    javax.swing.JOptionPane.showMessageDialog(this, info.toString(),
        "Player: " + playerName, javax.swing.JOptionPane.INFORMATION_MESSAGE);
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    if (currentState == null || board == null) {
      // Draw welcome message
      g.setColor(Color.BLACK);
      g.setFont(new Font("Arial", Font.BOLD, 20));
      g.drawString("Welcome to Kill Doctor Lucky!", 50, 100);
      g.setFont(new Font("Arial", Font.PLAIN, 14));
      g.drawString("Start a new game from the File menu", 50, 130);
      return;
    }
    
    Graphics2D g2d = (Graphics2D) g;
    roomClickAreas.clear();
    
    // Draw the actual world map with coordinates
    drawWorldMap(g2d);
    drawGameInfo(g2d);
  }
  
  /**
   * Draws the world map using actual room coordinates from the board.
   * 
   * @param g2d the graphics context
   */
  private void drawWorldMap(Graphics2D g2d) {
    final int padding = 50;
    
    // Enable anti-aliasing for smoother graphics
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    // Draw all rooms
    for (Room room : board.getAllRooms()) {
      int x = padding + room.getX() * SCALE;
      int y = padding + room.getY() * SCALE;
      int width = room.getWidth() * SCALE;
      int height = room.getHeight() * SCALE;
      
      // Draw room rectangle
      g2d.setColor(ROOM_COLOR);
      g2d.fillRect(x, y, width, height);
      
      g2d.setColor(ROOM_BORDER_COLOR);
      g2d.setStroke(new BasicStroke(2));
      g2d.drawRect(x, y, width, height);
      
      // Store clickable area
      roomClickAreas.put(room.getName(), new Rectangle(x, y, width, height));
      
      // Draw room name (centered)
      g2d.setColor(Color.BLACK);
      g2d.setFont(new Font("Arial", Font.BOLD, 12));
      FontMetrics fm = g2d.getFontMetrics();
      String roomName = room.getName();
      int textX = x + (width - fm.stringWidth(roomName)) / 2;
      int textY = y + 18;
      g2d.drawString(roomName, textX, textY);
      
      // Draw items in room
      if (!room.getItems().isEmpty()) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 9));
        g2d.setColor(Color.DARK_GRAY);
        int itemY = textY + 14;
        for (Item item : room.getItems()) {
          String itemText = "• " + item.getName() + " (" + item.getDamage() + ")";
          g2d.drawString(itemText, x + 5, itemY);
          itemY += 11;
        }
      }
      
      // Draw occupants at bottom of room
      drawOccupantsInRoom(g2d, room, x + 5, y + height - 20);
    }
  }
  
  /**
   * Draws occupants (players, Doctor Lucky, Pet) in a room.
   * 
   * @param g2d the graphics context
   * @param room the room to draw occupants for
   * @param x starting x coordinate
   * @param y starting y coordinate
   */
  private void drawOccupantsInRoom(Graphics2D g2d, Room room, int x, int y) {
    final int iconSize = 14;
    final int gap = 18;
    int currentX = x;
    
    // Draw players in this room
    for (GameState.PlayerInfo player : currentState.getPlayerInfos()) {
      if (player.getCurrentRoom().equals(room.getName())) {
        g2d.setColor(PLAYER_COLOR);
        g2d.fillOval(currentX, y, iconSize, iconSize);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String initial = player.getName().substring(0, 1).toUpperCase();
        g2d.drawString(initial, currentX + 4, y + 11);
        currentX += gap;
      }
    }
    
    // Draw Doctor Lucky if in this room
    if (currentState.getDoctorLocation().equals(room.getName())) {
      g2d.setColor(DOCTOR_COLOR);
      g2d.fillOval(currentX, y, iconSize, iconSize);
      g2d.setColor(Color.BLACK);
      g2d.drawOval(currentX, y, iconSize, iconSize);
      g2d.setFont(new Font("Arial", Font.BOLD, 10));
      g2d.drawString("D", currentX + 4, y + 11);
      currentX += gap;
    }
    
    // Draw Pet if in this room
    if (currentState.getPetLocation().equals(room.getName())) {
      g2d.setColor(PET_COLOR);
      g2d.fillOval(currentX, y, iconSize, iconSize);
      g2d.setColor(Color.BLACK);
      g2d.setFont(new Font("Arial", Font.BOLD, 9));
      g2d.drawString("P", currentX + 4, y + 11);
    }
  }
  
  /**
   * Draws game information overlay.
   * 
   * @param g2d the graphics context
   */
  private void drawGameInfo(Graphics2D g2d) {
    // Draw current turn info at top
    g2d.setColor(new Color(0, 0, 0, 180));
    g2d.fillRect(10, 10, 350, 30);
    
    g2d.setColor(Color.WHITE);
    g2d.setFont(new Font("Arial", Font.BOLD, 14));
    String turnInfo = String.format("Turn %d/%d - Current: %s",
        currentState.getTurnNumber(),
        currentState.getMaxTurns(),
        currentState.getCurrentPlayerName());
    g2d.drawString(turnInfo, 15, 30);
    
    // Draw legend at bottom-left
    int legendX = 10;
    int legendY = getHeight() - 110;
    
    g2d.setColor(new Color(255, 255, 255, 220));
    g2d.fillRect(legendX, legendY, 180, 90);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(legendX, legendY, 180, 90);
    
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString("Legend:", legendX + 10, legendY + 18);
    
    g2d.setFont(new Font("Arial", Font.PLAIN, 11));
    
    // Doctor Lucky
    g2d.setColor(DOCTOR_COLOR);
    g2d.fillOval(legendX + 10, legendY + 28, 12, 12);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Doctor Lucky", legendX + 28, legendY + 38);
    
    // Players
    g2d.setColor(PLAYER_COLOR);
    g2d.fillOval(legendX + 10, legendY + 48, 12, 12);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Players", legendX + 28, legendY + 58);
    
    // Pet
    g2d.setColor(PET_COLOR);
    g2d.fillOval(legendX + 10, legendY + 68, 12, 12);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Pet", legendX + 28, legendY + 78);
  }
}