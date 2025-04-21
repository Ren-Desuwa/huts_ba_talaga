package models;

import java.time.LocalDate;

public class Subscription {
    private String name;
    private String provider;
    private String accountNumber;
    private SubscriptionType type;
    private double monthlyCost;
    private LocalDate nextBillingDate;
    
    public Subscription(String name, String provider, String accountNumber, 
                      SubscriptionType type, double monthlyCost) {
        this.name = name;
        this.provider = provider;
        this.accountNumber = accountNumber;
        this.type = type;
        this.monthlyCost = monthlyCost;
        this.nextBillingDate = LocalDate.now().plusMonths(1);
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public SubscriptionType getType() { return type; }
    public void setType(SubscriptionType type) { this.type = type; }
    
    public double getMonthlyCost() { return monthlyCost; }
    public void setMonthlyCost(double monthlyCost) { this.monthlyCost = monthlyCost; }
    
    public LocalDate getNextBillingDate() { return nextBillingDate; }
    public void setNextBillingDate(LocalDate nextBillingDate) { this.nextBillingDate = nextBillingDate; }
    
    @Override
    public String toString() {
        return name + " (" + provider + ") - $" + monthlyCost + "/month";
    }
}
