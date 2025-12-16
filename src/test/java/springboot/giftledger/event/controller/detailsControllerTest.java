package springboot.giftledger.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.event.dto.*;
import springboot.giftledger.event.service.EventService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class detailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    EventRequestDto setRequestData(){
        AcquaintanceDto acquaintance = AcquaintanceDto.builder()
                .name("홍길동")
                .phone("010-1234-5678")
                .relation("친구")
                .groupName("고등학교 동창")
                .build();

        EventDto eventDetail = EventDto.builder()
                .eventName("길동이 결혼식")
                .eventType("결혼")
                .eventDate(LocalDateTime.of(2024, 5, 20, 12, 30))
                .location("여의도 웨딩컨벤션")
                .isOwner(false)
                .build();

        GiftLogDto giftLog = GiftLogDto.builder()
                .amount(100000L)
                .payMethod("보냄")
                .actionType("현금")
                .memo("식권 받음, 뷔페 맛있었음")
                .build();

        return EventRequestDto.builder()
                .eventDto(eventDetail)
                .acquaintanceDto(acquaintance)
                .giftLogDto(giftLog)
                .build();
    }

    EventDetailsResultDto setDetailsResultData() {

        EventDto eventDto = EventDto.builder()
                .eventId(23L)
                .eventName("박민수 자녀 돌잔치")
                .eventType("생일")
                .eventDate(LocalDateTime.of(2025, 5, 5, 12, 0))
                .location("워커힐 호텔")
                .isOwner(true)
                .build();

        // 2. 첫 번째 손님 데이터 (최우식 + 10만원)
        GuestLogDto guest1 = GuestLogDto.builder()
                .acquaintanceDto(AcquaintanceDto.builder()
                        .acquaintanceId(11L)
                        .name("최우식")
                        .relation("친구")
                        .groupName("대학교")
                        .phone("010-1122-3344")
                        .build())
                .giftLogDto(GiftLogDto.builder()
                        .giftLogId(27L)
                        .amount(100000L)
                        .actionType("받음")
                        .payMethod("현금")
                        .memo("돌반지 받음")
                        .build())
                .build();

        // 3. 두 번째 손님 데이터 (이광수 + 5만원)
        GuestLogDto guest2 = GuestLogDto.builder()
                .acquaintanceDto(AcquaintanceDto.builder()
                        .acquaintanceId(20L)
                        .name("이광수")
                        .relation("친구")
                        .groupName("대학교")
                        .phone("010-3692-5814")
                        .build())
                .giftLogDto(GiftLogDto.builder()
                        .giftLogId(28L)
                        .amount(50000L)
                        .actionType("받음")
                        .payMethod("현금")
                        .memo("봉투 받음")
                        .build())
                .build();

        // 4. 최종 결과 조립
        return EventDetailsResultDto.builder()
                .result("success")
                .eventDto(eventDto)
                .guestLogDtos(List.of(guest1, guest2)) // 리스트에 담기
                .build();
    }

    @Test
    @DisplayName("이벤트 상세 조회 성공 테스트")
    @WithMockUser(username = "user3@test.com", roles = "USER")
    void detailsEvent_success() throws Exception {
        // [Given]
        Long eventId = 1L;
        EventDetailsResultDto mockResponse = setDetailsResultData(); // 상세 조회용 결과 데이터 생성

        given(eventService.detailsEvent(any(), any()))
                .willReturn(mockResponse);

        // [When]
        MvcResult result = mockMvc.perform(get("/events/{eventId}", eventId) // URL 경로 변수 바인딩
                        .with(csrf()) // GET은 CSRF가 필수는 아니지만, 혹시 모를 설정에 대비해 안전하게 추가
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // [Then]
        String jsonString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        EventDetailsResultDto responseBody = objectMapper.readValue(jsonString, EventDetailsResultDto.class);

        assertAll("상세 조회 검증",
                () -> assertEquals("success", responseBody.getResult()),
                () -> assertEquals("박민수 자녀 돌잔치", responseBody.getEventDto().getEventName())
        );
    }

    @Test
    @DisplayName("상세 페이지 내 추가 등록 성공 테스트")
    @WithMockUser(username = "user3@test.com", roles = "USER")
    void insertEventOnDetails_success() throws Exception {
        // [Given]
        Long eventId = 1L;
        EventRequestDto mockRequest = setRequestData();       // 요청 데이터
        EventDetailsResultDto mockResponse = setDetailsResultData(); // 응답 데이터 (Details용)

        given(eventService.insertEventOnDetails(any(), any(), any()))
                .willReturn(mockResponse);

        // [When] POST 요청 (URL에 eventId 포함)
        MvcResult result = mockMvc.perform(post("/events/{eventId}", eventId)
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        // [Then]
        String jsonString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        EventDetailsResultDto responseBody = objectMapper.readValue(jsonString, EventDetailsResultDto.class);

        assertEquals("success", responseBody.getResult());
    }
}
