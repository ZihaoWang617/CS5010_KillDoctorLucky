package killdoctorlucky.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import killdoctorlucky.model.GameState;

/**
 * Panel that displays game information on the right side of the window.
 * Shows current player, turn count, Doctor Lucky's health, player inventory,
 * and the last action result.
 */
public class InfoPanel extends JPanel {
  
  private static final long serialVersionUID = 1L;
  
  private JLabel titleLabel;
  private JLabel currentPlayerLabel;
  private JLabel turnLabel;
  private JLabel doctorHealthLabel;
  private JLabel doctorLocationLabel;
  private JLabel petLocationLabel;
  private JTextArea playerInventoryArea;
  private JTextArea lastActionArea;
  
  /**
   * Constructs a new InfoPanel with all components.
   */
  public InfoPanel() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(new Color(240, 240, 240));
    setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    initializeComponents();
  }
  
  /**
   * Initializes all UI components.
   */
  private void initializeComponents() {
    // Title
    titleLabel = new JLabel("Game Information");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    titleLabel.setAlignmentX(LEFT_ALIGNMENT);
    add(titleLabel);
    add(Box.createRigidArea(new Dimension(0, 15)));
    
    // Current Player section
    final JPanel currentPlayerPanel = createInfoSection("Current Player");
    currentPlayerLabel = new JLabel("None");
    currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 14));
    currentPlayerLabel.setForeground(new Color(0, 102, 204));
    currentPlayerPanel.add(currentPlayerLabel);
    add(currentPlayerPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));
    
    // Turn info section
    final JPanel turnPanel = createInfoSection("Turn");
    turnLabel = new JLabel("0 / 0");
    turnPanel.add(turnLabel);
    add(turnPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));
    
    // Doctor Lucky section
    final JPanel doctorPanel = createInfoSection("Doctor Lucky");
    doctorHealthLabel = new JLabel("Health: 50/50");
    doctorHealthLabel.setForeground(new Color(204, 0, 0));
    doctorPanel.add(doctorHealthLabel);
    doctorLocationLabel = new JLabel("Location: Unknown");
    doctorPanel.add(doctorLocationLabel);
    add(doctorPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));
    
    // Pet section
    final JPanel petPanel = createInfoSection("Pet");
    petLocationLabel = new JLabel("Location: Unknown");
    petLocationLabel.setForeground(new Color(255, 140, 0));
    petPanel.add(petLocationLabel);
    add(petPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));
    
    // Player inventory section
    final JPanel inventoryPanel = createInfoSection("Current Player Inventory");
    playerInventoryArea = new JTextArea(5, 20);
    playerInventoryArea.setEditable(false);
    playerInventoryArea.setLineWrap(true);
    playerInventoryArea.setWrapStyleWord(true);
    playerInventoryArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    final JScrollPane inventoryScroll = new JScrollPane(playerInventoryArea);
    inventoryScroll.setAlignmentX(LEFT_ALIGNMENT);
    inventoryPanel.add(inventoryScroll);
    add(inventoryPanel);
    add(Box.createRigidArea(new Dimension(0, 10)));
    
    // Last action section
    final JPanel actionPanel = createInfoSection("Last Action");
    lastActionArea = new JTextArea(4, 20);
    lastActionArea.setEditable(false);
    lastActionArea.setLineWrap(true);
    lastActionArea.setWrapStyleWord(true);
    lastActionArea.setFont(new Font("Arial", Font.ITALIC, 11));
    lastActionArea.setForeground(new Color(0, 128, 0));
    final JScrollPane actionScroll = new JScrollPane(lastActionArea);
    actionScroll.setAlignmentX(LEFT_ALIGNMENT);
    actionPanel.add(actionScroll);
    add(actionPanel);
    
    // Add glue to push everything to the top
    add(Box.createVerticalGlue());
  }
  
  /**
   * Creates a titled section panel.
   * 
   * @param title the section title
   * @return a new JPanel with the title and border
   */
  private JPanel createInfoSection(String title) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setAlignmentX(LEFT_ALIGNMENT);
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.GRAY),
        title,
        0,
        0,
        new Font("Arial", Font.BOLD, 12)
    ));
    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
    return panel;
  }
  
  /**
   * Updates the panel with new game information.
   * 
   * @param state the current game state
   */
  public void updateGameInfo(GameState state) {
    if (state == null) {
      reset();
      return;
    }
    
    // Update current player
    String currentPlayer = state.getCurrentPlayerName();
    if (currentPlayer != null && !currentPlayer.isEmpty()) {
      currentPlayerLabel.setText(currentPlayer);
    } else {
      currentPlayerLabel.setText("None");
    }
    
    // Update turn
    turnLabel.setText(String.format("%d / %d", 
        state.getTurnNumber(), 
        state.getMaxTurns()));
    
    // Update Doctor Lucky health
    doctorHealthLabel.setText(String.format("Health: %d/%d",
        state.getDoctorHealth(),
        state.getDoctorMaxHealth()));
    
    // Update Doctor Lucky location
    doctorLocationLabel.setText("Location: " + state.getDoctorLocation());
    
    // Update pet location
    petLocationLabel.setText("Location: " + state.getPetLocation());
    
    // Update current player inventory
    updatePlayerInventory(state);
    
    // Update last action
    String lastAction = state.getLastActionResult();
    if (lastAction != null && !lastAction.isEmpty()) {
      lastActionArea.setText(lastAction);
    } else {
      lastActionArea.setText("No actions yet");
    }
    
    // Show game over message if applicable
    if (state.isGameOver()) {
      String winner = state.getWinner();
      if (winner != null) {
        lastActionArea.setText("GAME OVER!\nWinner: " + winner);
        lastActionArea.setForeground(new Color(0, 128, 0));
      } else {
        lastActionArea.setText("GAME OVER!\nDoctor Lucky escaped!");
        lastActionArea.setForeground(new Color(204, 0, 0));
      }
    } else {
      lastActionArea.setForeground(new Color(0, 128, 0));
    }
  }
  
  /**
   * Updates the player inventory display.
   * 
   * @param state the current game state
   */
  private void updatePlayerInventory(GameState state) {
    final StringBuilder inventory = new StringBuilder();
    
    final String currentPlayerName = state.getCurrentPlayerName();
    if (currentPlayerName == null || currentPlayerName.isEmpty()) {
      playerInventoryArea.setText("No player selected");
      return;
    }
    
    // Find current player's info
    GameState.PlayerInfo currentPlayerInfo = null;
    for (GameState.PlayerInfo playerInfo : state.getPlayerInfos()) {
      if (playerInfo.getName().equals(currentPlayerName)) {
        currentPlayerInfo = playerInfo;
        break;
      }
    }
    
    if (currentPlayerInfo == null) {
      playerInventoryArea.setText("Player not found");
      return;
    }
    
    // Display player info
    inventory.append("Player: ").append(currentPlayerInfo.getName()).append("\n");
    inventory.append("Room: ").append(currentPlayerInfo.getCurrentRoom()).append("\n");
    inventory.append("Type: ")
        .append(currentPlayerInfo.isComputer() ? "Computer" : "Human")
        .append("\n");
    inventory.append("\nItems:\n");
    
    if (currentPlayerInfo.getItems().isEmpty()) {
      inventory.append("  (none)");
    } else {
      for (String item : currentPlayerInfo.getItems()) {
        inventory.append("  - ").append(item).append("\n");
      }
    }
    
    playerInventoryArea.setText(inventory.toString());
  }
  
  /**
   * Resets the panel to initial state.
   */
  public void reset() {
    currentPlayerLabel.setText("None");
    turnLabel.setText("0 / 0");
    doctorHealthLabel.setText("Health: 50/50");
    doctorLocationLabel.setText("Location: Unknown");
    petLocationLabel.setText("Location: Unknown");
    playerInventoryArea.setText("No game in progress");
    lastActionArea.setText("Start a new game");
    lastActionArea.setForeground(new Color(0, 128, 0));
  }
}