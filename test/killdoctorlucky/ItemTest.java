package killdoctorlucky;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test class for Item functionality.
 * Tests item creation, room assignment, and properties.
 */
public class ItemTest {
  private Item knife;
  private Item rope;
  private Room kitchen;
  
  /**
   * Sets up test fixtures before each test.
   * Creates sample items and a room for testing.
   */
  @Before
  public void setUp() {
    knife = new Item("Knife", 3);
    rope = new Item("Rope", 2);
    kitchen = new Room("Kitchen", true);
  }
  
  /**
   * Tests valid item creation with name and damage.
   */
  @Test
  public void testValidItemCreation() {
    Item gun = new Item("Gun", 5);
    Assert.assertEquals("Gun", gun.getName());
    Assert.assertEquals(5, gun.getDamage());
    Assert.assertNull(gun.getCurrentRoom());
  }
  
  /**
   * Tests item creation with null name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullName() {
    new Item(null, 3);
  }
  
  /**
   * Tests item creation with empty name throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyName() {
    new Item("", 3);
  }
  
  /**
   * Tests item creation with negative damage throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeDamage() {
    new Item("Weapon", -1);
  }
  
  /**
   * Tests item can have zero damage.
   */
  @Test
  public void testZeroDamage() {
    Item harmless = new Item("Feather", 0);
    Assert.assertEquals(0, harmless.getDamage());
  }
  
  /**
   * Tests setting and getting item's room.
   */
  @Test
  public void testSetRoom() {
    Assert.assertNull(knife.getCurrentRoom());
    knife.setRoom(kitchen);
    Assert.assertEquals(kitchen, knife.getCurrentRoom());
  }
  
  /**
   * Tests setting room to null.
   */
  @Test
  public void testSetRoomNull() {
    knife.setRoom(kitchen);
    knife.setRoom(null);
    Assert.assertNull(knife.getCurrentRoom());
  }
  
  /**
   * Tests item toString method.
   */
  @Test
  public void testToString() {
    knife.setRoom(kitchen);
    String result = knife.toString();
    Assert.assertTrue(result.contains("Knife"));
    Assert.assertTrue(result.contains("3"));
    Assert.assertTrue(result.contains("Kitchen"));
  }
  
  /**
   * Tests item equality based on name.
   */
  @Test
  public void testEquals() {
    Item anotherKnife = new Item("Knife", 5);
    Assert.assertEquals(knife, anotherKnife);
    Assert.assertNotEquals(knife, rope);
  }
  
  /**
   * Tests item hash code consistency.
   */
  @Test
  public void testHashCode() {
    Item anotherKnife = new Item("Knife", 5);
    Assert.assertEquals(knife.hashCode(), anotherKnife.hashCode());
  }
}