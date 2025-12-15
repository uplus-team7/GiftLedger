package springboot.giftledger.event;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import springboot.giftledger.entity.Member;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.EventType;
import springboot.giftledger.enums.PayMethod;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.event.dto.*;
import springboot.giftledger.event.service.EventService;
import springboot.giftledger.repository.MemberRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class InsertEventTest {
    private EventService eventService;
    private MemberRepository memberRepository;

    private String email;
    private Member testMember;

    @BeforeEach
    void setUp() {
        // 테스트를 위한 더미 회원 생성 및 저장 (실제 Member 객체 확보)
        testMember = Member.builder()
                .email("test@example.com")
                .password("testpassword")
                .name("테스터")
                .build();
        testMember = memberRepository.save(testMember);
        email = testMember.getEmail();
    }

    private EventRequestDto createValidRequestDto() {
        // DTO 빌더를 사용하여 유효한 요청 데이터 생성
        return EventRequestDto.builder()
                .eventDto(EventDto.builder()
                        .eventType(EventType.WEDDING).eventName("테스트 결혼식").eventDate(LocalDateTime.parse("2026-05-20")).isOwner(false).location("한남동 호텔").build())
                .acquaintanceDto(AcquaintanceDto.builder()
                        .name("테스트 지인").phone("010-9999-9999").relation(Relation.FRIEND).build())
                .giftLogDto(GiftLogDto.builder()
                        .actionType(ActionType.GIVE).amount(50000L).payMethod(PayMethod.CASH).build())
                .build();
    }

    @Test
    void insertEventTest(){
        EventRequestDto requestDto = createValidRequestDto();
        EventResultDto result = eventService.insertEvent(email, requestDto);

        assertEquals("success", result.getResult());
        assertNotNull(result, "result is null");
//        log.info("Result Status: {}", result);

        assertNotNull(result.getEventDto().getEventId(), "getEventDto is null");
        assertNotNull(result.getAcquaintanceDto().getAcquaintanceId(), "getAcquaintanceDto is null");
        assertNotNull(result.getGiftLogDto().getGiftLogId(), "getGiftLogDto is null");

    }
}
