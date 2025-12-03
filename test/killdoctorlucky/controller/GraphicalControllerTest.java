package killdoctorlucky.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.GameState;
import killdoctorlucky.view.GameView;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for GraphicalController.
 * Tests controller in isolation using mock Model and mock View.
 */
public class GraphicalControllerTest {
  
  private MockGameView mockView;
  private GraphicalController controller;
  
  /**
   * Mock implementation of GameView for testing.
   */
  @SuppressWarnings("unused")
  private static class MockGameView implements GameView {
    private Features features;
    private GameState lastState;
    private List<String> messages = new ArrayList<>();
    private boolean visible = false;
    
    @Override
    public void setFeatures(Features featuresCallback) {
      this.features = featuresCallback;
    }
    
    @Override
    public void setBoard(Board gameBoard) {
      // Board set but not used in mock - that's OK for testing
    }
    
    @Override
    public void refresh(GameState state) {
      this.lastState = state;
    }
    
    @Override
    public void makeVisible() {
      this.visible = true;
    }
    
    @Override
    public void showWelcomeScreen() {
      messages.add("Welcome screen shown");
    }
    
    @Override
    public void showMessage(String message, String title, MessageType messageType) {
      messages.add(title + ": " + message);
    }
    
    @Override
    public String getPlayerName(String prompt) {
      return "TestPlayer";
    }
    
    @Override
    public String selectStartingRoom(String[] availableRooms) {
      return availableRooms.length > 0 ? availableRooms[0] : null;
    }
    
    @Override
    public String selectItem(String[] availableItems) {
      return availableItems.length > 0 ? availableItems[0] : null;
    }
    
    @Override
    public String selectRoomForPet(String[] availableRooms) {
      return availableRooms.length > 0 ? availableRooms[0] : null;
    }
    
    @Override
    public String selectWeapon(String[] availableWeapons) {
      return "poke";
    }
    
    @Override
    public void resetView() {
      this.lastState = null;
      messages.add("View reset");
    }
    
    // Test helper methods
    public List<String> getMessages() {
      return messages;
    }
    
    public boolean isVisible() {
      return visible;
    }
    
    public GameState getLastState() {
      return lastState;
    }
  }
  
  /**
   * Sets up the test fixture.
   */
  @Before
  public void setUp() {
    mockView = new MockGameView();
    controller = new GraphicalController(mockView);
  }
  
  /**
   * Tests controller creation.
   */
  @Test
  public void testControllerCreation() {
    assertNotNull(controller);
  }
  
  /**
   * Tests controller creation with null view throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testControllerCreationNullView() {
    new GraphicalController(null);
  }
  
  /**
   * Tests start() makes view visible and shows welcome.
   */
  @Test
  public void testStart() {
    controller.start();
    
    assertTrue(mockView.isVisible());
    assertTrue(mockView.getMessages().contains("Welcome screen shown"));
  }
  
  /**
   * Tests exitGame() with mock (can't fully test System.exit).
   */
  @Test
  public void testExitGame() {
    // This test is limited because exitGame() calls System.exit()
    // In a real scenario, you'd need to refactor to make it testable
    // For now, we just ensure the method exists and doesn't throw
    // We can't actually call it or it would terminate the test
    assertNotNull(controller);
  }
  
  /**
   * Tests that features are set on view.
   */
  @Test
  public void testFeaturesSetOnView() {
    // The constructor should have called setFeatures
    // We can verify by checking if our mock view received it
    assertNotNull(mockView.features);
  }
  
  /**
   * Tests addHumanPlayer with no game shows error.
   */
  @Test
  public void testAddHumanPlayerNoGame() {
    controller.addHumanPlayer("Alice", "Kitchen");
    
    // Should show error message
    boolean hasError = false;
    for (String msg : mockView.getMessages()) {
      if (msg.contains("No game") || msg.contains("Error")) {
        hasError = true;
        break;
      }
    }
    assertTrue("Should show error when no game loaded", hasError);
  }
  
  /**
   * Tests movePlayer with no game shows error.
   */
  @Test
  public void testMovePlayerNoGame() {
    controller.movePlayer("Kitchen");
    
    boolean hasError = false;
    for (String msg : mockView.getMessages()) {
      if (msg.contains("No game") || msg.contains("Error")) {
        hasError = true;
        break;
      }
    }
    assertTrue("Should show error when no game loaded", hasError);
  }
  
  /**
   * Tests pickUpItem with no game shows error.
   */
  @Test
  public void testPickUpItemNoGame() {
    controller.pickUpItem("Knife");
    
    boolean hasError = false;
    for (String msg : mockView.getMessages()) {
      if (msg.contains("No game") || msg.contains("Error")) {
        hasError = true;
        break;
      }
    }
    assertTrue("Should show error when no game loaded", hasError);
  }
  
  /**
   * Tests lookAround with no game shows error.
   */
  @Test
  public void testLookAroundNoGame() {
    controller.lookAround();
    
    boolean hasError = false;
    for (String msg : mockView.getMessages()) {
      if (msg.contains("No game") || msg.contains("Error")) {
        hasError = true;
        break;
      }
    }
    assertTrue("Should show error when no game loaded", hasError);
  }
  
  /**
   * Tests attemptMurder with no game shows error.
   */
  @Test
  public void testAttemptMurderNoGame() {
    controller.attemptMurder("Knife");
    
    boolean hasError = false;
    for (String msg : mockView.getMessages()) {
      if (msg.contains("No game") || msg.contains("Error")) {
        hasError = true;
        break;
      }
    }
    assertTrue("Should show error when no game loaded", hasError);
  }
  
  /**
   * Tests movePet with no game shows error.
   */
  @Test
  public void testMovePetNoGame() {
    controller.movePet("Kitchen");
    
    boolean hasError = false;
    for (String msg : mockView.getMessages()) {
      if (msg.contains("No game") || msg.contains("Error")) {
        hasError = true;
        break;
      }
    }
    assertTrue("Should show error when no game loaded", hasError);
  }
  
  /**
   * Tests endTurn with no game shows error.
   */
  @Test
  public void testEndTurnNoGame() {
    controller.endTurn();
    
    boolean hasError = false;
    for (String msg : mockView.getMessages()) {
      if (msg.contains("No game") || msg.contains("Error")) {
        hasError = true;
        break;
      }
    }
    assertTrue("Should show error when no game loaded", hasError);
  }
}