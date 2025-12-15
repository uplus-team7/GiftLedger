package springboot.giftledger.analysis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springboot.giftledger.analysis.dto.*;
import springboot.giftledger.entity.GiftLog;
import springboot.giftledger.entity.Member;
import springboot.giftledger.repository.GiftLogRepository;
import springboot.giftledger.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final GiftLogRepository giftLogRepository;
    private final MemberRepository memberRepository;

    @Override
    public DashboardDto getDashboard(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Long memberId = member.getMemberId();


        Long totalGive = giftLogRepository.getTotalGiveByMemberId(memberId);


        Long totalTake = giftLogRepository.getTotalTakeByMemberId(memberId);


        Long balance = totalTake - totalGive;


        Double recoveryRate = totalGive > 0
                ? Math.round((totalTake.doubleValue() / totalGive.doubleValue()) * 1000.0) / 10.0
                : 0.0;


        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;

        Long currentYearGive = giftLogRepository.getYearlyGiveByMemberId(memberId, currentYear);
        Long lastYearGive = giftLogRepository.getYearlyGiveByMemberId(memberId, lastYear);

        Long yearChangeAmount = currentYearGive - lastYearGive;
        Double yearChangePercent = lastYearGive > 0
                ? Math.round((yearChangeAmount.doubleValue() / lastYearGive.doubleValue()) * 1000.0) / 10.0
                : 0.0;

        return DashboardDto.builder()
                .totalGive(totalGive)
                .totalTake(totalTake)
                .balance(balance)
                .recoveryRate(recoveryRate)
                .yearChangePercent(yearChangePercent)
                .yearChangeAmount(yearChangeAmount)
                .build();
    }

    @Override
    public List<RecentEventDto> getRecentEvents(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        List<GiftLog> giftLogs = giftLogRepository
                .findTop5ByMemberIdOrderByEventDateDesc(member.getMemberId());

        return giftLogs.stream()
                .limit(5)
                .map(RecentEventDto::from)
                .collect(Collectors.toList());
    }



    @Override
    public PatternDto getPattern(String email, int year) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Long memberId = member.getMemberId();

        Map<Integer, Long> monthlyData = getMonthlyData(memberId, year);

        Map<String, Long> weekdayData = getWeekdayData(memberId, year);

        Map<String, PatternDto.EventTypeData> eventTypeData = getEventTypeData(memberId, year);

        return PatternDto.builder()
                .monthlyData(monthlyData)
                .weekdayData(weekdayData)
                .eventTypeData(eventTypeData)
                .build();
    }

    private Map<Integer, Long> getMonthlyData(Long memberId, int year) {
        List<Object[]> monthlyResults = giftLogRepository.getMonthlyPattern(memberId, year);
        Map<Integer, Long> monthlyData = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            monthlyData.put(i, 0L);
        }

        for (Object[] row : monthlyResults) {
            Integer month = (Integer) row[0];
            Long amount = ((Number) row[1]).longValue();
            monthlyData.put(month, amount);
        }

        return monthlyData;
    }

    private Map<String, Long> getWeekdayData(Long memberId, int year) {
        List<Object[]> weekdayResults = giftLogRepository.getWeekdayPattern(memberId, year);
        Map<String, Long> weekdayData = new LinkedHashMap<>();

        String[] days = {"일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"};
        for (String day : days) {
            weekdayData.put(day, 0L);
        }

        for (Object[] row : weekdayResults) {
            Integer dayOfWeek = (Integer) row[0];
            Long avgAmount = ((Number) row[1]).longValue();
            weekdayData.put(days[dayOfWeek - 1], avgAmount);
        }

        return weekdayData;
    }

    private Map<String, PatternDto.EventTypeData> getEventTypeData(Long memberId, int year) {
        List<Object[]> eventTypeResults = giftLogRepository.getEventTypeDistribution(memberId, year);
        Map<String, PatternDto.EventTypeData> eventTypeData = new HashMap<>();

        for (Object[] row : eventTypeResults) {
            String eventType = ((Enum<?>) row[0]).name();
            Integer count = ((Number) row[1]).intValue();
            Long amount = ((Number) row[2]).longValue();

            eventTypeData.put(eventType, PatternDto.EventTypeData.builder()
                    .count(count)
                    .amount(amount)
                    .build());
        }

        return eventTypeData;
    }





    @Override
    public RelationDto getRelation(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Long memberId = member.getMemberId();

        // 1. TOP 5 지인
        List<RelationDto.TopRelation> topRelations = getTopRelations(memberId);

        // 2. 지인별 회수율
        List<RelationDto.RelationRecovery> relationRecovery = getRelationRecovery(memberId);

        return RelationDto.builder()
                .topRelations(topRelations)
                .relationRecovery(relationRecovery)
                .build();
    }

    // TOP 5 지인 처리
    private List<RelationDto.TopRelation> getTopRelations(Long memberId) {
        List<Object[]> results = giftLogRepository.getTopRelations(memberId);

        return results.stream()
                .limit(5)  // TOP 5만
                .map(row -> {
                    String name = (String) row[0];
                    Long totalAmount = ((Number) row[1]).longValue();
                    Integer eventCount = ((Number) row[2]).intValue();
                    Long avgAmount = totalAmount / eventCount;

                    return RelationDto.TopRelation.builder()
                            .name(name)
                            .totalAmount(totalAmount)
                            .eventCount(eventCount)
                            .avgAmount(avgAmount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 지인별 회수율 처리
    private List<RelationDto.RelationRecovery> getRelationRecovery(Long memberId) {
        // GIVE 데이터
        List<Object[]> giveResults = giftLogRepository.getGiveByAcquaintance(memberId);
        Map<String, Long> giveMap = new HashMap<>();
        for (Object[] row : giveResults) {
            String name = (String) row[0];
            Long amount = ((Number) row[1]).longValue();
            giveMap.put(name, amount);
        }

        // TAKE 데이터
        List<Object[]> takeResults = giftLogRepository.getTakeByAcquaintance(memberId);
        Map<String, Long> takeMap = new HashMap<>();
        for (Object[] row : takeResults) {
            String name = (String) row[0];
            Long amount = ((Number) row[1]).longValue();
            takeMap.put(name, amount);
        }

        // 모든 지인 이름 수집 (GIVE만 있거나, TAKE만 있을 수도 있음)
        Set<String> allNames = new HashSet<>();
        allNames.addAll(giveMap.keySet());
        allNames.addAll(takeMap.keySet());

        // 회수율 계산
        return allNames.stream()
                .map(name -> {
                    Long give = giveMap.getOrDefault(name, 0L);
                    Long take = takeMap.getOrDefault(name, 0L);
                    Double rate = give > 0
                            ? Math.round((take.doubleValue() / give.doubleValue()) * 1000.0) / 10.0
                            : 0.0;

                    return RelationDto.RelationRecovery.builder()
                            .name(name)
                            .give(give)
                            .take(take)
                            .rate(rate)
                            .build();
                })
                .sorted((a, b) -> Long.compare(b.getGive(), a.getGive()))  // GIVE 금액 내림차순
                .collect(Collectors.toList());
    }





    @Override
    public RecoveryDto getRecovery(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        Long memberId = member.getMemberId();

        // 전체 회수율 계산
        Long totalGive = giftLogRepository.getTotalGiveByMemberId(memberId);
        Long totalTake = giftLogRepository.getTotalTakeByMemberId(memberId);
        Double totalRecoveryRate = totalGive > 0
                ? Math.round((totalTake.doubleValue() / totalGive.doubleValue()) * 1000.0) / 10.0
                : 0.0;

        // 미회수 금액
        Long balance = totalGive - totalTake;

        // 미회수 지인 리스트
        List<Object[]> unrecoveredResults = giftLogRepository.getUnrecoveredRelations(memberId);
        List<RecoveryDto.UnrecoveredRelation> unrecoveredList = new ArrayList<>();
        List<RecoveryDto.UnrecoveredRelation> longTermWarning = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Object[] row : unrecoveredResults) {
            String name = (String) row[0];
            Long giveAmount = ((Number) row[1]).longValue();
            Long takeAmount = ((Number) row[2]).longValue();
            LocalDateTime lastGiveDate = (LocalDateTime) row[3];

            Long unrecoveredRelationAmount = giveAmount - takeAmount;

            // 경과 일수 계산
            Long days = lastGiveDate != null
                    ? ChronoUnit.DAYS.between(lastGiveDate, now)
                    : 0L;

            String dateStr = lastGiveDate != null
                    ? lastGiveDate.toLocalDate().toString()
                    : "";

            RecoveryDto.UnrecoveredRelation relation = RecoveryDto.UnrecoveredRelation.builder()
                    .name(name)
                    .amount(unrecoveredRelationAmount)
                    .date(dateStr)
                    .days(days)
                    .build();

            unrecoveredList.add(relation);

            // 180일 이상이면 장기 미회수
            if (days >= 180) {
                longTermWarning.add(relation);
            }
        }

        // 날짜순 정렬 (오래된 순)
        unrecoveredList.sort((a, b) -> Long.compare(b.getDays(), a.getDays()));
        longTermWarning.sort((a, b) -> Long.compare(b.getDays(), a.getDays()));

        return RecoveryDto.builder()
                .totalRecovery(totalRecoveryRate)
                .unrecovered(balance)
                .unrecoveredList(unrecoveredList)
                .longTermWarning(longTermWarning)
                .build();
    }

}
