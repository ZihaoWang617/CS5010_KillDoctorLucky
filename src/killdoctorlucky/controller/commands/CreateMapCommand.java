package killdoctorlucky;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Command that creates a PNG map of the current world using the
 * real geometry parsed from the world file (x/y/width/height on Room).
 */
public class CreateMapCommand implements Command {

  private final String filename;
  /** Pixels per world unit. */
  private final int scale;
  /** Outer padding around the map in pixels. */
  private final int padding = 60;

  /**
   * Creates a map command.
   *
   * @param outputFilename file name to save (".png" will be appended if missing)
   * @throws IllegalArgumentException if the name is null or blank
   */
  public CreateMapCommand(String outputFilename) {
    if (outputFilename == null || outputFilename.trim().isEmpty()) {
      throw new IllegalArgumentException("Filename cannot be null or empty");
    }
    this.filename =
        outputFilename.endsWith(".png") ? outputFilename : outputFilename + ".png";
    this.scale = 30;
  }

  @Override
  public void execute(Game game, Appendable out) {
    if (game == null || out == null) {
      throw new IllegalArgumentException("Game and output cannot be null");
    }

    try {
      Board board = game.getBoard();
      Collection<Room> rooms = board.getAllRooms();

      // 1) Compute canvas size from real geometry.
      int maxX = 0;
      int maxY = 0;
      for (Room r : rooms) {
        maxX = Math.max(maxX, r.getX() + r.getWidth());
        maxY = Math.max(maxY, r.getY() + r.getHeight());
      }
      int widthPx = maxX * scale + padding * 2;
      int heightPx = maxY * scale + padding * 2;

      BufferedImage image =
          new BufferedImage(widthPx, heightPx, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = image.createGraphics();

      g2d.setRenderingHint(
          RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(
          RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

      // Background
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, widthPx, heightPx);

      // 2) Draw adjacency (connections) first — gray dashed lines under rooms.
      g2d.setColor(Color.GRAY);
      g2d.setStroke(new BasicStroke(
          1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
          10.0f, new float[] { 5.0f }, 0.0f));
      for (Room r : rooms) {
        int x = padding + r.getX() * scale;
        int y = padding + r.getY() * scale;
        int rw = r.getWidth() * scale;
        int rh = r.getHeight() * scale;
        int cx = x + rw / 2;
        int cy = y + rh / 2;

        for (Room nb : r.getConnections()) {
          int nx = padding + nb.getX() * scale + nb.getWidth() * scale / 2;
          int ny = padding + nb.getY() * scale + nb.getHeight() * scale / 2;
          g2d.drawLine(cx, cy, nx, ny);
        }
      }

      // 3) Draw rooms, titles, items, and occupants (on top of adjacency lines).
      for (Room r : rooms) {
        int x = padding + r.getX() * scale;
        int y = padding + r.getY() * scale;
        int rw = r.getWidth() * scale;
        int rh = r.getHeight() * scale;

        // Room fill
        g2d.setColor(new Color(230, 245, 255));
        g2d.fillRect(x, y, rw, rh);

        // Border
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, rw, rh);

        // Room title (centered)
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2d.getFontMetrics();
        String roomName = r.getName();
        int textX = x + (rw - fm.stringWidth(roomName)) / 2;
        int textY = y + Math.min(18, Math.max(14, rh / 8));
        g2d.drawString(roomName, textX, textY);

        // Items (under title)
        if (!r.getItems().isEmpty()) {
          g2d.setFont(new Font("Arial", Font.PLAIN, 10));
          g2d.setColor(Color.DARK_GRAY);
          int itemY = textY + 14;
          for (Item item : r.getItems()) {
            String itemText =
                "• " + item.getName() + " (" + item.getDamage() + ")";
            g2d.drawString(itemText, x + 5, itemY);
            itemY += 12;
          }
          g2d.setColor(Color.BLACK);
        }

        // Occupants row (bottom-left of the room)
        drawOccupants(g2d, r, x + 5, y + rh - 20);
      }

      // 3b) Draw sight lines (visibility) on TOP of rooms — blue dotted lines.
      g2d.setColor(new Color(30, 144, 255)); // DodgerBlue-like
      g2d.setStroke(new BasicStroke(
          1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
          10.0f, new float[] { 2.0f, 4.0f }, 0.0f));
      Map<String, List<String>> allSight = board.getAllSightLines();
      for (Room r : rooms) {
        int x = padding + r.getX() * scale;
        int y = padding + r.getY() * scale;
        int rw = r.getWidth() * scale;
        int rh = r.getHeight() * scale;
        int cx = x + rw / 2;
        int cy = y + rh / 2;

        List<String> vis = allSight.get(r.getName());
        if (vis == null) {
          continue;
        }
        for (String targetName : vis) {
          Room t = board.getRoom(targetName);
          if (t == null) {
            continue;
          }
          if (r.getName().compareTo(t.getName()) >= 0) {
            continue;
          }
          int nx = padding + t.getX() * scale + t.getWidth() * scale / 2;
          int ny = padding + t.getY() * scale + t.getHeight() * scale / 2;
          g2d.drawLine(cx, cy, nx, ny);
        }
      }

      // 4) Legend
      drawLegend(g2d, widthPx, heightPx);

      // 5) Save file
      ImageIO.write(image, "png", new File(filename));
      out.append("Map saved to: ").append(filename).append("\n")
          .append("Map dimensions: ").append(String.valueOf(widthPx))
          .append("x").append(String.valueOf(heightPx)).append(" pixels\n");

    } catch (IOException e) {
      try {
        out.append("Error creating map: ").append(e.getMessage()).append("\n");
      } catch (IOException io) {
        throw new IllegalStateException("Error writing error message", io);
      }
    }
  }

  /** Draws occupants: red 'D' for Doctor Lucky; blue initial for players. */
  private void drawOccupants(Graphics2D g2d, Room room, int x, int y) {
    final int icon = 14;
    final int gap = 18;
    int currentX = x;
    for (Occupant ocp : room.getOccupants()) {
      if (ocp instanceof DoctorLucky) {
        g2d.setColor(Color.RED);
        g2d.fillOval(currentX, y, icon, icon);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(currentX, y, icon, icon);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        g2d.drawString("D", currentX + 4, y + 11);
      } else if (ocp instanceof Player) {
        g2d.setColor(Color.BLUE);
        g2d.fillOval(currentX, y, icon, icon);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        String initial =
            ((Player) ocp).getName().substring(0, 1).toUpperCase();
        g2d.drawString(initial, currentX + 4, y + 11);
      }
      currentX += gap;
    }
  }

  /** Draws the legend box in the bottom-right corner. */
  private void drawLegend(Graphics2D g2d, int width, int height) {
    int lx = width - 180;
    int ly = height - 110;
    int w = 160;
    int h = 90;

    g2d.setColor(new Color(245, 245, 245));
    g2d.fillRect(lx, ly, w, h);
    g2d.setColor(Color.BLACK);
    g2d.drawRect(lx, ly, w, h);

    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    g2d.drawString("Legend", lx + 10, ly + 18);

    g2d.setFont(new Font("Arial", Font.PLAIN, 11));
    int y = ly + 40;

    g2d.setColor(Color.RED);
    g2d.fillOval(lx + 10, y - 10, 12, 12);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Doctor Lucky", lx + 30, y);
    y += 20;

    g2d.setColor(Color.BLUE);
    g2d.fillOval(lx + 10, y - 10, 12, 12);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Players", lx + 30, y);
    y += 20;

    // Sight line sample
    g2d.setColor(new Color(30, 144, 255));
    int sx = lx + 10;
    int sy = y;
    g2d.drawLine(sx, sy - 6, sx + 20, sy - 6);
    g2d.setColor(Color.BLACK);
    g2d.drawString("Sight line (visibility)", lx + 40, y);
    y += 20;

    g2d.drawString("• Items (damage)", lx + 10, y);
  }

  @Override
  public String getDescription() {
    return "Creates a PNG map of the world using real room geometry.";
  }
}
