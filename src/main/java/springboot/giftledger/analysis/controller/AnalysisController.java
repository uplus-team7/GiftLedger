package springboot.giftledger.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springboot.giftledger.analysis.dto.*;
import springboot.giftledger.analysis.service.AnalysisService;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "분석", description = "축의금 분석 API")
@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @Operation(summary = "대시보드 조회", description = "사용자의 종합 대시보드 정보를 조회합니다")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard(
            @AuthenticationPrincipal String email
    ) {
        DashboardDto dashboardDto = analysisService.getDashboard(email);
        return ResponseEntity.ok(dashboardDto);
    }

    @Operation(summary = "최근 이벤트 조회", description = "최근 5개의 이벤트를 조회합니다")
    @GetMapping("/recent-events")
    public ResponseEntity<List<RecentEventDto>> getRecentEvents(
            @AuthenticationPrincipal String email) {

        List<RecentEventDto> events = analysisService.getRecentEvents(email);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "지출 패턴 분석", description = "월별/요일별/이벤트별 지출 패턴을 분석합니다")
    @GetMapping("/pattern")
    public ResponseEntity<PatternDto> getPattern(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year) {

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        PatternDto pattern = analysisService.getPattern(email, year);
        return ResponseEntity.ok(pattern);
    }

    @Operation(summary = "지인 분석", description = "TOP 5 지인 및 지인별 회수율을 조회합니다")
    @GetMapping("/relation")
    public ResponseEntity<RelationDto> getRelation(
            @AuthenticationPrincipal String email) {

        RelationDto relation = analysisService.getRelation(email);
        return ResponseEntity.ok(relation);
    }

    @Operation(summary = "회수율 대시보드", description = "전체 회수율 및 미회수 지인 정보를 조회합니다")
    @GetMapping("/recovery")
    public ResponseEntity<RecoveryDto> getRecovery(
            @AuthenticationPrincipal String email) {

        RecoveryDto recovery = analysisService.getRecovery(email);
        return ResponseEntity.ok(recovery);
    }
}