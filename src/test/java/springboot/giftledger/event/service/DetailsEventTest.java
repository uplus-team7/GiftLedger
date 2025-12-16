package springboot.giftledger.event.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.entity.*;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;
import springboot.giftledger.enums.PayMethod;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.event.dto.*;
import springboot.giftledger.repository.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class DetailsEventTest {
    @Autowired private EntityManager em;

    @Autowired
    private EventService eventService;

    @Autowired private EventRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired private AcquaintanceRepository acquaintanceRepository;
    @Autowired private GiftLogRepository giftLogRepository;
    @Autowired private EventAcquaintanceRepository eventAcquaintanceRepository;

    @Test
    @DisplayName("[서비스 레이어 통합 테스트]상세 페이지 조회 성공 테스트")
    void detailsEvent_success(){

        Member member = memberRepository.save(Member.builder()
                .email("test@test.com")
                .password("password")
                .name("테스트")
                .ages("20")
                .build());

        Event event = eventRepository.save(Event.builder()
                .member(member)
                .eventName("테스트 자녀 돌잔치")
                .eventType(EventType.BIRTHDAY) // "생일" Enum
                .eventDate(LocalDateTime.of(2025, 5, 5, 12, 0))
                .location("워커힐 호텔")
                .isOwner(true)
                .build());

        Acquaintance friend1 = acquaintanceRepository.save(Acquaintance.builder()
                .member(member)
                .name("테스트 친구")
                .relation(Relation.FRIEND) // "친구" Enum
                .groupName("대학교")
                .phone("010-1188-3344")
                .build());

        Acquaintance friend2 = acquaintanceRepository.save(Acquaintance.builder()
                .member(member)
                .name("테스트 친구2")
                .relation(Relation.FRIEND)
                .groupName("대학교")
                .phone("010-3692-5814")
                .build());

        EventAcquaintance ea1 = eventAcquaintanceRepository.save(EventAcquaintance.builder()
                .event(event)
                .acquaintance(friend1)
                .build());

        EventAcquaintance ea2 = eventAcquaintanceRepository.save(EventAcquaintance.builder()
                .event(event)
                .acquaintance(friend2)
                .build());

        giftLogRepository.save(GiftLog.builder()
                .eventAcquaintance(ea1)
                .amount(100000L)
                .actionType(ActionType.TAKE) // 받음
                .payMethod(PayMethod.CASH)   // 현금
                .memo("돌반지 받음")
                .build());

        giftLogRepository.save(GiftLog.builder()
                .eventAcquaintance(ea2) // 친구2 연결 정보
                .amount(50000L)
                .actionType(ActionType.TAKE)
                .payMethod(PayMethod.CASH)
                .memo("봉투 받음")
                .build());

        em.flush();
        em.clear();

        Long eventId = event.getEventId();
        EventDetailsResultDto result = eventService.detailsEvent("test@test.com", eventId);

        assertThat(result).isNotNull();
        assertThat(result.getResult()).isEqualTo("success");

        assertThat(result.getEventDto().getEventName()).isEqualTo("테스트 자녀 돌잔치");
        assertThat(result.getEventDto().getLocation()).isEqualTo("워커힐 호텔");

        List<GuestLogDto> guests = result.getGuestLogDtos();
        assertThat(guests).hasSize(2);
    }

    @Test
    @DisplayName("[서비스 레이어 통합 테스트] 상세 페이지에서 추가 등록 - 새로운 지인")
    void insertEventOnDetails_success(){
        String email = "owner@test.com";
        Member member = memberRepository.save(setMember(email));
        Event event = eventRepository.save(setEvent(member));

        EventRequestDto eventRequestDto = setRequestData();

        Long eventId = event.getEventId();
        EventDetailsResultDto result = eventService.insertEventOnDetails(email, eventId, eventRequestDto);

        em.flush();
        em.clear();

        assertThat(result.getResult().equals("success"));

        // 이벤트에 연결된 데이터 조회
        Event newEvent = eventRepository.findById(eventId).get();

        // 지인 추가 확인
        List<EventAcquaintance> relations = newEvent.getEventAcquaintances();
        assertThat(relations).hasSize(1);

        EventAcquaintance relation = relations.get(0);
        assertThat(relation.getAcquaintance().getName()).isEqualTo("테스트 친구");

        // 기프트 추가 확인
        List<GiftLog> giftLogs = relation.getGiftLogs();
        assertThat(giftLogs).hasSize(1);
        assertThat(giftLogs.get(0).getActionType()).isEqualTo(ActionType.GIVE);
        assertThat(giftLogs.get(0).getAmount()).isEqualTo(100000L);
    }

    @Test
    @DisplayName("[서비스 레이어 통합 테스트] 상세 페이지에서 추가 등록 - 기존 지인")
    void insertEventOnDetails_existing_success(){
        String email = "owner@test.com";
        Member member = memberRepository.save(setMember(email));
        Event event = eventRepository.save(setEvent(member));
        Acquaintance acquaintance = acquaintanceRepository.save(setAcquaintance(member));
        long oldAcqCnt = acquaintanceRepository.count();

        Long eventId = event.getEventId();
        EventRequestDto eventRequestDto = setRequestData();

        EventDetailsResultDto result = eventService.insertEventOnDetails(email, event.getEventId(), eventRequestDto);

        em.flush();
        em.clear();

        assertThat(result.getResult().equals("success"));

        long newAcqCnt = acquaintanceRepository.count();
        assertThat(newAcqCnt).isEqualTo(oldAcqCnt); // 지인 증가 안함: 기존 지인 사용

        // 이벤트와 연결된 지인이 동일 지인인지 확인
        Event newEvent = eventRepository.findById(eventId).get();
        Long connectedId = newEvent.getEventAcquaintances().get(0).getAcquaintance().getAcquaintanceId();

        assertThat(connectedId).isEqualTo(acquaintance.getAcquaintanceId());

    }

    @Test
    @DisplayName("[서비스 레이어 통합 테스트] 상세 페이지에서 추가 등록 - 다른 유저의 이벤트에 등록 시도")
    void insertEventOnDetails_security_fail() {
        // 이벤트 주인
        Member owner = memberRepository.save(setMember("owner@test.com"));
        Event event = eventRepository.save(setEvent(owner));

        // 다른 유저 등록 시도
        String hackerEmail = "hacker@test.com";
        EventRequestDto eventRequestDto = setRequestData();

        assertThrows(SecurityException.class, () -> {
            eventService.insertEventOnDetails(hackerEmail, event.getEventId(), eventRequestDto);
        });

    }



    // test data
    Member setMember(String email) {
        return Member.builder()
                .email(email)
                .name("테스트")
                .ages("30")
                .password("pass")
                .build();
    }

    Event setEvent(Member member) {
        return Event.builder()
                .member(member)
                .eventName("나의 결혼식")
                .eventType(EventType.WEDDING)
                .eventDate(LocalDateTime.now())
                .isOwner(true)
                .build();
    }

    Acquaintance setAcquaintance(Member member) {
        return Acquaintance.builder()
                .member(member)
                .name("테스트 친구")
                .phone("010-1001-4001")
                .relation(Relation.FRIEND)
                .groupName("고등학교 동창")
                .build();
    }

    EventRequestDto setRequestData(){
        AcquaintanceDto acquaintance = AcquaintanceDto.builder()
                .name("테스트 친구")
                .phone("010-1001-4001")
                .relation("친구")
                .groupName("고등학교 동창")
                .build();

        GiftLogDto giftLog = GiftLogDto.builder()
                .amount(100000L)
                .payMethod("현금")
                .actionType("보냄")
                .memo("식권 받음, 뷔페 맛있었음")
                .build();

        return EventRequestDto.builder()
                .acquaintanceDto(acquaintance)
                .giftLogDto(giftLog)
                .build();
    }
}
