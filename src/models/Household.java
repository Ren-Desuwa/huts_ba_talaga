package models;

import java.util.UUID;

public class Household {
    private UUID id;
    private String name;
    private String address;
    private int numberOfOccupants;
    
    public Household(String name, String address, int numberOfOccupants) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.numberOfOccupants = numberOfOccupants;
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public int getNumberOfOccupants() { return numberOfOccupants; }
    public void setNumberOfOccupants(int numberOfOccupants) { this.numberOfOccupants = numberOfOccupants; }
}
