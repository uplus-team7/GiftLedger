package springboot.giftledger.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import springboot.giftledger.analysis.dto.*;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Event;
import springboot.giftledger.entity.EventAcquaintance;
import springboot.giftledger.entity.GiftLog;
import springboot.giftledger.entity.Member;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;
import springboot.giftledger.enums.PayMethod;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.enums.Role;
import springboot.giftledger.repository.GiftLogRepository;
import springboot.giftledger.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("AnalysisService Mock 테스트")
class AnalysisServiceTest {

    @Mock
    private GiftLogRepository giftLogRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AnalysisServiceImpl analysisService;

    private Member testMember;
    private String testEmail = "test@mycom.com";

    @BeforeEach
    void setUp() {
        log.info("=== 테스트 준비: Mock 데이터 초기화 ===");

        testMember = Member.builder()
                .memberId(1L)
                .email(testEmail)
                .name("테스트유저")
                .password("password")
                .ages("30대")
                .role(Role.ROLE_USER)
                .build();
    }

    // ========================================
    // 1. Dashboard 테스트
    // ========================================

    @Test
    @DisplayName("Dashboard - 회수율 계산 검증")
    void testDashboard_RecoveryRateCalculation() {
        log.info("=== [테스트 시작] Dashboard - 회수율 계산 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));
        given(giftLogRepository.getTotalGiveByMemberId(1L))
                .willReturn(1000000L);  // 준 돈: 100만원
        given(giftLogRepository.getTotalTakeByMemberId(1L))
                .willReturn(800000L);   // 받은 돈: 80만원
        given(giftLogRepository.getYearlyGiveByMemberId(eq(1L), anyInt()))
                .willReturn(500000L, 400000L);  // 올해 50만, 작년 40만

        // when
        DashboardDto result = analysisService.getDashboard(testEmail);

        // then
        assertThat(result.getTotalGive()).isEqualTo(1000000L);
        assertThat(result.getTotalTake()).isEqualTo(800000L);
        assertThat(result.getBalance()).isEqualTo(-200000L);  // 80만 - 100만 = -20만
        assertThat(result.getRecoveryRate()).isEqualTo(80.0);  // (80만 / 100만) * 100 = 80%
        assertThat(result.getYearChangeAmount()).isEqualTo(100000L);  // 50만 - 40만 = 10만
        assertThat(result.getYearChangePercent()).isEqualTo(25.0);  // (10만 / 40만) * 100 = 25%

        log.info("✅ [테스트 성공] 회수율: {}%, 잔액: {}원",
                result.getRecoveryRate(), result.getBalance());
        log.info("=== [테스트 종료] Dashboard - 회수율 계산 ===\n");
    }

    @Test
    @DisplayName("Dashboard - 준 돈이 0일 때 회수율 0% 반환")
    void testDashboard_ZeroGive() {
        log.info("=== [테스트 시작] Dashboard - 준 돈 0원 케이스 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));
        given(giftLogRepository.getTotalGiveByMemberId(1L))
                .willReturn(0L);
        given(giftLogRepository.getTotalTakeByMemberId(1L))
                .willReturn(500000L);
        given(giftLogRepository.getYearlyGiveByMemberId(eq(1L), anyInt()))
                .willReturn(0L, 0L);

        // when
        DashboardDto result = analysisService.getDashboard(testEmail);

        // then
        assertThat(result.getRecoveryRate()).isEqualTo(0.0);  // 0으로 나누기 방지

        log.info("✅ [테스트 성공] 준 돈 0원일 때 회수율 0% 반환");
        log.info("=== [테스트 종료] Dashboard - 준 돈 0원 케이스 ===\n");
    }

    @Test
    @DisplayName("Dashboard - 회원 없을 때 예외 발생")
    void testDashboard_MemberNotFound() {
        log.info("=== [테스트 시작] Dashboard - 회원 없음 예외 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> analysisService.getDashboard(testEmail))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Member not found");

        log.info("✅ [테스트 성공] 회원 없을 때 예외 발생");
        log.info("=== [테스트 종료] Dashboard - 회원 없음 예외 ===\n");
    }

    // ========================================
    // 2. RecentEvents 테스트
    // ========================================

    @Test
    @DisplayName("RecentEvents - 최근 5개 이벤트만 반환")
    void testRecentEvents_LimitFive() {
        log.info("=== [테스트 시작] RecentEvents - 최근 5개 제한 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));

        List<GiftLog> giftLogs = createMockGiftLogs(7);  // 7개 생성
        given(giftLogRepository.findTop5ByMemberIdOrderByEventDateDesc(1L))
                .willReturn(giftLogs);

        // when
        List<RecentEventDto> result = analysisService.getRecentEvents(testEmail);

        // then
        assertThat(result).hasSize(5);  // 5개만 반환

        log.info("✅ [테스트 성공] 7개 중 최근 5개만 반환");
        log.info("=== [테스트 종료] RecentEvents - 최근 5개 제한 ===\n");
    }

    @Test
    @DisplayName("RecentEvents - 이벤트가 없을 때 빈 리스트 반환")
    void testRecentEvents_EmptyList() {
        log.info("=== [테스트 시작] RecentEvents - 빈 리스트 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));
        given(giftLogRepository.findTop5ByMemberIdOrderByEventDateDesc(1L))
                .willReturn(Collections.emptyList());

        // when
        List<RecentEventDto> result = analysisService.getRecentEvents(testEmail);

        // then
        assertThat(result).isEmpty();

        log.info("✅ [테스트 성공] 이벤트 없을 때 빈 리스트 반환");
        log.info("=== [테스트 종료] RecentEvents - 빈 리스트 ===\n");
    }

    // ========================================
    // 3. Pattern 테스트
    // ========================================

    @Test
    @DisplayName("Pattern - 월별 데이터 1~12월 초기화 확인")
    void testPattern_MonthlyDataInitialization() {
        log.info("=== [테스트 시작] Pattern - 월별 데이터 초기화 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));

        // 3월, 6월, 9월만 데이터 있음
        List<Object[]> monthlyData = Arrays.asList(
                new Object[]{3, 100000L},
                new Object[]{6, 200000L},
                new Object[]{9, 300000L}
        );
        given(giftLogRepository.getMonthlyPattern(1L, 2024))
                .willReturn(monthlyData);
        given(giftLogRepository.getWeekdayPattern(1L, 2024))
                .willReturn(Collections.emptyList());
        given(giftLogRepository.getEventTypeDistribution(1L, 2024))
                .willReturn(Collections.emptyList());

        // when
        PatternDto result = analysisService.getPattern(testEmail, 2024);

        // then
        assertThat(result.getMonthlyData()).hasSize(12);  // 1~12월 모두 존재
        assertThat(result.getMonthlyData().get(3)).isEqualTo(100000L);
        assertThat(result.getMonthlyData().get(6)).isEqualTo(200000L);
        assertThat(result.getMonthlyData().get(1)).isEqualTo(0L);  // 데이터 없는 달은 0
        assertThat(result.getMonthlyData().get(12)).isEqualTo(0L);

        log.info("✅ [테스트 성공] 1~12월 모두 초기화, 데이터 없는 달은 0");
        log.info("=== [테스트 종료] Pattern - 월별 데이터 초기화 ===\n");
    }

    @Test
    @DisplayName("Pattern - 요일 데이터 일~토 매핑 확인")
    void testPattern_WeekdayMapping() {
        log.info("=== [테스트 시작] Pattern - 요일 매핑 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));

        // 1(일요일), 7(토요일)만 데이터 있음
        List<Object[]> weekdayData = Arrays.asList(
                new Object[]{1, 50000L},  // 일요일
                new Object[]{7, 100000L}  // 토요일
        );
        given(giftLogRepository.getMonthlyPattern(1L, 2024))
                .willReturn(Collections.emptyList());
        given(giftLogRepository.getWeekdayPattern(1L, 2024))
                .willReturn(weekdayData);
        given(giftLogRepository.getEventTypeDistribution(1L, 2024))
                .willReturn(Collections.emptyList());

        // when
        PatternDto result = analysisService.getPattern(testEmail, 2024);

        // then
        assertThat(result.getWeekdayData()).hasSize(7);  // 일~토 7개
        assertThat(result.getWeekdayData().get("일요일")).isEqualTo(50000L);
        assertThat(result.getWeekdayData().get("토요일")).isEqualTo(100000L);
        assertThat(result.getWeekdayData().get("월요일")).isEqualTo(0L);

        log.info("✅ [테스트 성공] 요일 매핑 정상: 일~토");
        log.info("=== [테스트 종료] Pattern - 요일 매핑 ===\n");
    }

    @Test
    @DisplayName("Pattern - 이벤트 타입별 분포 확인")
    void testPattern_EventTypeDistribution() {
        log.info("=== [테스트 시작] Pattern - 이벤트 타입 분포 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));

        List<Object[]> eventTypeData = Arrays.asList(
                new Object[]{EventType.WEDDING, 5, 500000L},
                new Object[]{EventType.BIRTHDAY, 10, 300000L}
        );
        given(giftLogRepository.getMonthlyPattern(1L, 2024))
                .willReturn(Collections.emptyList());
        given(giftLogRepository.getWeekdayPattern(1L, 2024))
                .willReturn(Collections.emptyList());
        given(giftLogRepository.getEventTypeDistribution(1L, 2024))
                .willReturn(eventTypeData);

        // when
        PatternDto result = analysisService.getPattern(testEmail, 2024);

        // then
        assertThat(result.getEventTypeData()).hasSize(2);
        assertThat(result.getEventTypeData().get("WEDDING").getCount()).isEqualTo(5);
        assertThat(result.getEventTypeData().get("WEDDING").getAmount()).isEqualTo(500000L);
        assertThat(result.getEventTypeData().get("BIRTHDAY").getCount()).isEqualTo(10);

        log.info("✅ [테스트 성공] 이벤트 타입별 분포 정상");
        log.info("=== [테스트 종료] Pattern - 이벤트 타입 분포 ===\n");
    }

    // ========================================
    // 4. Relation 테스트
    // ========================================

    @Test
    @DisplayName("Relation - TOP 5 지인만 반환")
    void testRelation_TopFiveLimit() {
        log.info("=== [테스트 시작] Relation - TOP 5 제한 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));

        // 10명의 지인 데이터 (TOP 5만 선택되어야 함)
        List<Object[]> topRelations = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            topRelations.add(new Object[]{"지인" + i, Long.valueOf(1000000 - i * 10000), Long.valueOf(i)});
        }
        given(giftLogRepository.getTopRelations(1L))
                .willReturn(topRelations);
        given(giftLogRepository.getGiveByAcquaintance(1L))
                .willReturn(Collections.emptyList());
        given(giftLogRepository.getTakeByAcquaintance(1L))
                .willReturn(Collections.emptyList());

        // when
        RelationDto result = analysisService.getRelation(testEmail);

        // then
        assertThat(result.getTopRelations()).hasSize(5);  // TOP 5만
        assertThat(result.getTopRelations().get(0).getName()).isEqualTo("지인1");

        log.info("✅ [테스트 성공] 10명 중 TOP 5만 반환");
        log.info("=== [테스트 종료] Relation - TOP 5 제한 ===\n");
    }

    @Test
    @DisplayName("Relation - 평균 금액 계산 검증")
    void testRelation_AvgAmountCalculation() {
        log.info("=== [테스트 시작] Relation - 평균 금액 계산 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));

        List<Object[]> topRelations = new ArrayList<>();
        topRelations.add(new Object[]{"지인A", 1000000L, 5L});

        given(giftLogRepository.getTopRelations(1L))
                .willReturn(topRelations);
        given(giftLogRepository.getGiveByAcquaintance(1L))
                .willReturn(Collections.emptyList());
        given(giftLogRepository.getTakeByAcquaintance(1L))
                .willReturn(Collections.emptyList());

        // when
        RelationDto result = analysisService.getRelation(testEmail);

        // then
        RelationDto.TopRelation top = result.getTopRelations().get(0);
        assertThat(top.getTotalAmount()).isEqualTo(1000000L);
        assertThat(top.getEventCount()).isEqualTo(5);
        assertThat(top.getAvgAmount()).isEqualTo(200000L);  // 평균 20만원

        log.info("✅ [테스트 성공] 평균 금액 계산: {}원", top.getAvgAmount());
        log.info("=== [테스트 종료] Relation - 평균 금액 계산 ===\n");
    }

    @Test
    @DisplayName("Relation - 회수율 계산 및 정렬 확인")
    void testRelation_RecoveryRateAndSorting() {
        log.info("=== [테스트 시작] Relation - 회수율 계산 및 정렬 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));

        List<Object[]> giveData = Arrays.asList(
                new Object[]{"지인A", 1000000L},
                new Object[]{"지인B", 500000L},
                new Object[]{"지인C", 300000L}
        );
        List<Object[]> takeData = Arrays.asList(
                new Object[]{"지인A", 800000L},
                new Object[]{"지인B", 500000L}
                // 지인C는 TAKE 없음
        );
        given(giftLogRepository.getTopRelations(1L))
                .willReturn(Collections.emptyList());
        given(giftLogRepository.getGiveByAcquaintance(1L))
                .willReturn(giveData);
        given(giftLogRepository.getTakeByAcquaintance(1L))
                .willReturn(takeData);

        // when
        RelationDto result = analysisService.getRelation(testEmail);

        // then
        List<RelationDto.RelationRecovery> recovery = result.getRelationRecovery();
        assertThat(recovery).hasSize(3);

        // GIVE 금액 내림차순 정렬 확인
        assertThat(recovery.get(0).getName()).isEqualTo("지인A");
        assertThat(recovery.get(1).getName()).isEqualTo("지인B");
        assertThat(recovery.get(2).getName()).isEqualTo("지인C");

        // 회수율 계산 확인
        assertThat(recovery.get(0).getRate()).isEqualTo(80.0);  // 80만 / 100만 = 80%
        assertThat(recovery.get(1).getRate()).isEqualTo(100.0);  // 50만 / 50만 = 100%
        assertThat(recovery.get(2).getRate()).isEqualTo(0.0);  // TAKE 없음 = 0%

        log.info("✅ [테스트 성공] 회수율 계산 및 GIVE 금액순 정렬");
        log.info("=== [테스트 종료] Relation - 회수율 계산 및 정렬 ===\n");
    }

    // ========================================
    // 5. Recovery 테스트
    // ========================================

    @Test
    @DisplayName("Recovery - 180일 이상 장기 미회수 경고")
    void testRecovery_LongTermWarning() {
        log.info("=== [테스트 시작] Recovery - 장기 미회수 경고 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));
        given(giftLogRepository.getTotalGiveByMemberId(1L))
                .willReturn(1000000L);
        given(giftLogRepository.getTotalTakeByMemberId(1L))
                .willReturn(500000L);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime longAgo = now.minusDays(200);  // 200일 전
        LocalDateTime recent = now.minusDays(100);   // 100일 전

        List<Object[]> unrecoveredData = Arrays.asList(
                new Object[]{"지인A", 300000L, 0L, longAgo},  // 200일 전 → 경고!
                new Object[]{"지인B", 200000L, 0L, recent}   // 100일 전 → 경고 아님
        );
        given(giftLogRepository.getUnrecoveredRelations(1L))
                .willReturn(unrecoveredData);

        // when
        RecoveryDto result = analysisService.getRecovery(testEmail);

        // then
        assertThat(result.getUnrecoveredList()).hasSize(2);
        assertThat(result.getLongTermWarning()).hasSize(1);  // 180일 이상 1명만
        assertThat(result.getLongTermWarning().get(0).getName()).isEqualTo("지인A");
        assertThat(result.getLongTermWarning().get(0).getDays()).isGreaterThan(180L);

        log.info("✅ [테스트 성공] 180일 이상 장기 미회수 1명 감지");
        log.info("=== [테스트 종료] Recovery - 장기 미회수 경고 ===\n");
    }

    @Test
    @DisplayName("Recovery - 미회수 금액 계산")
    void testRecovery_UnrecoveredAmount() {
        log.info("=== [테스트 시작] Recovery - 미회수 금액 계산 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));
        given(giftLogRepository.getTotalGiveByMemberId(1L))
                .willReturn(1000000L);
        given(giftLogRepository.getTotalTakeByMemberId(1L))
                .willReturn(300000L);

        LocalDateTime now = LocalDateTime.now();
        List<Object[]> unrecoveredData = Arrays.asList(
                new Object[]{"지인A", 500000L, 200000L, now.minusDays(30)},  // 미회수: 30만
                new Object[]{"지인B", 500000L, 100000L, now.minusDays(60)}   // 미회수: 40만
        );
        given(giftLogRepository.getUnrecoveredRelations(1L))
                .willReturn(unrecoveredData);

        // when
        RecoveryDto result = analysisService.getRecovery(testEmail);

        // then
        assertThat(result.getUnrecovered()).isEqualTo(700000L);  // 100만 - 30만 = 70만
        assertThat(result.getUnrecoveredList().get(0).getAmount()).isEqualTo(400000L);  // 지인B (60일 전)
        assertThat(result.getUnrecoveredList().get(1).getAmount()).isEqualTo(300000L);  // 지인A (30일 전)
        log.info("✅ [테스트 성공] 미회수 금액: {}원", result.getUnrecovered());
        log.info("=== [테스트 종료] Recovery - 미회수 금액 계산 ===\n");
    }

    @Test
    @DisplayName("Recovery - 전체 회수율 계산")
    void testRecovery_TotalRecoveryRate() {
        log.info("=== [테스트 시작] Recovery - 전체 회수율 ===");

        // given
        given(memberRepository.findByEmail(testEmail))
                .willReturn(Optional.of(testMember));
        given(giftLogRepository.getTotalGiveByMemberId(1L))
                .willReturn(1000000L);  // 준 돈: 100만
        given(giftLogRepository.getTotalTakeByMemberId(1L))
                .willReturn(850000L);   // 받은 돈: 85만
        given(giftLogRepository.getUnrecoveredRelations(1L))
                .willReturn(Collections.emptyList());

        // when
        RecoveryDto result = analysisService.getRecovery(testEmail);

        // then
        assertThat(result.getTotalRecovery()).isEqualTo(85.0);  // (85만 / 100만) * 100 = 85%

        log.info("✅ [테스트 성공] 전체 회수율: {}%", result.getTotalRecovery());
        log.info("=== [테스트 종료] Recovery - 전체 회수율 ===\n");
    }

    // ========================================
    // Helper Methods
    // ========================================

    private List<GiftLog> createMockGiftLogs(int count) {
        List<GiftLog> giftLogs = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            // Acquaintance 생성
            Acquaintance acquaintance = Acquaintance.builder()
                    .acquaintanceId((long) i)
                    .member(testMember)
                    .name("지인" + i)
                    .relation(Relation.FRIEND)
                    .phone("010-1234-567" + i)
                    .build();

            // Event 생성
            Event event = Event.builder()
                    .eventId((long) i)
                    .member(testMember)
                    .eventType(EventType.WEDDING)
                    .eventName("이벤트" + i)
                    .eventDate(LocalDateTime.now().minusDays(i))
                    .location("서울")
                    .isOwner(true)
                    .build();

            // EventAcquaintance 생성
            EventAcquaintance eventAcquaintance = EventAcquaintance.builder()
                    .eventAcquaintanceId((long) i)
                    .event(event)
                    .acquaintance(acquaintance)
                    .build();

            // GiftLog 생성
            GiftLog giftLog = GiftLog.builder()
                    .giftId((long) i)
                    .eventAcquaintance(eventAcquaintance)
                    .actionType(ActionType.GIVE)
                    .amount(100000L)
                    .payMethod(PayMethod.CASH)
                    .memo("테스트 메모 " + i)
                    .build();

            giftLogs.add(giftLog);
        }
        return giftLogs;
    }
}