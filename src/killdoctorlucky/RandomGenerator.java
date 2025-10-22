package killdoctorlucky;

import java.util.Random;

/**
 * Random number generator that supports both true randomness and
 * a predictable, cycling sequence for tests.
 */
public class RandomGenerator {
  private final Random rnd;    // used when no sequence is provided
  private final int[] sequence;
  private int idx;

  /** Creates a generator that delegates to java.util.Random. */
  public RandomGenerator() {
    this.rnd = new Random();
    this.sequence = null;
    this.idx = 0;
  }

  /**
   * Creates a generator that returns the provided values in order,
   * cycling when exhausted. Useful for predictable tests.
   *
   * @param values the fixed sequence to return
   * @throws IllegalArgumentException if values is null or empty
   */
  public RandomGenerator(int... values) {
    if (values == null || values.length == 0) {
      throw new IllegalArgumentException("values must not be null or empty");
    }
    this.sequence = values.clone();
    this.idx = 0;
    this.rnd = null;
  }

  /**
   * Returns a value in [0, bound).
   * For sequence mode, the sequence value is modulo bound.
   *
   * @param bound upper-exclusive bound, must be positive
   * @return an int in [0, bound)
   * @throws IllegalArgumentException if bound <= 0
   */
  public int nextInt(int bound) {
    if (bound <= 0) {
      throw new IllegalArgumentException("bound must be positive");
    }
    if (sequence != null) {
      int v = sequence[idx % sequence.length];
      idx++;
      // normalize to [0, bound)
      int mod = v % bound;
      return mod < 0 ? mod + bound : mod;
    }
    // true randomness
    return rnd.nextInt(bound);
  }
}
