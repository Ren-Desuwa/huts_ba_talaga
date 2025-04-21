package models;

public class Gas extends Utility {
    private double meterReading;
    private double ratePerUnit;
    
    public Gas(String name, String provider, String accountNumber, double ratePerUnit) {
        super(name, provider, accountNumber);
        this.ratePerUnit = ratePerUnit;
        this.meterReading = 0.0;
    }
    
    public double getMeterReading() { return meterReading; }
    public void setMeterReading(double meterReading) { this.meterReading = meterReading; }
    
    public double getRatePerUnit() { return ratePerUnit; }
    public void setRatePerUnit(double ratePerUnit) { this.ratePerUnit = ratePerUnit; }
    
    public double calculateBill(double previousReading) {
        double consumption = meterReading - previousReading;
        return consumption * ratePerUnit;
    }
}