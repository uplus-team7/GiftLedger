package springboot.giftledger.event.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.event.dto.EventDto;
import springboot.giftledger.event.dto.EventListResponse;
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.service.EventService;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerDetailGiftLogTest {
	
	
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventDto = EventDto.builder()
                .eventId(1L)
                .eventName("테스트 이벤트")
                .build();
    }
    
    @Test
    @DisplayName("이벤트 목록 조회 성공 ")
    void detcailsGiftLog_success() throws Exception {

        String email = "user1@test.com";

        EventUpdateResponse response =
        		EventUpdateResponse.builder()
                        .event(eventDto)
                        .build();

        ResultDto<EventUpdateResponse> result =
                ResultDto.of("success", response);

        given(eventService.detailsGiftLog(any(), anyLong() ))
                .willReturn(result);

        mockMvc.perform(
                get("/events/details/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("success"))
        .andExpect(jsonPath("$.data.event.eventName").value("테스트 이벤트"));
    }
    
    

}
