package springboot.giftledger.event.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.entity.*;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.event.dto.EventDto;
import springboot.giftledger.event.dto.EventRequestDto;
import springboot.giftledger.event.dto.EventResultDto;
import springboot.giftledger.event.dto.GiftLogDto;
import springboot.giftledger.repository.AcquaintanceRepository;
import springboot.giftledger.repository.EventRepository;
import springboot.giftledger.repository.GiftLogRepository;
import springboot.giftledger.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class InsertEventTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private EventService eventService;

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AcquaintanceRepository acquaintanceRepository;
    @Autowired
    private GiftLogRepository giftLogRepository;

    EventRequestDto setRequestData() {
        AcquaintanceDto acquaintance = AcquaintanceDto.builder()
                .name("테스트 친구")
                .phone("010-1010-0201")
                .relation("친구")
                .groupName("고등학교 동창")
                .build();

        EventDto eventDetail = EventDto.builder()
                .eventName("테스트 결혼식")
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

    Member setMember(String email) {
        return memberRepository.save(Member.builder()
                .email(email)
                .password("1234")
                .ages("30")
                .name("테스트")
                .build());
    }

    Acquaintance setAcquaintance(Member member) {
        return Acquaintance.builder()
                .member(member)
                .name("테스트 친구")
                .phone("010-1010-0201")
                .relation(Relation.FRIEND)
                .groupName("고등학교 동창")
                .build();
    }


    @Test
    @DisplayName("이벤트 및 관련 정보 등록 테스트 - 새로운 지인")
    void insertEvent_success() {
        // [Given]
        String email = "user@test.com";
        Member member = memberRepository.save(setMember(email));

        EventRequestDto requestDto = setRequestData();

        EventResultDto result = eventService.insertEvent(email, requestDto);

        em.flush();
        em.clear();

        assertThat(result.getResult()).isEqualTo("success");

        Long newEventId = result.getEventDto().getEventId();
        Event savedEvent = eventRepository.findById(newEventId).orElseThrow();
        assertThat(savedEvent.getEventName()).isEqualTo("테스트 결혼식");

        Long newAcqId = result.getAcquaintanceDto().getAcquaintanceId();
        Acquaintance savedAcq = acquaintanceRepository.findById(newAcqId).orElseThrow();
        assertThat(savedAcq.getPhone()).isEqualTo("010-1010-0201");

        List<EventAcquaintance> relations = savedEvent.getEventAcquaintances();
        assertThat(relations).hasSize(1);
        EventAcquaintance relation = relations.get(0);
        assertThat(relation.getAcquaintance().getName()).isEqualTo("테스트 친구");

        List<GiftLog> giftLogs = relation.getGiftLogs();
        assertThat(giftLogs).hasSize(1);
        assertThat(giftLogs.get(0).getAmount()).isEqualTo(100000L);

    }

    @Test
    @DisplayName("이벤트 및 관련 정보 등록 테스트 - 기존 지인")
    void insertEvent_existing_success() {
        String email = "user@test.com";
        Member member = memberRepository.save(setMember(email));
        Acquaintance acquaintance = acquaintanceRepository.save(setAcquaintance(member));
        Long oldAcqId = acquaintance.getAcquaintanceId();
        Long oldAcqCnt = acquaintanceRepository.count();

        EventRequestDto requestDto = setRequestData();

        EventResultDto result = eventService.insertEvent(email, requestDto);

        em.flush();
        em.clear();

        assertThat(result.getResult()).isEqualTo("success");

        // 지인 수가 변하지 않음 확인
        long newAcqCnt = acquaintanceRepository.count();
        assertThat(newAcqCnt).isEqualTo(oldAcqCnt);

        // 기존 지인id와 연결된 지인id 동일 여부 비교
        Event newEvent = eventRepository.findByEventId(result.getEventDto().getEventId());
        Acquaintance connectedAcq = newEvent.getEventAcquaintances().get(0).getAcquaintance();
        Long connectedId = connectedAcq.getAcquaintanceId();

        assertThat(connectedId).isEqualTo(acquaintance.getAcquaintanceId());
        assertThat(connectedAcq.getName()).isEqualTo("테스트 친구");
    }
}
