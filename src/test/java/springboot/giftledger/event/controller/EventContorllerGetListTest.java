package springboot.giftledger.event.controller;

import static org.mockito.ArgumentMatchers.any;
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
import springboot.giftledger.event.service.EventService;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)   // ❗ addFilters = false 제거
class EventControllerGetListTest {

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
    @DisplayName("이벤트 목록 조회 성공")
    void eventList_success() throws Exception {

        String email = "user1@test.com";

        EventListResponse response =
                EventListResponse.builder()
                        .event(eventDto)
                        .build();

        Page<EventListResponse> page =
                new PageImpl<>(List.of(response));

        ResultDto<Page<EventListResponse>> result =
                ResultDto.of("success", page);

        given(eventService.eventList(any(), any(Pageable.class)))
        	.willReturn(result);

        mockMvc.perform(
                get("/events")
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(
                                        email,   // principal = String
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("success"))
        .andExpect(jsonPath("$.data.content").isArray());
    }
    
    @Test
    @DisplayName("이벤트 목록 조회 실패 - 권한 없음")
    void eventList_fail() throws Exception {    	
    	
    	ResultDto<Page<EventListResponse>> result =
    			ResultDto.of("fail", null);
    	
        given(eventService.eventList(any(), any(Pageable.class)))
    	.willThrow(new IllegalArgumentException("email이 null 입니다."));
    	
    	mockMvc.perform(
    			get("/events")
    			)
    	.andExpect(status().isBadRequest())
    	.andExpect(jsonPath("$.result").value("IllegalArgumentException"));
    }
}