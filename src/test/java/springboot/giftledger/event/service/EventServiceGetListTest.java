package springboot.giftledger.event.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Event;
import springboot.giftledger.entity.EventAcquaintance;
import springboot.giftledger.entity.GiftLog;
import springboot.giftledger.entity.Member;
import springboot.giftledger.enums.EventType;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.event.dto.EventListResponse;
import springboot.giftledger.event.service.impl.EventServiceImpl;
import springboot.giftledger.repository.EventAcquaintanceRepository;
import springboot.giftledger.repository.EventRepository;
import springboot.giftledger.repository.GiftLogRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceGetListTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    GiftLogRepository giftLogRepository;

    @Mock
    EventAcquaintanceRepository eventAcquaintanceRepository;

    @InjectMocks
    EventServiceImpl eventService;

    Pageable pageable = PageRequest.of(0, 10);

    // 공통 테스트용 도메인 객체
    Member member;
    Acquaintance acquaintance;
    Event eventOwner;         // isOwner = true
    Event eventNonOwner;      // isOwner = false
    EventAcquaintance ea;

    @BeforeEach
    void setup() {

        // 공통 Member
        member = Member.builder()
                .name("테스트유저")
                .build();

        // 공통 Acquaintance
        acquaintance = Acquaintance.builder()
                .name("지인A")
                .relation(Relation.FRIEND)
                .build();

        // Event: 본인이 주최한 이벤트
        eventOwner = Event.builder()
                .eventId(1L)
                .isOwner(true)
                .eventType(EventType.ETC)
                .member(member)
                .build();

        // Event: 지인이 주최한 이벤트
        eventNonOwner = Event.builder()
                .eventId(2L)
                .isOwner(false)
                .eventType(EventType.ETC)
                .member(member)
                .build();

        // EventAcquaintance (지인)
        ea = EventAcquaintance.builder()
                .acquaintance(acquaintance)
                .build();
    }


    // ---------------- CASE 1 ----------------
    @Test
    @DisplayName("eventList 성공 테스트 - 본인이 주최한 이벤트 (isOwner=true)")
    void eventList_success_owner() {

        when(eventRepository.findByMember_Email(eq("test@test.com"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(eventOwner)));

        // sumAmount null → orElse(0L)
        when(giftLogRepository.sumAmountByEventId(1L))
                .thenReturn(null);

        ResultDto<Page<EventListResponse>> result =
                eventService.eventList("test@test.com", pageable);

        assertEquals("success", result.getResult());
    }


    // ---------------- CASE 2 ----------------
    @Test
    @DisplayName("eventList 성공 테스트 - 지인이 있지만 등록되지 않은 경우 (eaList empty)")
    void eventList_no_owner_case() {

        when(eventRepository.findByMember_Email(eq("test@test.com"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(eventNonOwner)));

        when(giftLogRepository.sumAmountByEventId(2L))
                .thenReturn(100L);

        // eaList empty
        when(eventAcquaintanceRepository.findAllByEventIdWithAcquaintance(2L))
                .thenReturn(List.of());

        ResultDto<Page<EventListResponse>> result =
                eventService.eventList("test@test.com", pageable);

        assertEquals("success", result.getResult());
    }


    // ---------------- CASE 3 ----------------
    @Test
    @DisplayName("eventList - 지인 존재 + giftLog 없음 (giftOpt empty)")
    void eventList_ea_exist_no_gift() {

        when(eventRepository.findByMember_Email(eq("test@test.com"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(eventNonOwner)));

        when(giftLogRepository.sumAmountByEventId(2L))
                .thenReturn(200L);

        // eaList 존재
        when(eventAcquaintanceRepository.findAllByEventIdWithAcquaintance(2L))
                .thenReturn(List.of(ea));

        // giftOpt empty
        when(giftLogRepository.findFirstByEventId(2L))
                .thenReturn(Optional.empty());

        ResultDto<Page<EventListResponse>> result =
                eventService.eventList("test@test.com", pageable);

        assertEquals("success", result.getResult());
    }


    // ---------------- CASE 4 ----------------
    @Test
    @DisplayName("eventList - 지인 + giftLog 존재")
    void eventList_ea_and_gift_exist() {

        when(eventRepository.findByMember_Email(eq("test@test.com"), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(eventNonOwner)));

        when(giftLogRepository.sumAmountByEventId(2L))
                .thenReturn(500L);

        // eaList 존재
        when(eventAcquaintanceRepository.findAllByEventIdWithAcquaintance(2L))
                .thenReturn(List.of(ea));

        GiftLog giftLog = GiftLog.builder()
                .memo("메모 테스트")
                .build();

        when(giftLogRepository.findFirstByEventId(2L))
                .thenReturn(Optional.of(giftLog));

        ResultDto<Page<EventListResponse>> result =
                eventService.eventList("test@test.com", pageable);

        assertEquals("success", result.getResult());
    }

}
