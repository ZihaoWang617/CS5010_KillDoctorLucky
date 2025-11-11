package killdoctorlucky.model.cards;

import killdoctorlucky.model.Game;
import killdoctorlucky.model.occupants.Player;

/**
 * Abstract base class for all cards in the Kill Doctor Lucky game. Cards are
 * playable objects that can be held in a player's hand and executed to produce
 * various effects during gameplay.
 */
public abstract class Card implements Playable {
  private final String name;
  private final String description;

  /**
   * Creates a new card with the specified name and description.
   * 
   * @param cardName the display name of the card
   * @param cardDescription a detailed explanation of what the card does
   * @throws IllegalArgumentException if name or description is null or empty
   */
  public Card(String cardName, String cardDescription) {
    if (cardName == null || cardName.trim().isEmpty()) {
      throw new IllegalArgumentException("Card name cannot be null or empty");
    }
    if (cardDescription == null || cardDescription.trim().isEmpty()) {
      throw new IllegalArgumentException("Card description cannot be null or empty");
    }

    this.name = cardName.trim();
    this.description = cardDescription.trim();
  }

  /**
   * Gets the display name of this card.
   * 
   * @return the card's name, never null or empty
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Gets a detailed description of this card's effect.
   * 
   * @return the card's description, never null or empty
   */
  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Executes this card's specific effect on the game state. Each card type
   * implements this method differently based on its purpose.
   * 
   * @param game   the current game instance
   * @param player the player playing this card
   * @throws IllegalArgumentException if game or player is null
   */
  @Override
  public abstract void execute(Game game, Player player);

  @Override
  public String toString() {
    return String.format("%s{name='%s'}", this.getClass().getSimpleName(), name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Card card = (Card) obj;
    return name.equals(card.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}