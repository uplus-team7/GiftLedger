package springboot.giftledger.analysis.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import springboot.giftledger.analysis.dto.DashboardDto;
import springboot.giftledger.analysis.dto.PatternDto;
import springboot.giftledger.analysis.dto.RecoveryDto;
import springboot.giftledger.analysis.dto.RelationDto;
import springboot.giftledger.analysis.service.AnalysisService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@WebMvcTest(AnalysisController.class)
@WithMockUser(username = "user1@test.com")
@DisplayName("Analysis Controller - 200 OK 전용 테스트")
class AnalysisControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AnalysisService analysisService;

    @Test
    @DisplayName("대시보드 API - 200 OK")
    void dashboard_ok() throws Exception {
        log.info("========== [START] Dashboard API Test ==========");
        given(analysisService.getDashboard(anyString())).willReturn(DashboardDto.builder().build());
        log.info("→ GET /analysis/dashboard 요청");
        mockMvc.perform(get("/analysis/dashboard")).andExpect(status().isOk());
        log.info("✓ Dashboard API 200 OK");
        log.info("========== [END] Dashboard API Test ==========\n");
    }

    @Test
    @DisplayName("패턴 API - 200 OK")
    void pattern_ok() throws Exception {
        log.info("========== [START] Pattern API Test ==========");
        given(analysisService.getPattern(anyString(), anyInt())).willReturn(PatternDto.builder().build());
        log.info("→ GET /analysis/pattern?year=2024 요청");
        mockMvc.perform(get("/analysis/pattern").param("year", "2024")).andExpect(status().isOk());
        log.info("✓ Pattern API 200 OK");
        log.info("========== [END] Pattern API Test ==========\n");
    }

    @Test
    @DisplayName("지인 분석 API - 200 OK")
    void relation_ok() throws Exception {
        log.info("========== [START] Relation API Test ==========");
        given(analysisService.getRelation(anyString())).willReturn(RelationDto.builder().build());
        log.info("→ GET /analysis/relation 요청");
        mockMvc.perform(get("/analysis/relation")).andExpect(status().isOk());
        log.info("✓ Relation API 200 OK");
        log.info("========== [END] Relation API Test ==========\n");
    }

    @Test
    @DisplayName("회수율 API - 200 OK")
    void recovery_ok() throws Exception {
        log.info("========== [START] Recovery API Test ==========");
        given(analysisService.getRecovery(anyString())).willReturn(RecoveryDto.builder().build());
        log.info("→ GET /analysis/recovery 요청");
        mockMvc.perform(get("/analysis/recovery")).andExpect(status().isOk());
        log.info("✓ Recovery API 200 OK");
        log.info("========== [END] Recovery API Test ==========\n");
    }
}
