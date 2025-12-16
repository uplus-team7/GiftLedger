package springboot.giftledger.acquaintance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.acquaintance.service.impl.AcquaintanceServiceImpl;
import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Member;
import springboot.giftledger.enums.Relation;
import springboot.giftledger.repository.AcquaintanceRepository;

@ExtendWith(MockitoExtension.class)
public class AcquaintanceServiceAcquaintanceListTest {
	
	@Mock
    AcquaintanceRepository acquaintanceRepository;

    @InjectMocks
    AcquaintanceServiceImpl acquaintanceService;

    String email = "test@test.com";
    Pageable pageable = PageRequest.of(0, 5);

    Acquaintance createAcq(Long id, String name) {
        return Acquaintance.builder()
                .acquaintanceId(id)
                .name(name)
                .member(new Member())
                .relation(Relation.ETC)
                .build();
    }

    @Test
    @DisplayName("acquaintanceList 성공 - keyword null")
    void success_keyword_null() {

        Page<Acquaintance> page =
                new PageImpl<>(List.of(createAcq(1L, "홍길동")), pageable, 1);

        when(acquaintanceRepository.findByMemberEmail(email, pageable))
                .thenReturn(page);

        ResultDto<Page<AcquaintanceDto>> result =
                acquaintanceService.acquaintanceList(email, null, pageable);

        assertEquals("success", result.getResult());
        assertFalse(result.getData().isEmpty());
    }


    @Test
    @DisplayName("acquaintanceList 성공 - keyword 존재")
    void success_keyword_exist() {

        Page<Acquaintance> page =
                new PageImpl<>(List.of(createAcq(2L, "김철수")), pageable, 1);

        when(acquaintanceRepository.findByMemberEmailAndNameContaining(eq(email), eq("철수"), eq(pageable)))
                .thenReturn(page);

        ResultDto<Page<AcquaintanceDto>> result =
                acquaintanceService.acquaintanceList(email, "철수", pageable);

        assertEquals("success", result.getResult());
        assertFalse(result.getData().isEmpty());
    }




    @Test
    @DisplayName("acquaintanceList 실패 - keyword 존재 + empty result")
    void fail_keyword_exist_empty() {

        Page<Acquaintance> emptyPage =
                new PageImpl<>(List.of(), pageable, 0);

        when(acquaintanceRepository.findByMemberEmailAndNameContaining(
                eq(email), eq("철수"), eq(pageable))
        ).thenReturn(emptyPage);

        ResultDto<Page<AcquaintanceDto>> result =
                acquaintanceService.acquaintanceList(email, "철수", pageable);

        assertEquals("fail", result.getResult());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("acquaintanceList 실패 - keyword null + empty result")
    void fail_keyword_null_empty() {
    	
    	Page<Acquaintance> emptyPage =
    			new PageImpl<>(List.of(), pageable, 0);
    	
    	when(acquaintanceRepository.findByMemberEmail(email, pageable))
    	.thenReturn(emptyPage);
    	
    	ResultDto<Page<AcquaintanceDto>> result =
    			acquaintanceService.acquaintanceList(email, null, pageable);
    	
    	assertEquals("fail", result.getResult());
    	assertTrue(result.getData().isEmpty());
    }
}
