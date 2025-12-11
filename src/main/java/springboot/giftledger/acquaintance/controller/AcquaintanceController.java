package springboot.giftledger.acquaintance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.acquaintance.service.AcquaintanceService;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.event.dto.EventUpdateResponse;

@RestController
@RequestMapping("/acquaintance")
@RequiredArgsConstructor
@Slf4j
public class AcquaintanceController {

	private final AcquaintanceService acquaintanceService;
	
	
	@GetMapping()
	public ResponseEntity<ResultDto<Page<AcquaintanceDto>>>  acquaintanceList(@AuthenticationPrincipal String email,
																			  @PageableDefault(size = 5, sort = "acquaintanceId", direction = Sort.Direction.DESC) Pageable pageable) {
		
		log.info("[PUT /acquaintance - Controller] : Start");
		ResultDto<Page<AcquaintanceDto>> acqResultDto = acquaintanceService.acquaintanceList(email, pageable);
		
		
        if ("success".equals(acqResultDto.getResult())) {
            log.info("[GET /acquaintance - Controller] : End");
        	return ResponseEntity.ok(acqResultDto);
        }
        else {
        	log.info("[PUT /acquaintance - Controller] : ERROR");
            return ResponseEntity.status(401).body(acqResultDto);
        }
		
	}
	
	
}
