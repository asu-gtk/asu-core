package kg.asugtk.api.websocket;

import kg.asugtk.common.dto.TelemetryPacketDTO;
import kg.asugtk.common.dto.DispatchMessageDTO;
import kg.asugtk.telemetry.service.TelemetryPersistenceService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

/**
 * STOMP WebSocket controller.
 *
 * Водитель → /app/telemetry     → broadcast → /topic/fleet/positions
 * Водитель → /app/driver/alert  → broadcast → /topic/dispatcher/alerts
 * Диспетчер → /app/dispatch     → broadcast → /topic/driver/{truckId}
 */
@Controller
public class AsuStompController {

    private final SimpMessagingTemplate broker;
    private final TelemetryPersistenceService persistenceService;

    public AsuStompController(SimpMessagingTemplate broker,
                              TelemetryPersistenceService persistenceService) {
        this.broker = broker;
        this.persistenceService = persistenceService;
    }

    /** Телеметрия от планшета водителя: GPS + скорость + топливо + вес */
    @MessageMapping("/telemetry")
    public void receiveTelemetry(@Payload TelemetryPacketDTO packet) {
        packet.setServerTimestamp(Instant.now().toEpochMilli());
        // Все диспетчеры подписаны на /topic/fleet/positions
        broker.convertAndSend("/topic/fleet/positions", packet);
        // Асинхронное сохранение в TimescaleDB (не блокирует WebSocket-поток)
        persistenceService.saveAsync(packet);
    }

    /** Доклад водителя диспетчеру: осыпь / мало топлива / ТО / SOS */
    @MessageMapping("/driver/alert")
    public void driverAlert(@Payload DispatchMessageDTO msg) {
        msg.setTimestamp(Instant.now().toEpochMilli());
        msg.setDirection("DRIVER_TO_DISPATCHER");
        broker.convertAndSend("/topic/dispatcher/alerts", msg);
    }

    /** Распоряжение диспетчера → конкретный борт или ALL */
    @MessageMapping("/dispatch")
    public void dispatchOrder(@Payload DispatchMessageDTO msg) {
        msg.setTimestamp(Instant.now().toEpochMilli());
        msg.setDirection("DISPATCHER_TO_DRIVER");
        if ("ALL".equalsIgnoreCase(msg.getTargetTruckId())) {
            broker.convertAndSend("/topic/driver/ALL", msg);
        } else {
            broker.convertAndSend("/topic/driver/" + msg.getTargetTruckId(), msg);
        }
    }
}
