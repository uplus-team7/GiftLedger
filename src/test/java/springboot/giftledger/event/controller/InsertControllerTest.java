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
import org.springframework.test.web.servlet.ResultMatcher;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.event.dto.*;
import springboot.giftledger.event.service.EventService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class InsertControllerTest {

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
                .payMethod("현금")
                .actionType("보냄")
                .memo("식권 받음, 뷔페 맛있었음")
                .build();

        return EventRequestDto.builder()
                .eventDto(eventDetail)
                .acquaintanceDto(acquaintance)
                .giftLogDto(giftLog)
                .build();
    }

    EventResultDto setResultData(){
        AcquaintanceDto acquaintance = AcquaintanceDto.builder()
                .acquaintanceId(1L)
                .name("홍길동")
                .phone("010-1234-5678")
                .relation("친구")
                .groupName("고등학교 동창")
                .build();

        EventDto eventDetail = EventDto.builder()
                .eventId(1L)
                .eventName("길동이 결혼식")
                .eventType("결혼")
                .eventDate(LocalDateTime.of(2024, 5, 20, 12, 30))
                .location("여의도 웨딩컨벤션")
                .isOwner(false)
                .build();

        GiftLogDto giftLog = GiftLogDto.builder()
                .giftLogId(1L)
                .amount(100000L)
                .payMethod("현금")
                .actionType("보냄")
                .memo("식권 받음, 뷔페 맛있었음")
                .build();

        return EventResultDto.builder()
                .result("success")
                .eventDto(eventDetail)
                .acquaintanceDto(acquaintance)
                .giftLogDto(giftLog)
                .build();
    }

    @Test
    @DisplayName("이벤트 등록 성공 테스트")
    @WithMockUser(username = "user3@test.com", roles = "USER")
    void insertEvent_success() throws Exception {
        String email = "user3@test.com";
        EventRequestDto mockRequest = setRequestData();
        EventResultDto mockResponse = setResultData();

        given(eventService.insertEvent(any(), any()))
                .willReturn(mockResponse);

        MvcResult result = mockMvc.perform(post("/events")
                        .with(csrf())
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(mockRequest)))
                        .andExpect(status().isOk())
                        .andReturn();

        String jsonString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        EventResultDto responseBody = objectMapper.readValue(jsonString, EventResultDto.class);

        assertAll("이벤트 등록 응답 검증",
                () -> assertEquals("success", responseBody.getResult()),
                () -> assertEquals("홍길동", responseBody.getAcquaintanceDto().getName()),
                () -> assertEquals("길동이 결혼식", responseBody.getEventDto().getEventName()),
                () -> assertEquals(100000L, responseBody.getGiftLogDto().getAmount())
        );

    }



}
