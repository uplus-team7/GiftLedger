package springboot.giftledger.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDto {
    private EventDto eventDto;
    private AcquaintanceDto acquaintanceDto;
    private GiftLogDto giftLogDto;
}
