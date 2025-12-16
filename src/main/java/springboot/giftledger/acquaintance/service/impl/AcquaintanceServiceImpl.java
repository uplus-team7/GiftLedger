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
	public ResultDto<Page<AcquaintanceDto>> acquaintanceList(String email, String keyword ,Pageable pageable) {
		
		Page<Acquaintance> acqPage = (keyword == null) ? 
				acquaintanceRepository.findByMemberEmail(email, pageable) : 
				acquaintanceRepository.findByMemberEmailAndNameContaining(email, keyword, pageable);
		
		Page<AcquaintanceDto> mapped = 
				acqPage.map(acq -> {
					return toAcquaintanceDto(acq);
				});
		
		
		return (mapped.isEmpty()) ? ResultDto.of("fail", mapped)  : ResultDto.of("success", mapped);
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
