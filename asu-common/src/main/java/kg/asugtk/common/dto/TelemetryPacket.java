package kg.asugtk.common.dto;

import kg.asugtk.common.model.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryPacket {
    private String deviceId;
    private String vehicleCode;
    private Instant timestamp;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speedKmH;
    private double heading;
    private double payloadTons;
    private double fuelLevelLiters;
    private boolean bodyRaised;
    private VehicleStatus status;
}
