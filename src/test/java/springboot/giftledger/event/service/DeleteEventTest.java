package springboot.giftledger.event.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import springboot.giftledger.entity.*;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;
import springboot.giftledger.enums.PayMethod;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.event.dto.EventResultDto;
import springboot.giftledger.repository.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@SpringBootTest
@Transactional
public class DeleteEventTest {
    @Autowired private EntityManager em;

    @Autowired
    private EventService eventService;

//    @MockitoBean
//    private GiftLogRepository giftLogRepository;
//
//    @Test
//    @DisplayName("선물 내역 삭제 성공 테스트")
//    void deleteEvent_success() {
//        // [Given]
//        Long giftId = 100L;
//
//        // [When]
//        EventResultDto result = eventService.deleteEvent("user@test.com", giftId);
//
//        // [Then]
//        assertThat(result.getResult()).isEqualTo("success");
//        verify(giftLogRepository, times(1)).deleteByGiftId(giftId);
//    }


    @Autowired private MemberRepository memberRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private AcquaintanceRepository acquaintanceRepository;
    @Autowired private EventAcquaintanceRepository eventAcquaintanceRepository;
    @Autowired private GiftLogRepository giftLogRepository;

    @Test
    @DisplayName("선물 내역 삭제 - 선물 내역만 삭제, 이벤트나 지인은 유지")
    void deleteEvent_success() {
        String email = "test@test.com";
        GiftLog tgt = setBaseData(email);

        Long targetId = tgt.getGiftId();
        Long eventId = tgt.getEventAcquaintance().getEvent().getEventId();
        Long acqId = tgt.getEventAcquaintance().getAcquaintance().getAcquaintanceId();

        // 값이 잘 들어갔는지 확인
        assertThat(giftLogRepository.findById(targetId)).isPresent();

        EventResultDto result = eventService.deleteEvent(email, targetId);

        em.flush();
        em.clear();

        assertThat(result.getResult().equals("success"));

        // 내역 삭제 확인
        assertThat(giftLogRepository.findById(targetId)).isEmpty();

        // 부모 데이터 존재 확인
        assertThat(eventRepository.findById(eventId)).isPresent();
        assertThat(acquaintanceRepository.findById(acqId)).isPresent();
    }


    GiftLog setBaseData(String email) {
        Member member = memberRepository.save(Member.builder()
                .email(email).password("pass").name("테스트").ages("20").build());

        Event event = eventRepository.save(Event.builder()
                .member(member)
                .eventName("삭제 테스트용 이벤트")
                .eventType(EventType.ETC)
                .eventDate(LocalDateTime.now())
                .isOwner(true).build());

        Acquaintance acq = acquaintanceRepository.save(Acquaintance.builder()
                .member(member)
                .name("삭제대상친구")
                .phone("010-0000-0000")
                .relation(Relation.FRIEND)
                .build());

        EventAcquaintance ea = eventAcquaintanceRepository.save(EventAcquaintance.builder()
                .event(event)
                .acquaintance(acq).build());

        return giftLogRepository.save(GiftLog.builder()
                .eventAcquaintance(ea)
                .amount(50000L)
                .actionType(ActionType.GIVE)
                .payMethod(PayMethod.CASH)
                .memo("곧 지워질 운명").build());
    }
}
