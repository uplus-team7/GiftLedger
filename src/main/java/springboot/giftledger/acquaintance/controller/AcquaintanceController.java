package springboot.giftledger.acquaintance.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.acquaintance.service.AcquaintanceService;
import springboot.giftledger.common.dto.ResultDto;

@RestController
@RequestMapping("/acquaintance")
@RequiredArgsConstructor
@Slf4j
public class AcquaintanceController {

	private final AcquaintanceService acquaintanceService;
	
	@Operation(summary = "지인 목록 조회" 
			 , description = """
			 			사용자가 등록한 지인 목록을 출력합니다
			 			- 키워드 미 요청 시 전체 중 5명
			 			- 키워드 요청 시 이름에 키워드가 포함되는 리스트 중 5명
			 			- 조회 된 지인이 없을 경우 400 
			 		""")
	@GetMapping
	public ResponseEntity<ResultDto<Page<AcquaintanceDto>>>  acquaintanceList(@AuthenticationPrincipal String email,
																			  @RequestParam(name = "keyword", required = false) String keyword,
																			  @PageableDefault(size = 5, sort = "acquaintanceId", direction = Sort.Direction.DESC) Pageable pageable) {
		
		log.info("[PUT /acquaintance - Controller] : Start");
		ResultDto<Page<AcquaintanceDto>> acqResultDto = acquaintanceService.acquaintanceList(email, keyword, pageable);
		
		
        if ("success".equals(acqResultDto.getResult())) {
            log.info("[GET /acquaintance - Controller] : End");
        	return ResponseEntity.ok(acqResultDto);
        }
        else {
        	log.info("[PUT /acquaintance - Controller] : ERROR");
            return ResponseEntity.status(400).body(acqResultDto);
        }
		
	}
	
	
}
