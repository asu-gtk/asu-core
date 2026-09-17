package kg.asugtk.telemetry.service;

import kg.asugtk.common.dto.TelemetryPacketDTO;
import kg.asugtk.telemetry.entity.TruckTelemetryEntity;
import kg.asugtk.telemetry.repository.TruckTelemetryRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TelemetryPersistenceService {

    private final TruckTelemetryRepository repo;

    public TelemetryPersistenceService(TruckTelemetryRepository repo) {
        this.repo = repo;
    }

    /**
     * Асинхронно сохраняет телеметрический пакет в TimescaleDB.
     * Вызывается из AsuStompController для каждого входящего пакета.
     * Не блокирует WebSocket-поток.
     */
    @Async("telemetryExecutor")
    @Transactional
    public void saveAsync(TelemetryPacketDTO packet) {
        TruckTelemetryEntity entity = new TruckTelemetryEntity();
        entity.setTruckId(packet.getTruckId());
        entity.setTruckNumber(packet.getTruckNumber());
        entity.setDriverName(packet.getDriverName());
        entity.setCapturedAt(Instant.ofEpochMilli(
                packet.getServerTimestamp() > 0 ? packet.getServerTimestamp() : System.currentTimeMillis()
        ));
        entity.setLat(packet.getLat());
        entity.setLon(packet.getLon());
        entity.setSpeedKmh(packet.getSpeedKmh());
        entity.setHeadingDeg(packet.getHeadingDeg());
        entity.setFuelLiters(packet.getFuelLiters());
        entity.setLoadWeightTons(packet.getLoadWeightTons());
        entity.setStatus(packet.getStatus());
        repo.save(entity);
    }
}
