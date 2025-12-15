package springboot.giftledger.analysis.controller;

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

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard(
            @AuthenticationPrincipal String email
    ) {
        DashboardDto dashboardDto = analysisService.getDashboard(email);
        return ResponseEntity.ok(dashboardDto);
    }

    @GetMapping("/recent-events")
    public ResponseEntity<List<RecentEventDto>> getRecentEvents(
            @AuthenticationPrincipal String email) {

        List<RecentEventDto> events = analysisService.getRecentEvents(email);
        return ResponseEntity.ok(events);
    }


    @GetMapping("/pattern")
    public ResponseEntity<PatternDto> getPattern(
            @AuthenticationPrincipal String email,
            @RequestParam(required = false) Integer year) {  // year 파라미터 추가

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        PatternDto pattern = analysisService.getPattern(email, year);
        return ResponseEntity.ok(pattern);
    }

    @GetMapping("/relation")
    public ResponseEntity<RelationDto> getRelation(
            @AuthenticationPrincipal String email) {

        RelationDto relation = analysisService.getRelation(email);
        return ResponseEntity.ok(relation);
    }

    @GetMapping("/recovery")
    public ResponseEntity<RecoveryDto> getRecovery(
            @AuthenticationPrincipal String email) {

        RecoveryDto recovery = analysisService.getRecovery(email);
        return ResponseEntity.ok(recovery);
    }
}
