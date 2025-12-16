package springboot.giftledger.event.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import springboot.giftledger.event.dto.EventDetailsResultDto;
import springboot.giftledger.event.dto.EventDto;
import springboot.giftledger.event.dto.EventListResponse;
import springboot.giftledger.event.dto.EventRequestDto;
import springboot.giftledger.event.dto.EventResultDto;
import springboot.giftledger.event.dto.EventUpdateRequest;
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.dto.GiftLogDto;
import springboot.giftledger.event.dto.GuestLogDto;
import springboot.giftledger.event.service.EventService;
import springboot.giftledger.repository.AcquaintanceRepository;
import springboot.giftledger.repository.EventAcquaintanceRepository;
import springboot.giftledger.repository.EventRepository;
import springboot.giftledger.repository.GiftLogRepository;
import springboot.giftledger.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService{
	
	private final EventRepository eventRepository;
	private final AcquaintanceRepository acqRepository;
	private final EventAcquaintanceRepository eventAcqRepository;
	private final GiftLogRepository giftLogRepository;
    private final AcquaintanceRepository acquaintanceRepository;
    private final MemberRepository memberRepository;
    private final EventAcquaintanceRepository eventAcquaintanceRepository;
	
	

	@Override
	public ResultDto<EventUpdateResponse> detailsGiftLog(String email, Long giftId) {
		
		GiftLog giftLog = giftLogRepository.findById(giftId).orElseThrow(
					() -> new IllegalArgumentException("해당 경조사비 전달 이력이 존재하지 않습니다.")
				);
		
		EventAcquaintance eventAcq = eventAcquaintanceRepository.findById(giftLog.getEventAcquaintance()
																				 .getEventAcquaintanceId()).orElseThrow(
				() -> new IllegalArgumentException("잘못된 요청 입니다.")
			);
		
		if(!eventAcq.getEvent().getMember().getEmail().equals(email)) {
			throw new AccessDeniedException("조회 권한이 없습니다");
		}
		
		EventUpdateResponse response = EventUpdateResponse.builder()
														  .event(toEventDto(eventAcq.getEvent()))
														  .acquaintance(toAcquaintanceDto(eventAcq.getAcquaintance()))
														  .giftLog(toGiftLogDto(giftLog))
														  .build();
		
		
		return ResultDto.of("success", response);
	}
    
    
	@Override
	@Transactional
	public ResultDto<EventUpdateResponse> updateEvent(long eventId, EventUpdateRequest req, String userName) {
		
		EventDto eventDto= req.getEvent();
		AcquaintanceDto acquaintanceDto= req.getAcquaintance();
		GiftLogDto giftLogDto = req.getGiftLog();
		
		// 이벤트 정보 -> 본인 확인
		Event event = eventRepository.findById(eventId).orElseThrow(
						() -> new IllegalArgumentException("해당 이벤트가 존재하지 않습니다.")
					);

		if(!event.getMember().getEmail().equals(userName)) {
			throw new AccessDeniedException("해당 이벤트를 수정할 권한이 없습니다.");
		}
		
		
		
		// 지인 정보 가져오기
		Acquaintance acq = acqRepository.findById(req.getAcquaintance().getAcquaintanceId()).orElseThrow(
					() -> new IllegalArgumentException("해당 지인 정보가 존재하지 않습니다")
				);	
		
		
		// GiftLog 가져오기
		GiftLog giftLog = giftLogRepository.findById(giftLogDto.getGiftLogId()).orElseThrow(
						() -> new IllegalArgumentException("해당 경조사비 내역이 존재하지 않습니다.")
					);
		
		if( giftLog.getEventAcquaintance().getAcquaintance().getAcquaintanceId() != acq.getAcquaintanceId()
			|| giftLog.getEventAcquaintance().getEvent().getEventId() != event.getEventId()) {
			
			log.info("Acq : "  + acq.getAcquaintanceId() 
					+ " / giftLog : " +  giftLog.getEventAcquaintance().getAcquaintance().getAcquaintanceId()
					+ " / event : " + event.getEventId()
					+ " / giftLog : " +  giftLog.getEventAcquaintance().getEvent().getEventId());
			throw new IllegalArgumentException("요청이 올바르지 않습니다");
			
		}
		
		// 지인 정보 수정
	    acq.setName(acquaintanceDto.getName());
	    acq.setRelation(Relation.fromDescription(acquaintanceDto.getRelation()));
	    acq.setGroupName(acquaintanceDto.getGroupName());
	    acq.setPhone(acquaintanceDto.getPhone());
		
		// 이벤트 정보 수정
		event.setEventType(EventType.fromDescription(eventDto.getEventType()));
	    event.setEventName(eventDto.getEventName());
	    event.setEventDate(eventDto.getEventDate());
	    event.setLocation(eventDto.getLocation());
	    
	    
	    // 경조사비 정보 수정
	    giftLog.setAmount(giftLogDto.getAmount());
	    giftLog.setActionType(ActionType.fromDescription(giftLogDto.getActionType()));
	    giftLog.setPayMethod(PayMethod.fromDescription(giftLogDto.getPayMethod()));
	    giftLog.setMemo(giftLogDto.getMemo());
	    
		
	    
	    EventUpdateResponse response = EventUpdateResponse.builder()
	    												  .acquaintance(toAcquaintanceDto(acq))
	    												  .event(toEventDto(event))
	    												  .giftLog(toGiftLogDto(giftLog))
	    												  .build();
	    
		
		return ResultDto.of("success", response);
	}


	@Override
	public ResultDto<Page<EventListResponse>> eventList(String email, Pageable pageable) {

	    Page<Event> eventPage =
	            eventRepository.findByMember_Email(email, pageable);

	    Page<EventListResponse> mapped =
	    		eventPage.map(event -> {

	    		    long totalAmount = Optional.ofNullable(
	    		            giftLogRepository.sumAmountByEventId(event.getEventId())
	    		    ).orElse(0L);

	    		    EventDto response = toEventDto(event);
	    		    response.setTotalAmount(totalAmount);

	    		    // 내가 주최한 이벤트
	    		    if (Boolean.TRUE.equals(event.getIsOwner())) {

	    		        return EventListResponse.builder()
	    		                .event(response)
	    		                .ownerName(event.getMember().getName())
	    		                .relation("본인")
	    		                .memo("")
	    		                .build();
	    		    }

	    		    List<EventAcquaintance> eaList =
	    		            eventAcqRepository.findAllByEventIdWithAcquaintance(event.getEventId());

	    		    if (eaList.isEmpty()) {
	    		        return EventListResponse.builder()
	    		                .event(response)
	    		                .ownerName("미등록")
	    		                .relation("")
	    		                .memo("")
	    		                .build();
	    		    }

	    		    EventAcquaintance ea = eaList.get(0);

	    		    Optional<GiftLog> giftOpt =
	    		            giftLogRepository.findFirstByEventId(event.getEventId());

	    		    return EventListResponse.builder()
	    		            .event(response)
	    		            .ownerName(ea.getAcquaintance().getName())
	    		            .relation(ea.getAcquaintance().getRelation().getDescription())
	    		            .memo(giftOpt.map(GiftLog::getMemo).orElse(""))
	    		            .build();
	    		});

	    return ResultDto.of("success", mapped);
	}
	
	
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
                    .relation(Relation.fromDescription( acquaintanceDto.getRelation() ))
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
                .eventType(EventType.fromDescription(eventDto.getEventType()))
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
                .actionType(ActionType.fromDescription( giftLogDto.getActionType() ))
                .amount(giftLogDto.getAmount())
                .payMethod(PayMethod.fromDescription(giftLogDto.getPayMethod()))
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
                .eventType(event.getEventType().getDescription())
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
                    .relation(acquaintance.getRelation().getDescription())
                    .build();

            List<GiftLog> giftLogs = eAcq.getGiftLogs();

            if (giftLogs != null && !giftLogs.isEmpty()) {
                for (GiftLog gl : giftLogs) {
                    GiftLogDto giftLogDto = GiftLogDto.builder()
                            .giftLogId(gl.getGiftId())
                            .actionType(gl.getActionType().getDescription())
                            .amount(gl.getAmount())
                            .payMethod(gl.getPayMethod().getDescription())
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
                    .relation(Relation.fromDescription(acquaintanceDto.getRelation()))
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
                .payMethod(PayMethod.fromDescription(giftLogDto.getPayMethod()))
                .actionType(ActionType.fromDescription(giftLogDto.getActionType()))
                .memo(giftLogDto.getMemo())
                .build();

        giftLogRepository.save(giftLog);

        return EventDetailsResultDto.builder()
                .result("success")
                .build();
    }
	
	
	
	/* toDto Method*/
	
	private EventDto toEventDto(Event event) {
	    return EventDto.builder()
	            .eventId(event.getEventId())
	            .eventType(event.getEventType().getDescription())
	            .eventName(event.getEventName())
	            .eventDate(event.getEventDate())
	            .location(event.getLocation())
	            .isOwner(event.getIsOwner())
	            .build();
	}
	
	private AcquaintanceDto toAcquaintanceDto(Acquaintance acq) {
	    return AcquaintanceDto.builder()
	            .acquaintanceId(acq.getAcquaintanceId())
	            .memberId(acq.getMember().getMemberId())
	            .name(acq.getName())
	            .relation(acq.getRelation().getDescription())
	            .groupName(acq.getGroupName())
	            .phone(acq.getPhone())
	            .build();
	}
	
	private GiftLogDto toGiftLogDto(GiftLog gl) {
	    return GiftLogDto.builder()
	            .giftLogId(gl.getGiftId())
	            .amount(gl.getAmount())
	            .actionType(gl.getActionType().getType())
	            .payMethod(gl.getPayMethod().getDescription())
	            .memo(gl.getMemo())
	            .build();
	}





	

	
	
}
