package springboot.giftledger.event.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.service.impl.EventServiceImpl;
import springboot.giftledger.repository.EventAcquaintanceRepository;
import springboot.giftledger.repository.GiftLogRepository;

@ExtendWith(MockitoExtension.class)
public class EventServiceDetailGiftLogTest {
	
    @Mock
    GiftLogRepository giftLogRepository;
    @Mock
    EventAcquaintanceRepository eventAcquaintanceRepository;

    @InjectMocks
    EventServiceImpl eventService;
    
    private GiftLog giftLog;
    private EventAcquaintance eventAcq;
    private Long giftId;
    String email ;
    
    @BeforeEach
    void setup() {
    	giftId = 1L;
    	email = "test@test.com";
    	
    	giftLog = GiftLog.builder()
                .giftId(giftId)
                .eventAcquaintance(
                        EventAcquaintance.builder().eventAcquaintanceId(10L).build()
                )
                .actionType(ActionType.GIVE)
                .payMethod(PayMethod.CASH)
                .build();
    	
    	eventAcq = EventAcquaintance.builder()
                .eventAcquaintanceId(10L)
                .event(Event.builder()
                			.eventType(EventType.ETC)
                			.isOwner(true)
                			.member(Member.builder()
                						  .email(email)
                						  .build())
                			.build())
                .acquaintance(Acquaintance.builder()
                						  .relation(Relation.FAMILY)
                						  .member(new Member())
                						  .build())
                .build();

    }
    
    
    @Test
    @DisplayName("detailsGiftLog 성공 테스트")
    void detailsGiftLog_success() throws Exception {
    	
    	//given
    	given(giftLogRepository.findById(giftId))
        .willReturn(Optional.of(giftLog));
    	
    	given(eventAcquaintanceRepository.findById(10L))
        .willReturn(Optional.of(eventAcq));
    	
    	//when
    	ResultDto<EventUpdateResponse> result = eventService.detailsGiftLog(email, giftId);
    	
    	//then
    	assertEquals("success", result.getResult());
        assertNotNull(result.getData());
    	
    	
    }
    
    @Test
    @DisplayName("detailsGiftLog 실패 테스트 - GiftLog 없음")
    void detailsGiftLog_fail_noGiftLog() throws Exception {
    	
    	//given
    	given(giftLogRepository.findById(giftId))
        .willReturn(Optional.empty());

    	
    	//when
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.detailsGiftLog("test@test.com", giftId)
        );
        
    	//then
        assertEquals("해당 경조사비 전달 이력이 존재하지 않습니다.", e.getMessage());

    	
    	
    }
    
    @Test
    @DisplayName("detailsGiftLog 실패 테스트 - EventAcquaintance  없음")
    void detailsGiftLog_fail_noEventAcq() {
        // given
        Long giftId = 1L;

        GiftLog giftLog = GiftLog.builder()
                .giftId(giftId)
                .eventAcquaintance(
                        EventAcquaintance.builder().eventAcquaintanceId(10L).build()
                )
                .build();

        given(giftLogRepository.findById(giftId))
                .willReturn(Optional.of(giftLog));

        given(eventAcquaintanceRepository.findById(10L))
                .willReturn(Optional.empty());

        // when & then
        IllegalArgumentException e = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.detailsGiftLog("test@test.com", giftId)
        );

        assertEquals("잘못된 요청 입니다.", e.getMessage());
    }

}
