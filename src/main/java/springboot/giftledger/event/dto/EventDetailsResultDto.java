package springboot.giftledger.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsResultDto {
    private String result;
    private EventDto eventDto;

    private List<GuestLogDto> guestLogDtos;
}
