package springboot.giftledger.event.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.event.dto.EventDetailsResultDto;
import springboot.giftledger.event.dto.EventListResponse;
import springboot.giftledger.event.dto.EventRequestDto;
import springboot.giftledger.event.dto.EventResultDto;
import springboot.giftledger.event.dto.EventUpdateRequest;
import springboot.giftledger.event.dto.EventUpdateResponse;
import springboot.giftledger.event.service.EventService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {
	
	private final EventService eventService;
	
	@GetMapping("details/{giftId}")
	public ResponseEntity<ResultDto<EventUpdateResponse>> detcailsGiftLog(@AuthenticationPrincipal String email,
			 														@PathVariable("giftId") Long giftId){
		
		log.info("[GET /details/{giftId} - Controller] : Start");
		log.info("[GET /details/{giftId} - Controller] : args -> userName : " + email + ", giftId :" + giftId);
		
		ResultDto<EventUpdateResponse> eventResultDto = eventService.detailsGiftLog(email , giftId);
		
		log.info("[GET /details/{giftId} - Controller] : End");
		return ResponseEntity.ok(eventResultDto);
        
	}
	
	
	@PutMapping("/{eventId}")
	public ResponseEntity<ResultDto<EventUpdateResponse>> updateEvent( @PathVariable("eventId") long eventId
													 , @RequestBody EventUpdateRequest req 
													 , @AuthenticationPrincipal String email ){
		
		log.info("[PUT /events/{eventId} - Controller] : Start");
		log.info("[PUT /events/{eventId} - Controller] : args -> eventId : " + eventId + " / userId : " + email);
		
		ResultDto<EventUpdateResponse> eventResultDto = eventService.updateEvent(eventId, req , email);
		
		
		log.info("[PUT /events/{eventId} - Controller] : End");
		return ResponseEntity.ok(eventResultDto);
        
	}
	
	
	@GetMapping()
	public ResponseEntity<ResultDto<Page<EventListResponse>>> eventList(@AuthenticationPrincipal String principal,
																		  @PageableDefault(size = 5, sort = "eventDate", direction = Sort.Direction.DESC) Pageable pageable){
		
		log.info("[GET /events - Controller] : Start");
		log.info("[PUT /events/{eventId} - Controller] : args -> userName : " + principal);
		
		ResultDto<Page<EventListResponse>> eventResultDto = eventService.eventList(principal, pageable);
		log.info("[PUT /events/{eventId} - Controller] : End");
		return ResponseEntity.ok(eventResultDto);
        
	}
	

    @PostMapping("")
    public ResponseEntity<EventResultDto> insertEvent(
            @AuthenticationPrincipal String email,
            @RequestBody EventRequestDto eventRequestDto) {

        log.info("[EventController - insertEvent] 사용자 정보 email: {}", email);
        log.info("[EventController - insertEvent] 이벤트 등록 요청 eventRequestDto: {}", eventRequestDto);

        try {
            EventResultDto eventResultDto = eventService.insertEvent(email, eventRequestDto);

            if ("success".equals(eventResultDto.getResult())) {
                return ResponseEntity.ok(eventResultDto);
            } else {
                log.warn("[EventController - insertEvent] eventResultDto 등록 실패: {}", eventResultDto);
                return ResponseEntity.status(401).body(eventResultDto);
            }
        } catch (Exception e) {
            log.error("[EventController - insertEvent] 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(EventResultDto.builder().result("fail").build());
        }

    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsResultDto> detailsEvent(
            @AuthenticationPrincipal String email,
            @PathVariable("eventId") Long eventId) {

        log.info("[EventController - detailsEvent] 사용자 정보 email: {}", email);
        log.info("[EventController - detailsEvent] eventId: {}", eventId);

        try {
            EventDetailsResultDto eventDetailsResultDto = eventService.detailsEvent(email, eventId);

            log.info("[EventController - detailsEvent] eventDetailsResultDto: {}", eventDetailsResultDto);
            if ("success".equals(eventDetailsResultDto.getResult())) {
                return ResponseEntity.ok(eventDetailsResultDto);
            } else {
                log.warn("[EventController - detailsEvent] eventDetailsResultDto 등록 실패: {}", eventDetailsResultDto);
                return ResponseEntity.status(401).body(eventDetailsResultDto);
            }
        } catch (Exception e) {
            log.error("[EventController - detailsEvent] 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(EventDetailsResultDto.builder().result("fail").build());
        }

    }

    @DeleteMapping("/{giftId}")
    public ResponseEntity<EventResultDto> deleteEvent(
            @AuthenticationPrincipal String email,
            @PathVariable("giftId") Long giftId) {

        log.info("[EventController - deleteEvent] 사용자 정보 email: {}", email);
        log.info("[EventController - deleteEvent] giftId: {}", giftId);

        try {
            EventResultDto eventResultDto = eventService.deleteEvent(email, giftId);

            log.info("[EventController - deleteEvent] eventResultDto: {}", eventResultDto);
            if ("success".equals(eventResultDto.getResult())) {
                return ResponseEntity.ok(eventResultDto);
            } else {
                log.warn("[EventController - deleteEvent] eventResultDto 등록 실패: {}", eventResultDto);
                return ResponseEntity.status(401).body(eventResultDto);
            }
        } catch (Exception e) {
            log.error("[EventController - deleteEvent] 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(EventResultDto.builder().result("fail").build());
        }

    }

    @PostMapping("/{eventId}")
    public ResponseEntity<EventDetailsResultDto> insertEventOnDetails(
            @AuthenticationPrincipal String email,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventRequestDto eventRequestDto
    ){
        log.info("[EventController - insertEventOnDetails] 사용자 정보 email: {}", email);
        log.info("[EventController - insertEventOnDetails] eventId: {}", eventId);

        try {
            EventDetailsResultDto eventDetailsResultDto = eventService.insertEventOnDetails(email, eventId, eventRequestDto);

            log.info("[EventController - insertEventOnDetails] eventDetailsResultDto: {}", eventDetailsResultDto);
            if ("success".equals(eventDetailsResultDto.getResult())) {
                return ResponseEntity.ok(eventDetailsResultDto);
            } else {
                log.warn("[EventController - insertEventOnDetails] eventDetailsResultDto 등록 실패: {}", eventDetailsResultDto);
                return ResponseEntity.status(401).body(EventDetailsResultDto.builder().result("fail").build());
            }
        } catch (Exception e) {
            log.error("[EventController - insertEventOnDetails] 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(EventDetailsResultDto.builder().result("fail").build());
        }
    }
}
