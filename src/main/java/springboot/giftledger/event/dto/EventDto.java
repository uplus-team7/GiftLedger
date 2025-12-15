package springboot.giftledger.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.giftledger.enums.EventType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long eventId;
    private String eventName;
    private EventType eventType;
    private LocalDateTime eventDate;
    private String location;
    private boolean isOwner;
}
