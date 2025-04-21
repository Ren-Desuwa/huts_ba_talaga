package models;

public class Water extends Utility {
    private double meterReading;
    private double ratePerCubicMeter;
    
    public Water(String name, String provider, String accountNumber, double ratePerCubicMeter) {
        super(name, provider, accountNumber);
        this.ratePerCubicMeter = ratePerCubicMeter;
        this.meterReading = 0.0;
    }
    
    public double getMeterReading() { return meterReading; }
    public void setMeterReading(double meterReading) { this.meterReading = meterReading; }
    
    public double getRatePerCubicMeter() { return ratePerCubicMeter; }
    public void setRatePerCubicMeter(double ratePerCubicMeter) { this.ratePerCubicMeter = ratePerCubicMeter; }
    
    public double calculateBill(double previousReading) {
        double consumption = meterReading - previousReading;
        return consumption * ratePerCubicMeter;
    }
}