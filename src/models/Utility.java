package models;

import java.time.LocalDate;

// Base class for all utilities
public abstract class Utility {
    private String name;
    private String provider;
    private String accountNumber;

    public Utility(String name, String provider, String accountNumber) {
        this.name = name;
        this.provider = provider;
        this.accountNumber = accountNumber;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    @Override
    public String toString() {
        return name + " (" + provider + ")";
    }
}