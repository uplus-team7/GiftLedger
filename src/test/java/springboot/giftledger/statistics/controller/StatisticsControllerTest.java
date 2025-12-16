package springboot.giftledger.statistics.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import springboot.giftledger.auth.controller.StatisticsController;
import springboot.giftledger.auth.dto.StatisticsDto;
import springboot.giftledger.auth.service.StatisticsService;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * StatisticsController 통합 테스트
 * - MockMvc를 사용한 HTTP 요청/응답 테스트
 * - Service는 Mock으로 대체
 */
@Slf4j
@WebMvcTest(StatisticsController.class)
@DisplayName("StatisticsController 테스트")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    @DisplayName("GET /statistics/event - 정상 요청")
    void testGetEventStatistics_Success() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - 정상 요청 ===");

        // given
        StatisticsDto mockResponse = StatisticsDto.builder()
                .ages("30대")
                .eventType("WEDDING")
                .actionType("GIVE")
                .averageAmount(150000L)
                .totalCount(10L)
                .build();

        given(statisticsService.getEventStatistics("30대", "WEDDING", "GIVE"))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/statistics/event")
                        .param("ages", "30대")
                        .param("eventType", "WEDDING")
                        .param("actionType", "GIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ages", is("30대")))
                .andExpect(jsonPath("$.eventType", is("WEDDING")))
                .andExpect(jsonPath("$.actionType", is("GIVE")))
                .andExpect(jsonPath("$.averageAmount", is(150000)))
                .andExpect(jsonPath("$.totalCount", is(10)));

        log.info("✅ [테스트 성공] 200 OK, 정상 응답");
        log.info("=== [테스트 종료] GET /statistics/event - 정상 요청 ===\n");
    }

    @Test
    @DisplayName("GET /statistics/event - actionType 기본값 GIVE")
    void testGetEventStatistics_DefaultActionType() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - 기본값 GIVE ===");

        // given
        StatisticsDto mockResponse = StatisticsDto.builder()
                .ages("20대")
                .eventType("BIRTHDAY")
                .actionType("GIVE")  // 기본값
                .averageAmount(50000L)
                .totalCount(5L)
                .build();

        given(statisticsService.getEventStatistics("20대", "BIRTHDAY", "GIVE"))
                .willReturn(mockResponse);

        // when & then (actionType 파라미터 없음)
        mockMvc.perform(get("/statistics/event")
                        .param("ages", "20대")
                        .param("eventType", "BIRTHDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionType", is("GIVE")));  // 기본값 확인

        log.info("✅ [테스트 성공] actionType 기본값 GIVE 적용");
        log.info("=== [테스트 종료] GET /statistics/event - 기본값 GIVE ===\n");
    }

    @Test
    @DisplayName("GET /statistics/event - TAKE 액션 타입")
    void testGetEventStatistics_TakeAction() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - TAKE 액션 ===");

        // given
        StatisticsDto mockResponse = StatisticsDto.builder()
                .ages("40대")
                .eventType("WEDDING")
                .actionType("TAKE")
                .averageAmount(200000L)
                .totalCount(3L)
                .build();

        given(statisticsService.getEventStatistics("40대", "WEDDING", "TAKE"))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/statistics/event")
                        .param("ages", "40대")
                        .param("eventType", "WEDDING")
                        .param("actionType", "TAKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actionType", is("TAKE")))
                .andExpect(jsonPath("$.averageAmount", is(200000)))
                .andExpect(jsonPath("$.totalCount", is(3)));

        log.info("✅ [테스트 성공] TAKE 액션 타입 처리");
        log.info("=== [테스트 종료] GET /statistics/event - TAKE 액션 ===\n");
    }

    @Test
    @DisplayName("GET /statistics/event - 데이터 없는 경우 (0 반환)")
    void testGetEventStatistics_NoData() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - 데이터 없음 ===");

        // given
        StatisticsDto mockResponse = StatisticsDto.builder()
                .ages("50대")
                .eventType("FUNERAL")
                .actionType("GIVE")
                .averageAmount(0L)  // 데이터 없음
                .totalCount(0L)
                .build();

        given(statisticsService.getEventStatistics("50대", "FUNERAL", "GIVE"))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/statistics/event")
                        .param("ages", "50대")
                        .param("eventType", "FUNERAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageAmount", is(0)))
                .andExpect(jsonPath("$.totalCount", is(0)));

        log.info("✅ [테스트 성공] 데이터 없을 때 0 반환");
        log.info("=== [테스트 종료] GET /statistics/event - 데이터 없음 ===\n");
    }

    @Test
    @DisplayName("GET /statistics/event - 다양한 이벤트 타입")
    void testGetEventStatistics_DifferentEventTypes() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - 다양한 이벤트 타입 ===");

        // BIRTHDAY 테스트
        StatisticsDto birthdayResponse = StatisticsDto.builder()
                .ages("30대")
                .eventType("BIRTHDAY")
                .actionType("GIVE")
                .averageAmount(30000L)
                .totalCount(15L)
                .build();

        given(statisticsService.getEventStatistics("30대", "BIRTHDAY", "GIVE"))
                .willReturn(birthdayResponse);

        mockMvc.perform(get("/statistics/event")
                        .param("ages", "30대")
                        .param("eventType", "BIRTHDAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventType", is("BIRTHDAY")))
                .andExpect(jsonPath("$.averageAmount", is(30000)));

        // FUNERAL 테스트
        StatisticsDto funeralResponse = StatisticsDto.builder()
                .ages("40대")
                .eventType("FUNERAL")
                .actionType("GIVE")
                .averageAmount(100000L)
                .totalCount(2L)
                .build();

        given(statisticsService.getEventStatistics("40대", "FUNERAL", "GIVE"))
                .willReturn(funeralResponse);

        mockMvc.perform(get("/statistics/event")
                        .param("ages", "40대")
                        .param("eventType", "FUNERAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventType", is("FUNERAL")))
                .andExpect(jsonPath("$.averageAmount", is(100000)));

        log.info("✅ [테스트 성공] 다양한 이벤트 타입 처리");
        log.info("=== [테스트 종료] GET /statistics/event - 다양한 이벤트 타입 ===\n");
    }

    @Test
    @DisplayName("GET /statistics/event - 다양한 연령대")
    void testGetEventStatistics_DifferentAges() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - 다양한 연령대 ===");

        // 20대 테스트
        StatisticsDto response20s = StatisticsDto.builder()
                .ages("20대")
                .eventType("WEDDING")
                .actionType("GIVE")
                .averageAmount(100000L)
                .totalCount(5L)
                .build();

        given(statisticsService.getEventStatistics("20대", "WEDDING", "GIVE"))
                .willReturn(response20s);

        mockMvc.perform(get("/statistics/event")
                        .param("ages", "20대")
                        .param("eventType", "WEDDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ages", is("20대")))
                .andExpect(jsonPath("$.averageAmount", is(100000)));

        // 60대 테스트
        StatisticsDto response60s = StatisticsDto.builder()
                .ages("60대")
                .eventType("WEDDING")
                .actionType("GIVE")
                .averageAmount(300000L)
                .totalCount(8L)
                .build();

        given(statisticsService.getEventStatistics("60대", "WEDDING", "GIVE"))
                .willReturn(response60s);

        mockMvc.perform(get("/statistics/event")
                        .param("ages", "60대")
                        .param("eventType", "WEDDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ages", is("60대")))
                .andExpect(jsonPath("$.averageAmount", is(300000)));

        log.info("✅ [테스트 성공] 다양한 연령대 처리");
        log.info("=== [테스트 종료] GET /statistics/event - 다양한 연령대 ===\n");
    }

    @Test
    @DisplayName("GET /statistics/event - 잘못된 파라미터 처리")
    void testGetEventStatistics_InvalidParameters() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - 잘못된 파라미터 ===");

        // given: Service에서 예외 처리하여 0 반환
        StatisticsDto mockResponse = StatisticsDto.builder()
                .ages("30대")
                .eventType("INVALID_TYPE")
                .actionType("GIVE")
                .averageAmount(0L)
                .totalCount(0L)
                .build();

        given(statisticsService.getEventStatistics("30대", "INVALID_TYPE", "GIVE"))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/statistics/event")
                        .param("ages", "30대")
                        .param("eventType", "INVALID_TYPE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageAmount", is(0)))
                .andExpect(jsonPath("$.totalCount", is(0)));

        log.info("✅ [테스트 성공] 잘못된 파라미터도 200 OK (Service에서 처리)");
        log.info("=== [테스트 종료] GET /statistics/event - 잘못된 파라미터 ===\n");
    }

    @Test
    @DisplayName("GET /statistics/event - CORS 헤더 확인")
    void testGetEventStatistics_CorsHeaders() throws Exception {
        log.info("=== [테스트 시작] GET /statistics/event - CORS 헤더 ===");

        // given
        StatisticsDto mockResponse = StatisticsDto.builder()
                .ages("30대")
                .eventType("WEDDING")
                .actionType("GIVE")
                .averageAmount(150000L)
                .totalCount(10L)
                .build();

        given(statisticsService.getEventStatistics("30대", "WEDDING", "GIVE"))
                .willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/statistics/event")
                        .param("ages", "30대")
                        .param("eventType", "WEDDING")
                        .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));

        log.info("✅ [테스트 성공] CORS 헤더 확인");
        log.info("=== [테스트 종료] GET /statistics/event - CORS 헤더 ===\n");
    }
}