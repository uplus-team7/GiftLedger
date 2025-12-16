package springboot.giftledger.event.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.event.dto.EventDto;
import springboot.giftledger.event.dto.EventUpdateRequest;
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.dto.GiftLogDto;
import springboot.giftledger.event.service.EventService;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EventControllerUpdateTest {
	
	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;
    
    @Autowired
    private ObjectMapper objectMapper;
    

    
    @Test
    @DisplayName("이벤트 수정 성공")
    void updateEvent_success() throws Exception {
        // given
        long eventId = 1L;
        String email = "test@test.com";

        EventUpdateRequest request = EventUpdateRequest.builder()
                .event(EventDto.builder()
                        .eventId(eventId)
                        .eventName("테스트 이벤트")
                        .build())
                .acquaintance(AcquaintanceDto.builder()
                        .acquaintanceId(10L)
                        .name("김철수")
                        .build())
                .giftLog(GiftLogDto.builder()
                        .amount(100000L)
                        .actionType("출금")
                        .payMethod("현금")
                        .build())
                .build();

        EventUpdateResponse response = EventUpdateResponse.builder()
                .event(request.getEvent())
                .acquaintance(request.getAcquaintance())
                .giftLog(request.getGiftLog())
                .build();

        ResultDto<EventUpdateResponse> result =
                ResultDto.of("success", response);

        given(eventService.updateEvent(eq(eventId), any(EventUpdateRequest.class), any()))
                .willReturn(result);

        // when & then
        mockMvc.perform(put("/events/{eventId}", eventId)
        				.with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andExpect(jsonPath("$.data.event.eventName").value("테스트 이벤트"))
                .andExpect(jsonPath("$.data.acquaintance.name").value("김철수"))
                .andExpect(jsonPath("$.data.giftLog.amount").value(100000));
    }
    
    @Test
    @DisplayName("이벤트 수정 실패 - 인증 누락(email null)")
    void updateEvent_fail_noAuthentication() throws Exception {
        // given
        long eventId = 1L;

        EventUpdateRequest request = EventUpdateRequest.builder()
                .event(EventDto.builder().eventId(eventId).build())
                .build();

        // service에서 email null 검증 예외 발생하도록 설정
        given(eventService.updateEvent(eq(eventId), any(EventUpdateRequest.class), isNull()))
                .willThrow(new IllegalArgumentException("email이 null 입니다."));

        // when & then
        mockMvc.perform(put("/events/{eventId}", eventId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("IllegalArgumentException"))
                .andExpect(jsonPath("$.message").value("email이 null 입니다."));
    }
}
