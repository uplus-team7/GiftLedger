package springboot.giftledger.event.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.giftledger.enums.EventType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Long eventId;
    private String eventName;
    private String eventType;
    private LocalDateTime eventDate;
    private String location;
    @JsonProperty("isOwner")
    private boolean isOwner;
    
    private Long totalAmount;
}
