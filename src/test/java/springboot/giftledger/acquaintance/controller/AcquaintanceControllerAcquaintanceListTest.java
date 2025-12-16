package springboot.giftledger.acquaintance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.acquaintance.service.AcquaintanceService;
import springboot.giftledger.common.dto.ResultDto;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AcquaintanceController.class)
public class AcquaintanceControllerAcquaintanceListTest {
	
	    @Autowired
	    MockMvc mockMvc;

	    @MockitoBean
	    AcquaintanceService acquaintanceService;

	    Page<AcquaintanceDto> emptyPage = new PageImpl<>(java.util.List.of(), PageRequest.of(0, 5), 0);



	    @Test
	    @DisplayName("GET /acquaintance 성공 - status 200")
	    @WithMockUser(username = "test@test.com", roles = "USER")
	    void acquaintanceList_success() throws Exception {

	        ResultDto<Page<AcquaintanceDto>> result =
	                ResultDto.of("success", emptyPage);

	        when(acquaintanceService.acquaintanceList(
	                any(),
	                any(),          
	                any(Pageable.class)
	        )).thenReturn(result);

	        mockMvc.perform(get("/acquaintance")
	                        .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(status().isOk())
	                .andExpect(jsonPath("$.result").value("success"));
	    }


	    
	    @Test
	    @DisplayName("GET /acquaintance 실패 - status 400")
	    @WithMockUser(username = "test@test.com", roles = "USER")
	    void acquaintanceList_fail() throws Exception {

	        ResultDto<Page<AcquaintanceDto>> result =
	                ResultDto.of("Error", null);

	        when(acquaintanceService.acquaintanceList(any(), eq("검색어"), any(Pageable.class)))
	                .thenReturn(result);

	        mockMvc.perform(get("/acquaintance")
	                        .param("keyword", "검색어")
	                        .contentType(MediaType.APPLICATION_JSON))
	                .andExpect(status().isBadRequest())
	                .andExpect(jsonPath("$.result").value("Error"));
	    }
	
}
