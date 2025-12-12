package springboot.giftledger.event.dto;

import springboot.giftledger.enums.PayMethod;
import springboot.giftledger.enums.Relation;

public class DetailsDto {
    private Long acquaintanceId;
    private Long giftLogId;
    private String name;
    private Relation relation;
    private String groupName;
    private String phone;

    private Long amount;
    private PayMethod payMethod;
    private String memo;
}
