package springboot.giftledger.enums;

public enum Relation {
    FAMILY("가족"),
    FRIEND("친구"),
    COWORKER("직장동료"),
    ETC("기타");

    private final String description;

    Relation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
    
    public static Relation fromDescription(String description) {
        for (Relation type : Relation.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown description: " + description);
    }
}
