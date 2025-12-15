package springboot.giftledger.enums;

public enum EventType {
    WEDDING("결혼"),
    FUNERAL("장례"),
    BIRTHDAY("생일"),
    ETC("기타");

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
    public static EventType fromDescription(String description) {
        for (EventType type : EventType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown description: " + description);
    }
}
