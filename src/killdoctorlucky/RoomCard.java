package killdoctorlucky;

/**
 * Represents a room card that allows instant teleportation to a specific room.
 * Room cards bypass normal movement restrictions and allow players to instantly
 * travel to any named room in the mansion, providing strategic positioning
 * advantages.
 */
public class RoomCard extends Card {
  private final String targetRoom;

  /**
   * Creates a new room card that teleports to the specified target room.
   * 
   * @param name       the display name of the card (e.g., "Kitchen Card",
   *                   "Library Access")
   * @param targetTeleportRoom the name of the room this card teleports to
   * @throws IllegalArgumentException if name or targetRoom is null or empty
   */
  public RoomCard(String name, String targetTeleportRoom) {
    super(name, "A room card that allows instant teleportation to the " + targetTeleportRoom + ".");

    if (targetTeleportRoom == null || targetTeleportRoom.trim().isEmpty()) {
      throw new IllegalArgumentException("Target room cannot be null or empty");
    }

    this.targetRoom = targetTeleportRoom.trim();
  }

  /**
   * Gets the name of the room this card teleports to.
   * 
   * @return the target room name, never null or empty
   */
  public String getTargetRoom() {
    return targetRoom;
  }

  /**
   * Executes this room card to teleport the player to the target room. The player
   * is instantly moved to the specified room regardless of distance or normal
   * movement restrictions. Playing this card removes it from the player's hand.
   * 
   * @param game   the current game instance where teleportation occurs
   * @param player the player using this card to teleport
   * @throws IllegalArgumentException if game or player is null
   */
  @Override
  public void execute(Game game, Player player) {
    if (game == null) {
      throw new IllegalArgumentException("Game cannot be null");
    }
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }

    // The actual teleportation logic will be handled by the Game class
    // This method is called when the room card is played for instant movement
    // The Game class will get the target room via getTargetRoom() and move the
    // player
  }

  @Override
  public String toString() {
    return String.format("RoomCard{name='%s', targetRoom='%s'}", getName(), targetRoom);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    RoomCard that = (RoomCard) obj;
    return targetRoom.equals(that.targetRoom);
  }

  @Override
  public int hashCode() {
    return super.hashCode() * 31 + targetRoom.hashCode();
  }
}