package springboot.giftledger.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import springboot.giftledger.entity.*;
import springboot.giftledger.event.dto.*;
import springboot.giftledger.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AcquaintanceRepository acquaintanceRepository;
    private final GiftLogRepository giftLogRepository;
    private final MemberRepository memberRepository;
    private final EventAcquaintanceRepository eventAcquaintanceRepository;

    @Override
    @Transactional
    public EventResultDto insertEvent(String email, EventRequestDto eventRequestDto) {
        EventDto eventDto = eventRequestDto.getEventDto();
        AcquaintanceDto acquaintanceDto = eventRequestDto.getAcquaintanceDto();
        GiftLogDto giftLogDto = eventRequestDto.getGiftLogDto();

        // memberId에 대한 db 확인 중복..어떻게? 최적화 필요.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원 ID 입니다."));

        // member로 해당 멤버에 속한 것이 맞는지 검증해줘야됨!!!!!!!!!!
        Acquaintance acquaintance = acquaintanceRepository.findByPhone_AndMember(acquaintanceDto.getPhone(), member);

        // 1. 지인이 이미 있는지 전화번호로 확인.
        if (acquaintance == null) {
            acquaintance = Acquaintance.builder()
                    .name(acquaintanceDto.getName())
                    .member(member)
                    .relation(acquaintanceDto.getRelation())
                    .groupName(acquaintanceDto.getGroupName())
                    .phone(acquaintanceDto.getPhone())
                    .build();

            log.info("[EventServiceImpl - insertEvent] 새로운 지인 저장 시도 acquaintance: {}", acquaintance);
            acquaintanceRepository.save(acquaintance);
            log.info("[EventServiceImpl - insertEvent] 새로운 지인 저장 완료");
        }
        acquaintanceDto.setAcquaintanceId(acquaintance.getAcquaintanceId());

        // 이벤트 등록
        Event event = Event.builder()
                .member(member)
                .eventType(eventDto.getEventType())
                .eventName(eventDto.getEventName())
                .eventDate(eventDto.getEventDate())
                .location(eventDto.getLocation())
                .isOwner(eventDto.isOwner())
                .build();

        log.info("[EventServiceImpl - insertEvent] event db에 저장 시도: {}", event);
        eventRepository.save(event);
        eventDto.setEventId(event.getEventId());
        log.info("[EventServiceImpl - insertEvent] event 저장 완료");

        // 관계 테이블 등록
        EventAcquaintance eventAcquaintance = EventAcquaintance.builder()
                .event(event)
                .acquaintance(acquaintance)
                .build();

        eventAcquaintanceRepository.save(eventAcquaintance);
        log.info("[EventServiceImpl - insertEvent] 관계 테이블 저장 완료");

        // 4. 기프트로그 등록
        GiftLog giftLog = GiftLog.builder()
                .eventAcquaintance(eventAcquaintance)
                .actionType(giftLogDto.getActionType())
                .amount(giftLogDto.getAmount())
                .payMethod(giftLogDto.getPayMethod())
                .memo(giftLogDto.getMemo())
                .build();

        log.info("[EventServiceImpl - insertEvent] giftLog db에 저장 시도: {}", giftLog);
        giftLogRepository.save(giftLog);
        giftLogDto.setGiftLogId(giftLog.getGiftId());
        log.info("[EventServiceImpl - insertEvent] giftLog 저장 완료");

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
        // giftLogId로 삭제
        giftLogRepository.deleteByGiftId(giftId);

        log.info("[EventService - deleteEvent] Gift 내역 삭제 완료.");

        return EventResultDto.builder()
                .result("success")
                .build();
    }

    @Override
    @Transactional
    public EventDetailsResultDto detailsEvent(String email, Long eventId) {

        Event event = eventRepository.findDetailsByEventId(email, eventId);
        EventDto eventDto = EventDto.builder()
                .eventId(event.getEventId())
                .eventType(event.getEventType())
                .eventName(event.getEventName())
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .isOwner(event.getIsOwner())
                .build();

        List<GuestLogDto> guestLogDtos = new ArrayList<>();

        for(EventAcquaintance eAcq : event.getEventAcquaintances()) {

            Acquaintance acquaintance = eAcq.getAcquaintance();

            AcquaintanceDto acquaintanceDto = AcquaintanceDto.builder()
                    .acquaintanceId(acquaintance.getAcquaintanceId())
                    .name(acquaintance.getName())
                    .groupName(acquaintance.getGroupName())
                    .phone(acquaintance.getPhone())
                    .relation(acquaintance.getRelation())
                    .build();

            List<GiftLog> giftLogs = eAcq.getGiftLogs();

            if (giftLogs != null && !giftLogs.isEmpty()) {
                for (GiftLog gl : giftLogs) {
                    GiftLogDto giftLogDto = GiftLogDto.builder()
                            .giftLogId(gl.getGiftId())
                            .actionType(gl.getActionType())
                            .amount(gl.getAmount())
                            .payMethod(gl.getPayMethod())
                            .memo(gl.getMemo())
                            .build();

                    GuestLogDto guestLogItem = GuestLogDto.builder()
                            .acquaintanceDto(acquaintanceDto)
                            .giftLogDto(giftLogDto)
                            .build();

                    guestLogDtos.add(guestLogItem);
                }
            }
        }

        return EventDetailsResultDto.builder()
                .result("success")
                .eventDto(eventDto)
                .guestLogDtos(guestLogDtos)
                .build();
    }

    @Override
    @Transactional
    public EventDetailsResultDto insertEventOnDetails(String email, Long eventId, EventRequestDto eventRequestDto) {

        Event event = eventRepository.findByEventId(eventId);

        if(!event.getMember().getEmail().equals(email)) {
            throw new SecurityException("이 이벤트를 수정할 권한이 없습니다.");
        }

        AcquaintanceDto acquaintanceDto = eventRequestDto.getAcquaintanceDto();
        GiftLogDto giftLogDto = eventRequestDto.getGiftLogDto();

        // 지인이 이미 있는지 전화번호로 확인. - 해당 멤버의 지인인지 동시에 확인
        Acquaintance acquaintance = acquaintanceRepository.findByPhone_AndMember_Email(acquaintanceDto.getPhone(), email);

        if (acquaintance == null) {
            acquaintance = Acquaintance.builder()
                    .name(acquaintanceDto.getName())
                    .member(event.getMember())
                    .relation(acquaintanceDto.getRelation())
                    .groupName(acquaintanceDto.getGroupName())
                    .phone(acquaintanceDto.getPhone())
                    .build();

            log.info("새로운 지인 저장 시도 acquaintance: {}", acquaintance);
            acquaintanceRepository.save(acquaintance);
            log.info("새로운 지인 저장 완료");
        }

        EventAcquaintance eventAcquaintance = EventAcquaintance.builder()
                .acquaintance(acquaintance)
                .event(event)
                .build();

        eventAcquaintanceRepository.save(eventAcquaintance);

        GiftLog giftLog = GiftLog.builder()
                .eventAcquaintance(eventAcquaintance)
                .amount(giftLogDto.getAmount())
                .payMethod(giftLogDto.getPayMethod())
                .actionType(giftLogDto.getActionType())
                .memo(giftLogDto.getMemo())
                .build();

        giftLogRepository.save(giftLog);

        return EventDetailsResultDto.builder()
                .result("success")
                .build();
    }
}
