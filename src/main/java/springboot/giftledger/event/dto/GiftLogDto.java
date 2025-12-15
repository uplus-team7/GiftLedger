package springboot.giftledger.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.PayMethod;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftLogDto {

    private Long giftLogId;
    private Long amount;
    private ActionType actionType;
    private PayMethod payMethod;
    private String memo;
}
