package springboot.giftledger.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Event;
import springboot.giftledger.entity.GiftLog;
import springboot.giftledger.entity.Member;
import springboot.giftledger.event.dto.*;
import springboot.giftledger.repository.AcquaintanceRepository;
import springboot.giftledger.repository.EventRepository;
import springboot.giftledger.repository.GiftLogRepository;
import springboot.giftledger.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AcquaintanceRepository acquaintanceRepository;
    private final GiftLogRepository giftLogRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public EventResultDto insertEvent(String email, EventRequestDto eventRequestDto) {
        EventDto eventDto = eventRequestDto.getEventDto();
        AcquaintanceDto acquaintanceDto = eventRequestDto.getAcquaintanceDto();
        GiftLogDto giftLogDto = eventRequestDto.getGiftLogDto();

        // memberId에 대한 db 확인 중복..어떻게? 최적화 필요.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID 입니다."));

        Acquaintance acquaintance = acquaintanceRepository.findByPhone(acquaintanceDto.getPhone());

        // 1. 지인이 이미 있는지 전화번호로 확인.
        if (acquaintance == null) {
            log.info("새로운 지인 정보 acquaintanceDto: {}", acquaintanceDto);
            acquaintance = Acquaintance.builder()
                    .name(acquaintanceDto.getName())
                    .member(member)
                    .relation(acquaintanceDto.getRelation())
                    .groupName(acquaintanceDto.getGroupName())
                    .phone(acquaintanceDto.getPhone())
                    .build();

            log.info("새로운 지인 저장 시도 acquaintance: {}", acquaintance);
            acquaintanceRepository.save(acquaintance);
            log.info("새로운 지인 저장 완료");
        }
        acquaintanceDto.setAcquaintanceId(acquaintance.getAcquaintanceId());

        // 이벤트 등록
        Event event = Event.builder()
                .acquaintance(acquaintance)
                .eventType(eventDto.getEventType())
                .eventName(eventDto.getEventName())
                .eventDate(eventDto.getEventDate())
                .location(eventDto.getLocation())
                .isOwner(eventDto.isOwner())
                .build();

        log.info("event db에 저장 시도: {}", event);
        eventRepository.save(event);
        eventDto.setEventId(event.getEventId());
        log.info("event 저장 완료");

        // 4. 기프트로그 등록
        GiftLog giftLog = GiftLog.builder()
                .event(event)
                .actionType(giftLogDto.getActionType())
                .amount(giftLogDto.getAmount())
                .payMethod(giftLogDto.getPayMethod())
                .memo(giftLogDto.getMemo())
                .build();

        log.info("giftLog db에 저장 시도: {}", giftLog);
        giftLogRepository.save(giftLog);
        giftLogDto.setGiftLogId(giftLog.getGiftId());
        log.info("giftLog 저장 완료");

        return EventResultDto.builder()
                .result("success")
                .acquaintanceDto(acquaintanceDto)
                .eventDto(eventDto)
                .giftLogDto(giftLogDto)
                .build();
    }

    @Override
    @Transactional
    public EventResultDto deleteEvent(String email, Long giftId) {
        // member 존재 확인
        // giftLogId로 삭제
        giftLogRepository.deleteByGiftId(giftId);

        log.info("[EventService - deleteEvent] Gift 내역 삭제 완료.");

        return EventResultDto.builder()
                .result("success")
                .build();
    }

    @Override
    @Transactional
    public EventResultDto detailsEvent(String email, Long eventId) {
        // email로 memberId 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID 입니다."));

        // => 한번에 조회해서, result로 묶어 보내면 -> 프론트에서 처리.
        // 하나의 이벤트에 무수히 많은 지인들과 그와 연결된 기프트로그
        // 즉, 하나의 이벤트 : 지인-기프트로그 리스트
        eventRepository.findDetailsByEventId(email, eventId);

        return null;
    }
}
