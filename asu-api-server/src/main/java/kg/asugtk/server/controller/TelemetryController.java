package kg.asugtk.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.asugtk.common.dto.TelemetryPacket;
import kg.asugtk.telemetry.service.TelemetryBufferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/api/v1/telemetry")
@Tag(name = "Realtime Telemetry", description = "Сбор и получение оперативной телеметрии техники")
@CrossOrigin(origins = "*")
public class TelemetryController {

    private final TelemetryBufferService telemetryBuffer;

    @Autowired
    public TelemetryController(TelemetryBufferService telemetryBuffer) {
        this.telemetryBuffer = telemetryBuffer;
    }

    @PostMapping("/push")
    @Operation(summary = "Прием HTTP-пакета телеметрии от бортового терминала")
    public ResponseEntity<Void> pushTelemetry(@RequestBody TelemetryPacket packet) {
        telemetryBuffer.updateTelemetry(packet);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current")
    @Operation(summary = "Получение актуальных координат и состояния всей техники на линии")
    public ResponseEntity<Collection<TelemetryPacket>> getAllCurrent() {
        return ResponseEntity.ok(telemetryBuffer.getAllLatest());
    }

    @GetMapping("/current/{vehicleCode}")
    @Operation(summary = "Получение последнего состояния конкретной машины")
    public ResponseEntity<TelemetryPacket> getByCode(@PathVariable String vehicleCode) {
        TelemetryPacket packet = telemetryBuffer.getLatest(vehicleCode);
        return packet != null ? ResponseEntity.ok(packet) : ResponseEntity.notFound().build();
    }
}
