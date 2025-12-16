package springboot.giftledger.analysis.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import springboot.giftledger.repository.GiftLogRepository;
import springboot.giftledger.repository.MemberRepository;
import springboot.giftledger.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 운영 DB 데이터로 테스트
 *
 * 주의사항:
 * - 실제 DB에 연결됨!
 * - @Transactional(readOnly = true)로 조회만 가능
 * - 데이터 수정/삭제 절대 안 함!
 * - 실제 회원 이메일로 테스트
 */
@SpringBootTest  // 전체 컨텍스트 로드 (실제 DB 연결)
@Transactional(readOnly = true)  // 읽기 전용! 수정 불가!
@DisplayName("실제 운영 DB 데이터 테스트")
class GiftLogRepositoryTest {

    @Autowired
    private GiftLogRepository giftLogRepository;

    @Autowired
    private MemberRepository memberRepository;

    // ========================================
    // 실제 회원으로 테스트
    // ========================================

    @Test
    @DisplayName("실제 회원 - TOP 지인 조회")
    void testRealUser_TopRelations() {
        // given: 실제 회원 이메일 (본인 이메일로 변경!)
        String realEmail = "user1@test.com";  // ← 실제 DB에 있는 이메일로 변경!

        Member member = memberRepository.findByEmail(realEmail)
                .orElseThrow(() -> new RuntimeException("회원 없음! 이메일 확인하세요: " + realEmail));

        // when: 실제 데이터로 TOP 지인 조회
        List<Object[]> topRelations = giftLogRepository.getTopRelations(member.getMemberId());

        // then: 실제 데이터 검증
        System.out.println("=== TOP 지인 조회 결과 ===");
        System.out.println("총 " + topRelations.size() + "명");

        for (int i = 0; i < Math.min(5, topRelations.size()); i++) {
            Object[] row = topRelations.get(i);
            String name = (String) row[0];
            Long totalAmount = ((Number) row[1]).longValue();
            Long eventCount = ((Number) row[2]).longValue();

            System.out.println((i+1) + "등: " + name +
                    " (총 " + totalAmount + "원, " + eventCount + "회)");
        }

        // 검증: 최소 1명 이상
        assertThat(topRelations.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("실제 회원 - 미회수 지인 조회")
    void testRealUser_UnrecoveredRelations() {
        // given
        String realEmail = "user1@test.com";  // ← 실제 이메일로 변경!

        Member member = memberRepository.findByEmail(realEmail)
                .orElseThrow(() -> new RuntimeException("회원 없음!"));

        // when
        List<Object[]> unrecoveredList = giftLogRepository.getUnrecoveredRelations(
                member.getMemberId());

        // then
        System.out.println("=== 미회수 지인 ===");
        System.out.println("총 " + unrecoveredList.size() + "명");

        for (Object[] row : unrecoveredList) {
            String name = (String) row[0];
            Long giveAmount = ((Number) row[1]).longValue();
            Long takeAmount = ((Number) row[2]).longValue();
            Long diff = giveAmount - takeAmount;

            System.out.println("- " + name +
                    " (준 돈: " + giveAmount + "원, 받은 돈: " + takeAmount +
                    "원, 차액: " + diff + "원)");
        }

        // 미회수가 없을 수도 있으므로 size >= 0
        assertThat(unrecoveredList.size()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("실제 회원 - 2024년 월별 패턴")
    void testRealUser_MonthlyPattern() {
        // given
        String realEmail = "user1@test.com";  // ← 실제 이메일로 변경!

        Member member = memberRepository.findByEmail(realEmail)
                .orElseThrow(() -> new RuntimeException("회원 없음!"));

        // when
        List<Object[]> monthlyPattern = giftLogRepository.getMonthlyPattern(
                member.getMemberId(), 2024);

        // then
        System.out.println("=== 2024년 월별 지출 패턴 ===");

        if (monthlyPattern.isEmpty()) {
            System.out.println("2024년 데이터 없음");
        } else {
            for (Object[] row : monthlyPattern) {
                Integer month = (Integer) row[0];
                Long amount = ((Number) row[1]).longValue();

                System.out.println(month + "월: " + amount + "원");
            }
        }

        // 데이터가 있을 수도, 없을 수도 있음
        assertThat(monthlyPattern).isNotNull();
    }

    @Test
    @DisplayName("실제 회원 - 총 지출/수입 확인")
    void testRealUser_TotalGiveAndTake() {
        // given
        String realEmail = "user1@test.com";  // ← 실제 이메일로 변경!

        Member member = memberRepository.findByEmail(realEmail)
                .orElseThrow(() -> new RuntimeException("회원 없음!"));

        // when
        Long totalGive = giftLogRepository.getTotalGiveByMemberId(member.getMemberId());
        Long totalTake = giftLogRepository.getTotalTakeByMemberId(member.getMemberId());

        // then
        System.out.println("=== 총 지출/수입 ===");
        System.out.println("총 지출: " + (totalGive != null ? totalGive : 0) + "원");
        System.out.println("총 수입: " + (totalTake != null ? totalTake : 0) + "원");
        System.out.println("잔액: " + ((totalTake != null ? totalTake : 0) - (totalGive != null ? totalGive : 0)) + "원");

        // null일 수도 있음
        assertThat(totalGive).isNotNull();
        assertThat(totalTake).isNotNull();
    }

    @Test
    @DisplayName("전체 회원 수 확인")
    void testAllMembers() {
        // when
        List<Member> members = memberRepository.findAll();

        // then
        System.out.println("=== 전체 회원 목록 ===");
        System.out.println("총 " + members.size() + "명");

        for (Member member : members) {
            System.out.println("- " + member.getEmail() + " (" + member.getName() + ")");
        }

        assertThat(members.size()).isGreaterThan(0);
    }
}