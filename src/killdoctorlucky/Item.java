package killdoctorlucky;

/**
 * Represents an item in the game world that can be used as a weapon.
 * Items have a damage value and are located in specific rooms.
 * Items are physical objects placed in rooms that can be picked up and used by players.
 */
public class Item {
    private final String name;
    private final int damage;
    private Room currentRoom;
    
    /**
     * Creates a new item with the specified name and damage value.
     * 
     * @param name the name of the item (e.g., "Revolver", "Knife")
     * @param damage the damage this item can inflict when used as a weapon
     * @throws IllegalArgumentException if name is null or empty, or damage is negative
     */
    public Item(String name, int damage) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Damage cannot be negative");
        }
        
        this.name = name.trim();
        this.damage = damage;
    }
    
    /**
     * Gets the name of this item.
     * 
     * @return the item's name, never null or empty
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the damage value of this item.
     * This represents how much harm this item can cause when used as a weapon.
     * 
     * @return the item's damage value, always non-negative
     */
    public int getDamage() {
        return damage;
    }
    
    /**
     * Gets the room where this item is currently located.
     * 
     * @return the current room containing this item, or null if not placed in any room
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    /**
     * Sets the room where this item is located.
     * This is typically called when placing items during world initialization.
     * 
     * @param room the room to place this item in
     */
    public void setRoom(Room room) {
        this.currentRoom = room;
    }
    
    @Override
    public String toString() {
        String location = (currentRoom != null) ? currentRoom.getName() : "nowhere";
        return String.format("Item{name='%s', damage=%d, location='%s'}", 
                           name, damage, location);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return name.equals(item.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}