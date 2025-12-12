package springboot.giftledger.event.service;

import springboot.giftledger.event.dto.EventRequestDto;
import springboot.giftledger.event.dto.EventResultDto;
import springboot.giftledger.security.MyUserDetails;

public interface EventService {
    EventResultDto insertEvent(String email, EventRequestDto eventRequestDto);

    EventResultDto deleteEvent(String email, Long eventId);

    EventResultDto detailsEvent(String email, Long eventId);
}
