package springboot.giftledger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import springboot.giftledger.entity.EventAcquaintance;
import springboot.giftledger.entity.GiftLog;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;

@Repository
public interface GiftLogRepository extends JpaRepository<GiftLog, Long> {
	
	
	
    @Query("SELECT COALESCE(SUM(g.amount), 0) " +
            "FROM GiftLog g " +
            "WHERE g.eventAcquaintance.event.eventId = :eventId")
    Long sumAmountByEventId(@Param("eventId") Long eventId);
    
    
    @Query("""
    	    select g
    	    from GiftLog g
    	    join g.eventAcquaintance ea
    	    where ea.event.eventId = :eventId
    	    order by g.giftId asc
    	""")
    	Optional<GiftLog> findFirstByEventId(@Param("eventId") Long eventId);

    @Query("SELECT AVG(g.amount) " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "JOIN e.member m " +
            "WHERE m.ages = :ages " +
            "AND e.eventType = :eventType " +
            "AND g.actionType = :actionType")
    Double findAverageAmountByAgesAndEventTypeAndActionType(
            @Param("ages") String ages,
            @Param("eventType") EventType eventType,
            @Param("actionType") ActionType actionType);


    // 연령대, 이벤트 타입, 액션 타입별 총 건수
    @Query("SELECT COUNT(g) " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "JOIN e.member m " +
            "WHERE m.ages = :ages " +
            "AND e.eventType = :eventType " +
            "AND g.actionType = :actionType")
    Long countByAgesAndEventTypeAndActionType(
            @Param("ages") String ages,
            @Param("eventType") EventType eventType,
            @Param("actionType") ActionType actionType);

    // ===== 1번 대시보드용 쿼리 =====

    // 사용자의 총 지출 금액 (GIVE)
    @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId AND g.actionType = 'GIVE'")
    Long getTotalGiveByMemberId(@Param("memberId") Long memberId);

    // 사용자의 총 수입 금액 (TAKE)
    @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId AND g.actionType = 'TAKE'")
    Long getTotalTakeByMemberId(@Param("memberId") Long memberId);

    // 특정 년도의 지출 금액
    @Query("SELECT COALESCE(SUM(g.amount), 0) FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "AND g.actionType = 'GIVE' " +
            "AND YEAR(e.eventDate) = :year")
    Long getYearlyGiveByMemberId(@Param("memberId") Long memberId, @Param("year") int year);

    // 최근 이벤트 5개
    @Query("SELECT g FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "ORDER BY e.eventDate DESC")
    List<GiftLog> findTop5ByMemberIdOrderByEventDateDesc(@Param("memberId") Long memberId);


    // ===== 2번 지출 패턴 분석용 쿼리 =====

    // 특정 연도의 월별 지출 (1~12월)
    @Query("SELECT MONTH(e.eventDate) as month, SUM(g.amount) as amount " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "AND g.actionType = 'GIVE' " +
            "AND YEAR(e.eventDate) = :year " +
            "GROUP BY MONTH(e.eventDate)")
    List<Object[]> getMonthlyPattern(@Param("memberId") Long memberId,
                                     @Param("year") int year);

    // 특정 연도의 요일별 평균 지출 (일~토)
    @Query("SELECT DAYOFWEEK(e.eventDate) as dayOfWeek, AVG(g.amount) as avgAmount " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "AND g.actionType = 'GIVE' " +
            "AND YEAR(e.eventDate) = :year " +
            "GROUP BY DAYOFWEEK(e.eventDate)")
    List<Object[]> getWeekdayPattern(@Param("memberId") Long memberId,
                                     @Param("year") int year);

    // 특정 연도의 이벤트 타입별 분포 (결혼식, 장례식, 생일, 기타)
    @Query("SELECT e.eventType, COUNT(g), SUM(g.amount) " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "AND g.actionType = 'GIVE' " +
            "AND YEAR(e.eventDate) = :year " +
            "GROUP BY e.eventType")
    List<Object[]> getEventTypeDistribution(@Param("memberId") Long memberId,
                                            @Param("year") int year);


    // ===== 3번 지인 분석용 쿼리 =====

    // TOP 5 지인 (총 지출액 기준)
    @Query("SELECT a.name, SUM(g.amount) as totalAmount, COUNT(g) as eventCount " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.acquaintance a " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "AND g.actionType = 'GIVE' " +
            "GROUP BY a.acquaintanceId, a.name " +
            "ORDER BY totalAmount DESC")
    List<Object[]> getTopRelations(@Param("memberId") Long memberId);

    // 지인별 GIVE 총액
    @Query("SELECT a.name, SUM(g.amount) " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.acquaintance a " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "AND g.actionType = 'GIVE' " +
            "GROUP BY a.acquaintanceId, a.name")
    List<Object[]> getGiveByAcquaintance(@Param("memberId") Long memberId);

    // 지인별 TAKE 총액
    @Query("SELECT a.name, SUM(g.amount) " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.acquaintance a " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "AND g.actionType = 'TAKE' " +
            "GROUP BY a.acquaintanceId, a.name")
    List<Object[]> getTakeByAcquaintance(@Param("memberId") Long memberId);

    // ===== 4번 회수율 대시보드용 쿼리 =====

    // 지인별 마지막 GIVE 날짜 및 금액 (미회수 계산용)
    @Query("SELECT a.name, " +
            "SUM(CASE WHEN g.actionType = 'GIVE' THEN g.amount ELSE 0 END) as giveAmount, " +
            "SUM(CASE WHEN g.actionType = 'TAKE' THEN g.amount ELSE 0 END) as takeAmount, " +
            "MAX(CASE WHEN g.actionType = 'GIVE' THEN e.eventDate ELSE NULL END) as lastGiveDate " +
            "FROM GiftLog g " +
            "JOIN g.eventAcquaintance ea " +
            "JOIN ea.acquaintance a " +
            "JOIN ea.event e " +
            "WHERE e.member.memberId = :memberId " +
            "GROUP BY a.acquaintanceId, a.name " +
            "HAVING SUM(CASE WHEN g.actionType = 'GIVE' THEN g.amount ELSE 0 END) > " +
            "SUM(CASE WHEN g.actionType = 'TAKE' THEN g.amount ELSE 0 END)")
    List<Object[]> getUnrecoveredRelations(@Param("memberId") Long memberId);

    void deleteByGiftId(Long giftId);

    List<GiftLog> findAllByEventAcquaintance(EventAcquaintance eventAcquaintance);
}
