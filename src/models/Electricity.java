package models;

public class Electricity {
    private String id;  // Add this field
    private String name;
    private String provider;
    private String accountNumber;
    private double ratePerKwh;
    private double meterReading;
    
    public Electricity(String name, String provider, String accountNumber, double ratePerKwh) {
        this.name = name;
        this.provider = provider;
        this.accountNumber = accountNumber;
        this.ratePerKwh = ratePerKwh;
        this.meterReading = 0.0;
    }
    
    // Add getter and setter for id
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public double getRatePerKwh() {
        return ratePerKwh;
    }
    
    public void setRatePerKwh(double ratePerKwh) {
        this.ratePerKwh = ratePerKwh;
    }
    
    public double getMeterReading() {
        return meterReading;
    }
    
    public void setMeterReading(double meterReading) {
        this.meterReading = meterReading;
    }
    
    public double calculateBill(double previousReading) {
        double consumption = meterReading - previousReading;
        if (consumption < 0) {
            consumption = 0; // Prevent negative consumption
        }
        return consumption * ratePerKwh;
    }
}