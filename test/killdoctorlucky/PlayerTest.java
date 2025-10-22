package killdoctorlucky;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for Player functionality. Tests player creation, movement, card
 * management, and interface implementations.
 */
public class PlayerTest {
  private Player alice;
  private Player bob;
  private Room kitchen;
  private Room diningRoom;
  private Room library;
  private WeaponCard knife;
  private MoveCard quickStep;
  private Board board;

  /**
   * Sets up the test fixture before each test.
   * Initializes Room objects (kitchen, diningRoom, library) with connections between them,
   * creates Player instances (alice, bob), WeaponCard and MoveCard objects,
   * and sets up a Board with added rooms and connections.
   */
  @Before
  public void setUp() {
    kitchen = new Room("Kitchen", true);
    diningRoom = new Room("Dining Room", true);
    library = new Room("Library", true);

    // Connect rooms
    kitchen.addConnection(diningRoom);
    diningRoom.addConnection(kitchen);
    diningRoom.addConnection(library);
    library.addConnection(diningRoom);

    alice = new Player("Alice", kitchen);
    bob = new Player("Bob", diningRoom);

    knife = new WeaponCard("Knife", 5);
    quickStep = new MoveCard("Quick Step", 1);

    board = new Board();
    board.addRoom(kitchen);
    board.addRoom(diningRoom);
    board.addRoom(library);
    board.connectRooms(kitchen, diningRoom);
    board.connectRooms(diningRoom, library);
  }

  /**
   * Tests valid player creation.
   */
  @Test
  public void testValidPlayerCreation() {
    Player charlie = new Player("Charlie", library);
    assertEquals("Charlie", charlie.getName());
    assertEquals(library, charlie.getCurrentRoom());
    assertTrue(library.isOccupiedBy(charlie));
  }

  /**
   * Tests invalid player creation with null name.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPlayerCreationNullName() {
    new Player(null, kitchen);
  }

  /**
   * Tests invalid player creation with empty name.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPlayerCreationEmptyName() {
    new Player("", kitchen);
  }

  /**
   * Tests invalid player creation with null room.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPlayerCreationNullRoom() {
    new Player("Bob", null);
  }

  /**
   * Tests valid movement between connected rooms.
   */
  @Test
  public void testValidMovement() {
    assertEquals(kitchen, alice.getCurrentRoom());
    assertTrue(alice.moveToRoom(diningRoom));
    assertEquals(diningRoom, alice.getCurrentRoom());
    assertTrue(diningRoom.isOccupiedBy(alice));
    assertFalse(kitchen.isOccupiedBy(alice));
  }

  /**
   * Tests invalid movement to unconnected room.
   */
  @Test
  public void testInvalidMovement() {
    // Try to move to unconnected room
    assertFalse(alice.moveToRoom(library));
    assertEquals(kitchen, alice.getCurrentRoom());
  }

  /**
   * Tests move to null room throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNullRoom() {
    alice.moveToRoom(null);
  }

  /**
   * Tests adding card to player's hand.
   */
  @Test
  public void testAddCard() {
    List<Playable> hand = alice.getHand();
    int initialSize = hand.size();

    alice.addCard(knife);
    assertEquals(initialSize + 1, alice.getHand().size());
    assertTrue(alice.getHand().contains(knife));
  }

  /**
   * Tests adding null card throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testAddNullCard() {
    alice.addCard(null);
  }

  /**
   * Tests removing card from player's hand.
   */
  @Test
  public void testRemoveCard() {
    alice.addCard(knife);
    assertTrue(alice.getHand().contains(knife));

    assertTrue(alice.removeCard(knife));
    assertFalse(alice.getHand().contains(knife));
  }

  /**
   * Tests removing non-existent card returns false.
   */
  @Test
  public void testRemoveNonExistentCard() {
    assertFalse(alice.removeCard(knife));
  }

  /**
   * Tests removing null card throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testRemoveNullCard() {
    alice.removeCard(null);
  }

  /**
   * Tests playing a card from hand.
   */
  @Test
  public void testPlayCard() {
    alice.addCard(knife);
    assertTrue(alice.getHand().contains(knife));

    alice.playCard(knife);
    assertFalse(alice.getHand().contains(knife));
  }

  /**
   * Tests playing card not in hand throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPlayCardNotInHand() {
    alice.playCard(knife);
  }

  /**
   * Tests playing null card throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPlayNullCard() {
    alice.playCard(null);
  }

  /**
   * Tests checking if player has weapon cards.
   */
  @Test
  public void testHasWeapons() {
    assertFalse(alice.hasWeapons());

    alice.addCard(quickStep);
    assertFalse(alice.hasWeapons());

    alice.addCard(knife);
    assertTrue(alice.hasWeapons());
  }

  /**
   * Tests player can always draw cards.
   */
  @Test
  public void testCanDrawCard() {
    // Based on game rules, players can always draw cards
    assertTrue(alice.canDrawCard());
  }

  /**
   * Tests visibility in same room.
   */
  @Test
  public void testCanBeSeenBySameRoom() {
    // Move bob to same room as alice
    bob.moveToRoom(kitchen);
    assertTrue(alice.canBeSeenBy(bob, board));
  }

  /**
   * Tests visibility with null occupant throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCanBeSeenByNullOccupant() {
    alice.canBeSeenBy(null, board);
  }

  /**
   * Tests visibility with null board throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testCanBeSeenByNullBoard() {
    alice.canBeSeenBy(bob, null);
  }

  /**
   * Tests getHand returns defensive copy.
   */
  @Test
  public void testGetHandDefensiveCopy() {
    alice.addCard(knife);
    List<Playable> hand1 = alice.getHand();
    List<Playable> hand2 = alice.getHand();

    // Should be different list instances but same contents
    assertNotSame(hand1, hand2);
    assertEquals(hand1, hand2);

    // Modifying returned list shouldn't affect player's actual hand
    hand1.clear();
    assertTrue(alice.getHand().contains(knife));
  }

  /**
   * Tests player toString method.
   */
  @Test
  public void testToString() {
    String result = alice.toString();
    assertTrue(result.contains("Alice"));
    assertTrue(result.contains("Kitchen"));
  }

  /**
   * Tests player equality based on name.
   */
  @Test
  public void testEquals() {
    Player anotherAlice = new Player("Alice", diningRoom);
    assertEquals(alice, anotherAlice);
    assertNotEquals(alice, bob);
  }
  
  /**
   * Tests picking up item.
   */
  @Test
  public void testPickUpItem() {
    Item item = new Item("TestItem", 3);
    assertTrue(alice.canCarryMore());
    assertTrue(alice.pickUpItem(item));
    assertEquals(1, alice.getInventory().size());
    assertTrue(alice.getInventory().contains(item));
  }
  
  /**
   * Tests inventory capacity limit.
   */
  @Test
  public void testInventoryCapacity() {
    Item item1 = new Item("Item1", 1);
    Item item2 = new Item("Item2", 2);
    Item item3 = new Item("Item3", 3);
    final Item item4 = new Item("Item4", 4);
    
    assertTrue(alice.pickUpItem(item1));
    assertTrue(alice.pickUpItem(item2));
    assertTrue(alice.pickUpItem(item3));
    assertFalse(alice.canCarryMore());
    assertFalse(alice.pickUpItem(item4));
  }
  
  /**
   * Tests dropping item.
   */
  @Test
  public void testDropItem() {
    Item item = new Item("TestItem", 3);
    alice.pickUpItem(item);
    
    Item dropped = alice.dropItem("TestItem");
    assertEquals(item, dropped);
    assertEquals(0, alice.getInventory().size());
  }
  
  /**
   * Tests dropping non-existent item.
   */
  @Test
  public void testDropNonExistentItem() {
    Item dropped = alice.dropItem("NonExistent");
    assertEquals(null, dropped);
  }
}