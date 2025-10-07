package killdoctorlucky;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the Kill Doctor Lucky game.
 * Players can move between rooms, hold cards in their hand, and attempt to murder Doctor Lucky.
 * Each player has a name, current location, and a hand of playable cards.
 */
public class Player implements Movable, Occupant {
    private final String name;
    private Room currentRoom;
    private final List<Playable> hand;
    
    /**
     * Creates a new player with the specified name in the given starting room.
     * The player begins with an empty hand of cards.
     * 
     * @param name the player's name for identification
     * @param startingRoom the room where this player begins the game
     * @throws IllegalArgumentException if name is null, empty, or startingRoom is null
     */
    public Player(String name, Room startingRoom) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (startingRoom == null) {
            throw new IllegalArgumentException("Starting room cannot be null");
        }
        
        this.name = name.trim();
        this.currentRoom = startingRoom;
        this.hand = new ArrayList<>();
        
        // Add this player to the starting room's occupants
        startingRoom.addOccupant(this);
    }
    
    /**
     * Gets this player's name.
     * 
     * @return the player's name, never null or empty
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the room where this player is currently located.
     * 
     * @return the player's current room, never null
     */
    @Override
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    /**
     * Gets a copy of this player's current hand of cards.
     * Modifying the returned list will not affect the player's actual hand.
     * 
     * @return a new list containing all cards in the player's hand
     */
    public List<Playable> getHand() {
        return new ArrayList<>(hand);
    }
    
    /**
     * Attempts to move this player to the specified destination room.
     * The move succeeds only if the destination is connected to the current room.
     * 
     * @param destination the room to move to
     * @return true if the move was successful, false if the destination is not reachable
     * @throws IllegalArgumentException if destination is null
     */
    @Override
    public boolean moveToRoom(Room destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination room cannot be null");
        }
        
        // Check if destination is connected to current room
        if (!currentRoom.getConnections().contains(destination)) {
            return false;
        }
        
        // Remove from current room and add to destination
        currentRoom.removeOccupant(this);
        destination.addOccupant(this);
        this.currentRoom = destination;
        
        return true;
    }
    
    /**
     * Adds a card to this player's hand.
     * 
     * @param card the card to add to the hand
     * @throws IllegalArgumentException if card is null
     */
    public void addCard(Playable card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        hand.add(card);
    }
    
    /**
     * Removes a card from this player's hand if it exists.
     * 
     * @param card the card to remove from the hand
     * @return true if the card was removed, false if it wasn't in the hand
     * @throws IllegalArgumentException if card is null
     */
    public boolean removeCard(Playable card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        return hand.remove(card);
    }
    
    /**
     * Plays a card from this player's hand, executing its effect and removing it.
     * The card must be in the player's hand to be played.
     * 
     * @param card the card to play
     * @throws IllegalArgumentException if card is null or not in the player's hand
     */
    public void playCard(Playable card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("Card is not in player's hand");
        }
        
        // Note: The execute method will be called by the Game class
        // when it's ready to process the card's effects
        hand.remove(card);
    }
    
    /**
     * Checks if this player has any weapon cards in their hand.
     * 
     * @return true if the player has at least one weapon card, false otherwise
     */
    public boolean hasWeapons() {
        for (Playable card : hand) {
            if (card instanceof WeaponCard) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if this player can draw another card.
     * Based on the game rules, players can always draw cards as there's no hand size limit.
     * 
     * @return true (players can always draw cards)
     */
    public boolean canDrawCard() {
        // Based on game rules, there's no hand size limit
        return true;
    }
    
    /**
     * Determines if this player can be seen by another occupant.
     * Visibility is determined by room connections and line-of-sight calculations.
     * 
     * @param other the occupant trying to see this player
     * @param board the game board for visibility calculations
     * @return true if this player is visible to the other occupant, false otherwise
     * @throws IllegalArgumentException if other or board is null
     */
    @Override
    public boolean canBeSeenBy(Occupant other, Board board) {
        if (other == null) {
            throw new IllegalArgumentException("Other occupant cannot be null");
        }
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        
        // If in the same room, always visible
        if (this.getCurrentRoom().equals(other.getCurrentRoom())) {
            return true;
        }
        
        // Otherwise, use board's visibility calculation
        return board.calculateVisibility(other, this);
    }
    
    @Override
    public String toString() {
        return String.format("Player{name='%s', room='%s', cards=%d}", 
                           name, currentRoom.getName(), hand.size());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player player = (Player) obj;
        return name.equals(player.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}