package models;

public class Electricity extends Utility {
    private double meterReading;
    private double ratePerKwh;
    
    public Electricity(String name, String provider, String accountNumber, double ratePerKwh) {
        super(name, provider, accountNumber);
        this.ratePerKwh = ratePerKwh;
        this.meterReading = 0.0;
    }
    
    public double getMeterReading() { return meterReading; }
    public void setMeterReading(double meterReading) { this.meterReading = meterReading; }
    
    public double getRatePerKwh() { return ratePerKwh; }
    public void setRatePerKwh(double ratePerKwh) { this.ratePerKwh = ratePerKwh; }
    
    public double calculateBill(double previousReading) {
        double consumption = meterReading - previousReading;
        return consumption * ratePerKwh;
    }
}