package kg.asugtk.telemetry.service;

import kg.asugtk.common.dto.TelemetryPacket;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelemetryBufferService {

    private final Map<String, TelemetryPacket> latestVehicleTelemetry = new ConcurrentHashMap<>();

    public void updateTelemetry(TelemetryPacket packet) {
        if (packet != null && packet.getVehicleCode() != null) {
            latestVehicleTelemetry.put(packet.getVehicleCode(), packet);
        }
    }

    public TelemetryPacket getLatest(String vehicleCode) {
        return latestVehicleTelemetry.get(vehicleCode);
    }

    public Collection<TelemetryPacket> getAllLatest() {
        return latestVehicleTelemetry.values();
    }
}
