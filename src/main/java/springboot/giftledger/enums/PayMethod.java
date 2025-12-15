package springboot.giftledger.enums;

public enum PayMethod {
    CASH("현금"),
    BANK_TRANSFER("계좌이체"),
    KAKAOPAY("카카오페이");

    private final String description;

    PayMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
    
    
    public static PayMethod fromDescription(String description) {
        for (PayMethod type : PayMethod.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown description: " + description);
    }
}
