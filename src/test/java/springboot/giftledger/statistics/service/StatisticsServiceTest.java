package springboot.giftledger.statistics.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import springboot.giftledger.auth.dto.StatisticsDto;
import springboot.giftledger.auth.service.StatisticsServiceImpl;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;
import springboot.giftledger.repository.GiftLogRepository;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * StatisticsService Mock 단위 테스트
 * - Repository를 Mock으로 대체
 * - 비즈니스 로직만 검증
 */
@Slf4j
@WithMockUser
@ExtendWith(MockitoExtension.class)
@DisplayName("StatisticsService Mock 테스트")
class StatisticsServiceTest {

    @Mock
    private GiftLogRepository giftLogRepository;

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Test
    @DisplayName("통계 조회 - 정상 케이스")
    void testGetEventStatistics_Success() {
        log.info("=== [테스트 시작] 통계 조회 - 정상 케이스 ===");

        // given
        String ages = "30대";
        String eventType = "WEDDING";
        String actionType = "GIVE";

        given(giftLogRepository.findAverageAmountByAgesAndEventTypeAndActionType(
                ages, EventType.WEDDING, ActionType.GIVE))
                .willReturn(150000.0);  // 평균 15만원

        given(giftLogRepository.countByAgesAndEventTypeAndActionType(
                ages, EventType.WEDDING, ActionType.GIVE))
                .willReturn(10L);  // 총 10건

        // when
        StatisticsDto result = statisticsService.getEventStatistics(ages, eventType, actionType);

        // then
        assertThat(result.getAges()).isEqualTo("30대");
        assertThat(result.getEventType()).isEqualTo("WEDDING");
        assertThat(result.getActionType()).isEqualTo("GIVE");
        assertThat(result.getAverageAmount()).isEqualTo(150000L);
        assertThat(result.getTotalCount()).isEqualTo(10L);

        log.info("✅ [테스트 성공] 평균: {}원, 건수: {}건",
                result.getAverageAmount(), result.getTotalCount());
        log.info("=== [테스트 종료] 통계 조회 - 정상 케이스 ===\n");
    }

    @Test
    @DisplayName("통계 조회 - 평균 금액이 null인 경우 0 반환")
    void testGetEventStatistics_NullAverage() {
        log.info("=== [테스트 시작] 통계 조회 - null 평균 ===");

        // given
        String ages = "20대";
        String eventType = "BIRTHDAY";
        String actionType = "GIVE";

        given(giftLogRepository.findAverageAmountByAgesAndEventTypeAndActionType(
                ages, EventType.BIRTHDAY, ActionType.GIVE))
                .willReturn(null);  // 데이터 없음

        given(giftLogRepository.countByAgesAndEventTypeAndActionType(
                ages, EventType.BIRTHDAY, ActionType.GIVE))
                .willReturn(0L);

        // when
        StatisticsDto result = statisticsService.getEventStatistics(ages, eventType, actionType);

        // then
        assertThat(result.getAverageAmount()).isEqualTo(0L);
        assertThat(result.getTotalCount()).isEqualTo(0L);

        log.info("✅ [테스트 성공] null 값을 0으로 처리");
        log.info("=== [테스트 종료] 통계 조회 - null 평균 ===\n");
    }

    @Test
    @DisplayName("통계 조회 - 총 건수가 null인 경우 0 반환")
    void testGetEventStatistics_NullCount() {
        log.info("=== [테스트 시작] 통계 조회 - null 건수 ===");

        // given
        String ages = "40대";
        String eventType = "FUNERAL";
        String actionType = "TAKE";

        given(giftLogRepository.findAverageAmountByAgesAndEventTypeAndActionType(
                ages, EventType.FUNERAL, ActionType.TAKE))
                .willReturn(100000.0);

        given(giftLogRepository.countByAgesAndEventTypeAndActionType(
                ages, EventType.FUNERAL, ActionType.TAKE))
                .willReturn(null);  // null 반환

        // when
        StatisticsDto result = statisticsService.getEventStatistics(ages, eventType, actionType);

        // then
        assertThat(result.getAverageAmount()).isEqualTo(100000L);
        assertThat(result.getTotalCount()).isEqualTo(0L);  // null → 0

        log.info("✅ [테스트 성공] null 건수를 0으로 처리");
        log.info("=== [테스트 종료] 통계 조회 - null 건수 ===\n");
    }

    @Test
    @DisplayName("통계 조회 - 잘못된 EventType으로 예외 처리")
    void testGetEventStatistics_InvalidEventType() {
        log.info("=== [테스트 시작] 통계 조회 - 잘못된 EventType ===");

        // given
        String ages = "30대";
        String eventType = "INVALID_TYPE";  // 존재하지 않는 타입
        String actionType = "GIVE";

        // when
        StatisticsDto result = statisticsService.getEventStatistics(ages, eventType, actionType);

        // then
        assertThat(result.getAges()).isEqualTo("30대");
        assertThat(result.getEventType()).isEqualTo("INVALID_TYPE");
        assertThat(result.getAverageAmount()).isEqualTo(0L);
        assertThat(result.getTotalCount()).isEqualTo(0L);
        assertThat(result.getActionType()).isEqualTo("GIVE");

        log.info("✅ [테스트 성공] 잘못된 EventType 예외 처리");
        log.info("=== [테스트 종료] 통계 조회 - 잘못된 EventType ===\n");
    }

    @Test
    @DisplayName("통계 조회 - 잘못된 ActionType으로 예외 처리")
    void testGetEventStatistics_InvalidActionType() {
        log.info("=== [테스트 시작] 통계 조회 - 잘못된 ActionType ===");

        // given
        String ages = "20대";
        String eventType = "WEDDING";
        String actionType = "INVALID_ACTION";  // 존재하지 않는 타입

        // when
        StatisticsDto result = statisticsService.getEventStatistics(ages, eventType, actionType);

        // then
        assertThat(result.getAverageAmount()).isEqualTo(0L);
        assertThat(result.getTotalCount()).isEqualTo(0L);

        log.info("✅ [테스트 성공] 잘못된 ActionType 예외 처리");
        log.info("=== [테스트 종료] 통계 조회 - 잘못된 ActionType ===\n");
    }

    @Test
    @DisplayName("통계 조회 - TAKE 액션 타입")
    void testGetEventStatistics_TakeAction() {
        log.info("=== [테스트 시작] 통계 조회 - TAKE 액션 ===");

        // given
        String ages = "50대";
        String eventType = "WEDDING";
        String actionType = "TAKE";

        given(giftLogRepository.findAverageAmountByAgesAndEventTypeAndActionType(
                ages, EventType.WEDDING, ActionType.TAKE))
                .willReturn(200000.0);  // 평균 20만원

        given(giftLogRepository.countByAgesAndEventTypeAndActionType(
                ages, EventType.WEDDING, ActionType.TAKE))
                .willReturn(5L);

        // when
        StatisticsDto result = statisticsService.getEventStatistics(ages, eventType, actionType);

        // then
        assertThat(result.getActionType()).isEqualTo("TAKE");
        assertThat(result.getAverageAmount()).isEqualTo(200000L);
        assertThat(result.getTotalCount()).isEqualTo(5L);

        log.info("✅ [테스트 성공] TAKE 액션 타입 처리");
        log.info("=== [테스트 종료] 통계 조회 - TAKE 액션 ===\n");
    }

    @Test
    @DisplayName("통계 조회 - 다양한 이벤트 타입")
    void testGetEventStatistics_DifferentEventTypes() {
        log.info("=== [테스트 시작] 통계 조회 - 다양한 이벤트 타입 ===");

        // BIRTHDAY 테스트
        given(giftLogRepository.findAverageAmountByAgesAndEventTypeAndActionType(
                "30대", EventType.BIRTHDAY, ActionType.GIVE))
                .willReturn(50000.0);
        given(giftLogRepository.countByAgesAndEventTypeAndActionType(
                "30대", EventType.BIRTHDAY, ActionType.GIVE))
                .willReturn(20L);

        StatisticsDto birthdayResult = statisticsService.getEventStatistics("30대", "BIRTHDAY", "GIVE");

        assertThat(birthdayResult.getEventType()).isEqualTo("BIRTHDAY");
        assertThat(birthdayResult.getAverageAmount()).isEqualTo(50000L);
        assertThat(birthdayResult.getTotalCount()).isEqualTo(20L);

        // FUNERAL 테스트
        given(giftLogRepository.findAverageAmountByAgesAndEventTypeAndActionType(
                "40대", EventType.FUNERAL, ActionType.GIVE))
                .willReturn(100000.0);
        given(giftLogRepository.countByAgesAndEventTypeAndActionType(
                "40대", EventType.FUNERAL, ActionType.GIVE))
                .willReturn(3L);

        StatisticsDto funeralResult = statisticsService.getEventStatistics("40대", "FUNERAL", "GIVE");

        assertThat(funeralResult.getEventType()).isEqualTo("FUNERAL");
        assertThat(funeralResult.getAverageAmount()).isEqualTo(100000L);

        log.info("✅ [테스트 성공] 다양한 이벤트 타입 처리");
        log.info("=== [테스트 종료] 통계 조회 - 다양한 이벤트 타입 ===\n");
    }

    @Test
    @DisplayName("통계 조회 - Double to Long 변환 확인")
    void testGetEventStatistics_DoubleToLongConversion() {
        log.info("=== [테스트 시작] 통계 조회 - Double to Long 변환 ===");

        // given
        String ages = "30대";
        String eventType = "WEDDING";
        String actionType = "GIVE";

        // 소수점이 있는 평균값
        given(giftLogRepository.findAverageAmountByAgesAndEventTypeAndActionType(
                ages, EventType.WEDDING, ActionType.GIVE))
                .willReturn(123456.789);  // 소수점 있는 값

        given(giftLogRepository.countByAgesAndEventTypeAndActionType(
                ages, EventType.WEDDING, ActionType.GIVE))
                .willReturn(7L);

        // when
        StatisticsDto result = statisticsService.getEventStatistics(ages, eventType, actionType);

        // then
        assertThat(result.getAverageAmount()).isEqualTo(123456L);  // 소수점 버림

        log.info("✅ [테스트 성공] Double → Long 변환 (소수점 버림)");
        log.info("=== [테스트 종료] 통계 조회 - Double to Long 변환 ===\n");
    }
}