package springboot.giftledger.event.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Event;
import springboot.giftledger.entity.GiftLog;
import springboot.giftledger.event.dto.EventDto;
import springboot.giftledger.event.dto.EventUpdateRequest;
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.dto.GiftLogDto;
import springboot.giftledger.event.service.EventService;
import springboot.giftledger.repository.EventRepository;
import springboot.giftledger.repository.GiftLogRepository;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService{
	
	private final EventRepository eventRepository;
	private final GiftLogRepository giftLogRepository;
	
	
	@Override
	@Transactional
	public ResultDto<EventUpdateResponse> updateEvent(long eventId, EventUpdateRequest req, String userName) {
		
		EventDto eventDto= req.getEvent();
		AcquaintanceDto acquaintanceDto= req.getAcquaintance();
		GiftLogDto giftLogDto = req.getGiftLog();
		
		Event event = eventRepository.findById(eventId).orElseThrow(
						() -> new IllegalArgumentException("이벤트 없음")
					);
		
		Acquaintance acq = event.getAcquaintance();
		
		if(!acq.getMember().getEmail().equals(userName)) {
			throw new AccessDeniedException("해당 이벤트를 수정할 권한이 없습니다.");
		}
		
		
		GiftLog giftLog = giftLogRepository.findById(giftLogDto.getGiftId()).orElseThrow(
						() -> new IllegalArgumentException("경조사비 내역 없음")
					);
		
		// 지인 정보 수정
	    acq.setName(acquaintanceDto.getName());
	    acq.setRelation(acquaintanceDto.getRelation());
	    acq.setGroupName(acquaintanceDto.getGroupName());
	    acq.setPhone(acquaintanceDto.getPhone());
		
		// 이벤트 정보 수정
		event.setEventType(eventDto.getEventType());
	    event.setEventName(eventDto.getEventName());
	    event.setEventDate(eventDto.getEventDate());
	    event.setLocation(eventDto.getLocation());
	    event.setIsOwner(eventDto.getIsOwner());
	    
	    
	    // 경조사비 정보 수정
	    giftLog.setAmount(giftLogDto.getAmount());
	    giftLog.setActionType(giftLogDto.getActionType());
	    giftLog.setPayMethod(giftLogDto.getPayMethod());
	    giftLog.setMemo(giftLogDto.getMemo());
	    
		
	    
	    EventUpdateResponse response = EventUpdateResponse.builder()
	    												  .acquaintance(toAcquaintanceDto(acq))
	    												  .event(toEventDto(event))
	    												  .giftLog(toGiftLogDto(giftLog))
	    												  .build();
	    
		
		return ResultDto.of("success", response);
	}


	@Override
	public ResultDto<Page<EventUpdateResponse>> eventList(String email, Pageable pageable) {

		Page<Event> eventPage =
	            eventRepository.findByAcquaintanceMemberEmail(email, pageable);

	    Page<EventUpdateResponse> mapped =
	            eventPage.map(event -> {

	                Acquaintance acq = event.getAcquaintance();
	                GiftLog gift = giftLogRepository.findFirstByEventOrderByGiftIdDesc(event);

	                return EventUpdateResponse.builder()
	                        .acquaintance(toAcquaintanceDto(acq))
	                        .event(toEventDto(event))
	                        .giftLog(toGiftLogDto(gift))
	                        .build();
	            });
		
		
		
		
		
		return ResultDto.of("success", mapped);
	}
	
	
	
	/* toDto Method*/
	
	private EventDto toEventDto(Event event) {
	    return EventDto.builder()
	            .eventId(event.getEventId())
	            .eventType(event.getEventType())
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
	            .relation(acq.getRelation())
	            .groupName(acq.getGroupName())
	            .phone(acq.getPhone())
	            .build();
	}
	
	private GiftLogDto toGiftLogDto(GiftLog gl) {
	    return GiftLogDto.builder()
	            .giftId(gl.getGiftId())
	            .amount(gl.getAmount())
	            .actionType(gl.getActionType())
	            .payMethod(gl.getPayMethod())
	            .memo(gl.getMemo())
	            .build();
	}



	

	
	
}
