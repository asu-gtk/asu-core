package kg.asugtk.common.dto;

public class DispatchMessageDTO {
    private String messageId;
    private String senderRole;   // DRIVER | DISPATCHER
    private String targetTruckId; // truck code или "ALL"
    private String type;          // ALERT_FUEL | ALERT_ROAD | ALERT_SOS | ORDER_ROUTE | ORDER_BVR | INFO
    private String text;
    private String direction;     // DRIVER_TO_DISPATCHER | DISPATCHER_TO_DRIVER
    private double lat;
    private double lon;
    private long timestamp;

    // --- getters / setters ---

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getTargetTruckId() { return targetTruckId; }
    public void setTargetTruckId(String targetTruckId) { this.targetTruckId = targetTruckId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
