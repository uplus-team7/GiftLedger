package springboot.giftledger.event.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Event;
import springboot.giftledger.entity.EventAcquaintance;
import springboot.giftledger.entity.GiftLog;
import springboot.giftledger.entity.Member;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;
import springboot.giftledger.enums.PayMethod;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.event.dto.EventDto;
import springboot.giftledger.event.dto.EventUpdateRequest;
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.dto.GiftLogDto;
import springboot.giftledger.event.service.impl.EventServiceImpl;
import springboot.giftledger.repository.AcquaintanceRepository;
import springboot.giftledger.repository.EventRepository;
import springboot.giftledger.repository.GiftLogRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceUpdateTest {

    @Mock EventRepository eventRepository;
    @Mock AcquaintanceRepository acqRepository;
    @Mock GiftLogRepository giftLogRepository;

    @InjectMocks EventServiceImpl eventService;

    // 공통 테스트 데이터
    Event event;
    Acquaintance acquaintance;
    GiftLog giftLog;
    EventAcquaintance eventAcq;

    EventUpdateRequest req;
    EventDto eventDto;
    AcquaintanceDto acqDto;
    GiftLogDto giftLogDto;

    String userEmail = "me@test.com";


    @BeforeEach
    void setup() {

        // Member
        Member member = Member.builder()
                .email(userEmail)
                .name("사용자")
                .build();

        // Event
        event = Event.builder()
                .eventId(1L)
                .member(member)
                .eventName("기존 이벤트")
                .isOwner(true)
                .build();

        // Acquaintance
        acquaintance = Acquaintance.builder()
                .acquaintanceId(10L)
                .member(member)
                .name("기존 지인")
                .relation(Relation.FRIEND)
                .build();

        // EventAcquaintance
        eventAcq = EventAcquaintance.builder()
                .event(event)
                .acquaintance(acquaintance)
                .build();

        // GiftLog
        giftLog = GiftLog.builder()
                .giftId(100L)
                .eventAcquaintance(eventAcq)
                .amount(1000L)
                .memo("기존 메모")
                .build();

        // DTOs (업데이트 요청)
        eventDto = EventDto.builder()
                .eventId(1L)
                .eventName("업데이트된 이벤트")
                .eventType(EventType.WEDDING.getDescription())
                .eventDate(LocalDateTime.now())
                .location("서울")
                .build();

        acqDto = AcquaintanceDto.builder()
                .acquaintanceId(10L)
                .name("수정된 지인")
                .relation(Relation.FAMILY.getDescription())
                .groupName("친척")
                .phone("01012345678")
                .build();

        giftLogDto = GiftLogDto.builder()
                .giftLogId(100L)
                .amount(50000L)
                .actionType(ActionType.GIVE.getDescription())
                .payMethod(PayMethod.CASH.getDescription())
                .memo("수정된 메모")
                .build();

        req = EventUpdateRequest.builder()
                .event(eventDto)
                .acquaintance(acqDto)
                .giftLog(giftLogDto)
                .build();
    }

    // ---------------- CASE 1 ----------------
    @Test
    @DisplayName("updateEvent 성공")
    void updateEvent_success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(acqRepository.findById(10L)).thenReturn(Optional.of(acquaintance));
        when(giftLogRepository.findById(100L)).thenReturn(Optional.of(giftLog));

        ResultDto<EventUpdateResponse> result = eventService.updateEvent(1L, req, userEmail);

        assertEquals("success", result.getResult());
        assertEquals("업데이트된 이벤트", result.getData().getEvent().getEventName());
    }

    // ---------------- CASE 2 ----------------
    @Test
    @DisplayName("updateEvent 실패 - 이벤트 없음")
    void fail_event_not_found() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> eventService.updateEvent(1L, req, userEmail));

        assertEquals("해당 이벤트가 존재하지 않습니다.", e.getMessage());
    }

    // ---------------- CASE 3 ----------------
    @Test
    @DisplayName("updateEvent 실패 - 권한 없음")
    void fail_no_permission() {

        // event.member.email != userName
        event.getMember().setEmail("other@test.com");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        Exception e = assertThrows(AccessDeniedException.class,
                () -> eventService.updateEvent(1L, req, userEmail));

        assertEquals("해당 이벤트를 수정할 권한이 없습니다.", e.getMessage());
    }

    // ---------------- CASE 4 ----------------
    @Test
    @DisplayName("updateEvent 실패 - 지인 정보 없음")
    void fail_acquaintance_not_found() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(acqRepository.findById(10L)).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> eventService.updateEvent(1L, req, userEmail));

        assertEquals("해당 지인 정보가 존재하지 않습니다", e.getMessage());
    }


    // ---------------- CASE 5 ----------------
    @Test
    @DisplayName("updateEvent 실패 - GiftLog 없음")
    void fail_giftlog_not_found() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(acqRepository.findById(10L)).thenReturn(Optional.of(acquaintance));
        when(giftLogRepository.findById(100L)).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> eventService.updateEvent(1L, req, userEmail));

        assertEquals("해당 경조사비 내역이 존재하지 않습니다.", e.getMessage());
    }

    // ---------------- CASE 6 ----------------
    @Test
    @DisplayName("updateEvent 실패 - 위변조 데이터 (eventId/acqId mismatch)")
    void fail_invalid_mapping() {

        // 위변조된 giftLog 구성
        EventAcquaintance wrongEa = EventAcquaintance.builder()
                .event(Event.builder().eventId(999L).build())
                .acquaintance(Acquaintance.builder().acquaintanceId(999L).build())
                .build();

        GiftLog wrongGiftLog = GiftLog.builder()
                .giftId(100L)
                .eventAcquaintance(wrongEa)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(acqRepository.findById(10L)).thenReturn(Optional.of(acquaintance));
        when(giftLogRepository.findById(100L)).thenReturn(Optional.of(wrongGiftLog));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> eventService.updateEvent(1L, req, userEmail));

        assertEquals("요청이 올바르지 않습니다", e.getMessage());
    }
}
