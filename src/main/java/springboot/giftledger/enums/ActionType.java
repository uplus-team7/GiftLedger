package springboot.giftledger.enums;

public enum ActionType {
    GIVE("출금", "보냄"),
    TAKE("입금", "받음");

    private final String type;
    private final String description;

    ActionType(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
    
    public static ActionType fromDescription(String str) {
    	
    	
        for (ActionType actionType : ActionType.values()) {
            if (actionType.description.equals(str) || actionType.type.equals(str)) {
                return actionType;
            }
        }
        throw new IllegalArgumentException("Unknown description: " + str);
    }
}
