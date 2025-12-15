package springboot.giftledger.event;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.giftledger.event.dto.EventDetailsResultDto;
import springboot.giftledger.event.service.EventService;

@SpringBootTest
@Transactional
public class DetailsEventTest {
    @Autowired
    private EventService eventService;

    @Test
    void detailsTest(){
        String email = "user3@test.com";
        Long eventId = 11L;

        EventDetailsResultDto eventDetailsResultDto = eventService.detailsEvent(email,eventId);

        assert(eventDetailsResultDto != null);
        assert(eventDetailsResultDto.getResult().equals("success"));
    }
}
