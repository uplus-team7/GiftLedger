package springboot.giftledger.event.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.event.dto.EventUpdateRequest;
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.service.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

	private final EventService eventService;
	
	@PutMapping("/{eventId}")
	public ResponseEntity<ResultDto<EventUpdateResponse>> updateEvent( @PathVariable("eventId") long eventId
													 , @RequestBody EventUpdateRequest req 
													 , @AuthenticationPrincipal String principal ){
		
		log.info("[PUT /events/{eventId} - Controller] : Start");
		log.info("[PUT /events/{eventId} - Controller] : args -> eventId : " + eventId + " / userId : " + principal);
		
		ResultDto eventResultDto = eventService.updateEvent(eventId, req , principal);
		
		
        if ("success".equals(eventResultDto.getResult())) {
            log.info("[PUT /events/{eventId} - Controller] : End");
        	return ResponseEntity.ok(eventResultDto);
        }
        else {
        	log.info("[PUT /events/{eventId} - Controller] : End");
            return ResponseEntity.status(401).body(eventResultDto);
        }
        
	}
	
	
	@GetMapping()
	public ResponseEntity<ResultDto<Page<EventUpdateResponse>>> eventList(@AuthenticationPrincipal String principal,
																		  @PageableDefault(size = 5, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable){
		
		log.info("[GET /events - Controller] : Start");
		log.info("[PUT /events/{eventId} - Controller] : args -> userName : " + principal);
		
		ResultDto<Page<EventUpdateResponse>> eventResultDto = eventService.eventList(principal, pageable);
		
        if ("success".equals(eventResultDto.getResult())) {
            log.info("[PUT /events/{eventId} - Controller] : End");
        	return ResponseEntity.ok(eventResultDto);
        }
        else {
        	log.info("[PUT /events/{eventId} - Controller] : End");
            return ResponseEntity.status(401).body(eventResultDto);
        }
        
	}
	
	
	
}
