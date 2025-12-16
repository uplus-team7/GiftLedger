package springboot.giftledger.acquaintance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.common.dto.ResultDto;

public interface AcquaintanceService {
	
	ResultDto<Page<AcquaintanceDto>> acquaintanceList (String email, String keyword, Pageable pageable);

}
