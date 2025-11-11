package killdoctorlucky.model.occupants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import killdoctorlucky.model.Board;
import killdoctorlucky.model.Item;
import killdoctorlucky.model.Room;
import killdoctorlucky.model.cards.MoveCard;
import killdoctorlucky.model.cards.Playable;
import killdoctorlucky.model.cards.WeaponCard;
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
  
  @Test
  public void testGetBestWeaponItem() {
    Room testRoom = new Room("Kitchen");
    Player testPlayer = new Player("Alice", testRoom);
    
    Item testKnife = new Item("Knife", 5);
    Item rope = new Item("Rope", 3);
    Item gun = new Item("Gun", 8);
    
    testPlayer.pickUpItem(testKnife);
    testPlayer.pickUpItem(rope);
    testPlayer.pickUpItem(gun);
    
    Item best = testPlayer.getBestWeaponItem();
    assertNotNull("Best weapon should not be null", best);
    assertEquals("Gun", best.getName());
    assertEquals(8, best.getDamage());
  }
  
  @Test
  public void testGetBestWeaponItemEmpty() {
    Room room = new Room("Kitchen");
    Player player = new Player("Alice", room);
    
    assertNull("Best weapon should be null when inventory is empty", 
        player.getBestWeaponItem());
  }
  
  @Test
  public void testGetBestWeaponItemSingleItem() {
    Room testRoom = new Room("Kitchen");
    Player testPlayer = new Player("Alice", testRoom);
    
    Item testKnife = new Item("Knife", 5);
    testPlayer.pickUpItem(testKnife);
    
    Item best = testPlayer.getBestWeaponItem();
    assertEquals("Should return the only weapon", testKnife, best);
  }
  
  @Test
  public void testGetBestWeaponItemMultipleSameDamage() {
    Room testRoom = new Room("Kitchen");
    Player testPlayer = new Player("Alice", testRoom);
    
    Item testKnife = new Item("Knife", 5);
    Item sword = new Item("Sword", 5);
    
    testPlayer.pickUpItem(testKnife);
    testPlayer.pickUpItem(sword);
    
    Item best = testPlayer.getBestWeaponItem();
    assertNotNull(best);
    assertEquals(5, best.getDamage());
    // Should return one of them (either is acceptable)
  }
  
  @Test
  public void testGetPokeInEyeDamage() {
    Room room = new Room("Kitchen");
    Player player = new Player("Alice", room);
    
    assertEquals("Poke in eye should always do 1 damage", 
        1, player.getPokeInEyeDamage());
  }
  
  @Test
  public void testGetPokeInEyeDamageConstant() {
    Room room = new Room("Kitchen");
    Player player1 = new Player("Alice", room);
    Player player2 = new Player("Bob", room);
    
    // All players should have same poke damage
    assertEquals(player1.getPokeInEyeDamage(), player2.getPokeInEyeDamage());
    assertEquals(1, player1.getPokeInEyeDamage());
  }
  
  @Test
  public void testCanSeeOtherPlayerSameRoom() {
    Board testBoard = new Board();
    
    Room testRoom1 = new Room("Kitchen");
    testRoom1.setGeometry(0, 0, 5, 5);
    
    testBoard.addRoom(testRoom1);
    
    Player alice1 = new Player("Alice", testRoom1);
    Player bob1 = new Player("Bob", testRoom1);
    
    assertTrue("Players in same room should see each other", 
        alice1.canSeeOtherPlayer(bob1, testBoard));
  }
  
  @Test
  public void testCanSeeOtherPlayerAdjacentRoom() {
    Board testBoard = new Board();
    
    Room testRoom1 = new Room("Kitchen");
    testRoom1.setGeometry(0, 0, 5, 5);
    Room testRoom2 = new Room("Dining");
    testRoom2.setGeometry(6, 0, 10, 5);
    
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    testBoard.connectRooms(testRoom1, testRoom2);
    
    Player alice1 = new Player("Alice", testRoom1);
    Player bob1 = new Player("Bob", testRoom2);
    
    assertTrue("Players in adjacent rooms should see each other", 
        alice1.canSeeOtherPlayer(bob1, testBoard));
  }
  
  @Test
  public void testCanSeeOtherPlayerNonAdjacentRoom() {
    Board testBoard = new Board();
    
    Room testRoom1 = new Room("Kitchen");
    testRoom1.setGeometry(0, 0, 5, 5);
    Room testRoom2 = new Room("Library");
    testRoom2.setGeometry(11, 0, 15, 5);
    
    testBoard.addRoom(testRoom1);
    testBoard.addRoom(testRoom2);
    // Not connected
    
    Player alice1 = new Player("Alice", testRoom1);
    Player bob1 = new Player("Bob", testRoom2);
    
    assertFalse("Players in non-adjacent rooms should not see each other", 
        alice1.canSeeOtherPlayer(bob1, testBoard));
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCanSeeOtherPlayerNullPlayer() {
    Board testBoard = new Board();
    Room testRoom = new Room("Kitchen");
    Player testPlayer = new Player("Alice", testRoom);
    
    testPlayer.canSeeOtherPlayer(null, testBoard);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testCanSeeOtherPlayerNullBoard() {
    Room testRoom = new Room("Kitchen");
    Player alice1 = new Player("Alice", testRoom);
    Player bob1 = new Player("Bob", testRoom);
    
    alice1.canSeeOtherPlayer(bob1, null);
  }
  
  @Test
  public void testBestWeaponAfterDroppingItem() {
    Room testRoom = new Room("Kitchen");
    Player testPlayer = new Player("Alice", testRoom);
    
    Item testKnife = new Item("Knife", 5);
    Item testGun = new Item("Gun", 8);
    
    testPlayer.pickUpItem(testKnife);
    testPlayer.pickUpItem(testGun);
    
    assertEquals("Gun", testPlayer.getBestWeaponItem().getName());
    
    // Drop the gun
    testPlayer.dropItem("Gun");
    
    // Now knife should be the best weapon
    assertEquals("Knife", testPlayer.getBestWeaponItem().getName());
  }
  
  @Test
  public void testBestWeaponWithZeroDamageItems() {
    Room testRoom = new Room("Kitchen");
    Player testPlayer = new Player("Alice", testRoom);
    
    Item feather = new Item("Feather", 0);
    Item testKnife = new Item("Knife", 5);
    
    testPlayer.pickUpItem(feather);
    testPlayer.pickUpItem(testKnife);
    
    Item best = testPlayer.getBestWeaponItem();
    assertEquals("Knife", best.getName());
    assertEquals(5, best.getDamage());
  }
}