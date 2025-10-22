package killdoctorlucky;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test class for RandomGenerator functionality.
 * Tests both random and predictable sequence generation.
 */
public class RandomGeneratorTest {
  
  /**
   * Tests default constructor creates random generator.
   */
  @Test
  public void testDefaultConstructor() {
    RandomGenerator rng = new RandomGenerator();
    int value = rng.nextInt(10);
    Assert.assertTrue(value >= 0 && value < 10);
  }
  
  /**
   * Tests predictable sequence generation.
   */
  @Test
  public void testPredictableSequence() {
    RandomGenerator rng = new RandomGenerator(5, 3, 7);
    Assert.assertEquals(5, rng.nextInt(10));
    Assert.assertEquals(3, rng.nextInt(10));
    Assert.assertEquals(7, rng.nextInt(10));
    Assert.assertEquals(5, rng.nextInt(10)); // Cycles back
  }
  
  /**
   * Tests modulo behavior with larger values.
   */
  @Test
  public void testModuloBehavior() {
    RandomGenerator rng = new RandomGenerator(15, 23, 7);
    Assert.assertEquals(5, rng.nextInt(10));
    Assert.assertEquals(3, rng.nextInt(10));
    Assert.assertEquals(7, rng.nextInt(10));
  }
  
  /**
   * Tests invalid bound throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidBound() {
    RandomGenerator rng = new RandomGenerator();
    rng.nextInt(0);
  }
  
  /**
   * Tests negative bound throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeBound() {
    RandomGenerator rng = new RandomGenerator();
    rng.nextInt(-5);
  }
  
  /**
   * Tests null sequence throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullSequence() {
    new RandomGenerator((int[]) null);
  }
  
  /**
   * Tests empty sequence throws exception.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testEmptySequence() {
    new RandomGenerator(new int[0]);
  }
}