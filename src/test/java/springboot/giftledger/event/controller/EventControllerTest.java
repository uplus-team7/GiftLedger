package springboot.giftledger.event.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.event.dto.EventDto;
import springboot.giftledger.event.dto.EventUpdateRequest;
import springboot.giftledger.event.service.EventService;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;
    
    @Test
    void dummy() {
        new Object().toString();
    }

    
    @Test
    @DisplayName("이벤트 수정 성공")
    void updateEvent_success() throws Exception {
//    	
//    	// Given
//    	EventUpdateRequest request = EventUpdateRequest.builder()
//                .event(EventDto.builder()
//                        .eventType("결혼")
//                        .eventName("손흥민 결혼식")
//                        .build())
//                .acquaintance(AcquaintanceDto.builder()
//                		.
//                		.build())
//                .build();
//    	
//    	// When
//    	
//    	// Then
    }

}
