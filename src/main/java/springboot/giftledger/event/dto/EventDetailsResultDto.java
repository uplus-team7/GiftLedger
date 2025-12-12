package springboot.giftledger.event.dto;

import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;

import java.time.LocalDateTime;
import java.util.List;

public class EventDetailsResultDto {
    private Long eventId;
    private EventType eventType;
    private String eventName;
    private LocalDateTime eventDate;
    private String location;
    private String hostName;
    private ActionType actionType;

    private List<DetailsDto> details;
}
