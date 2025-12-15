package springboot.giftledger.acquaintance.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.acquaintance.service.AcquaintanceService;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.repository.AcquaintanceRepository;

@Service
@RequiredArgsConstructor
public class AcquaintanceServiceImpl implements AcquaintanceService {

	private final AcquaintanceRepository acquaintanceRepository;

	@Override
	public ResultDto<Page<AcquaintanceDto>> acquaintanceList(String email, Pageable pageable) {
		
		Page<Acquaintance> acqPage = 
				acquaintanceRepository.findByMemberEmail(email, pageable);
		
		Page<AcquaintanceDto> mapped = 
				acqPage.map(acq -> {
					return toAcquaintanceDto(acq);
				});
		
		
		return ResultDto.of("success", mapped);
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
	
	
	
}
