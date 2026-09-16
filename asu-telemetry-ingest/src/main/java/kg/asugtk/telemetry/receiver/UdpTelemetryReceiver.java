package kg.asugtk.telemetry.receiver;

import kg.asugtk.common.dto.TelemetryPacket;
import kg.asugtk.common.model.VehicleStatus;
import kg.asugtk.telemetry.service.TelemetryBufferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class UdpTelemetryReceiver {

    private static final Logger log = LoggerFactory.getLogger(UdpTelemetryReceiver.class);
    private final TelemetryBufferService bufferService;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;
    private DatagramSocket socket;

    @Autowired
    public UdpTelemetryReceiver(TelemetryBufferService bufferService) {
        this.bufferService = bufferService;
    }

    public synchronized void start(int port) {
        if (running) return;
        running = true;
        executor.submit(() -> {
            try {
                socket = new DatagramSocket(port);
                byte[] buffer = new byte[1024];
                log.info("UDP Telemetry Server listening on port {}", port);
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    parseAndStore(message);
                }
            } catch (Exception e) {
                if (running) {
                    log.error("UDP Server error", e);
                }
            }
        });
    }

    public synchronized void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        executor.shutdownNow();
    }

    /**
     * Парсинг CSV пакета телеметрии:
     * DEVICE_ID;VEHICLE_CODE;LAT;LON;SPEED;PAYLOAD;FUEL;STATUS
     */
    public void parseAndStore(String raw) {
        try {
            String[] parts = raw.trim().split(";");
            if (parts.length >= 7) {
                TelemetryPacket packet = TelemetryPacket.builder()
                        .deviceId(parts[0])
                        .vehicleCode(parts[1])
                        .latitude(Double.parseDouble(parts[2]))
                        .longitude(Double.parseDouble(parts[3]))
                        .speedKmH(Double.parseDouble(parts[4]))
                        .payloadTons(Double.parseDouble(parts[5]))
                        .fuelLevelLiters(Double.parseDouble(parts[6]))
                        .status(parts.length > 7 ? VehicleStatus.valueOf(parts[7]) : VehicleStatus.HAULING_LOADED)
                        .timestamp(Instant.now())
                        .build();
                bufferService.updateTelemetry(packet);
            }
        } catch (Exception e) {
            log.warn("Failed to parse raw telemetry datagram: {}", raw);
        }
    }
}
