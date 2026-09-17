package kg.asugtk.common.dto;

import java.time.LocalDateTime;

public class TelemetryPacketDTO {
    private String truckId;
    private String truckNumber;
    private String driverName;
    private double lat;
    private double lon;
    private double speedKmh;
    private double headingDeg;
    private double fuelLiters;
    private double loadWeightTons;
    private String status; // LOADING, HAULING, UNLOADING, IDLE, SOS
    private long serverTimestamp;

    // --- getters / setters ---

    public String getTruckId() { return truckId; }
    public void setTruckId(String truckId) { this.truckId = truckId; }

    public String getTruckNumber() { return truckNumber; }
    public void setTruckNumber(String truckNumber) { this.truckNumber = truckNumber; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public double getSpeedKmh() { return speedKmh; }
    public void setSpeedKmh(double speedKmh) { this.speedKmh = speedKmh; }

    public double getHeadingDeg() { return headingDeg; }
    public void setHeadingDeg(double headingDeg) { this.headingDeg = headingDeg; }

    public double getFuelLiters() { return fuelLiters; }
    public void setFuelLiters(double fuelLiters) { this.fuelLiters = fuelLiters; }

    public double getLoadWeightTons() { return loadWeightTons; }
    public void setLoadWeightTons(double loadWeightTons) { this.loadWeightTons = loadWeightTons; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getServerTimestamp() { return serverTimestamp; }
    public void setServerTimestamp(long serverTimestamp) { this.serverTimestamp = serverTimestamp; }
}
