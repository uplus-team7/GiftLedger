package springboot.giftledger.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestLogDto {

    private AcquaintanceDto acquaintanceDto;
    private GiftLogDto giftLogDto;
}
