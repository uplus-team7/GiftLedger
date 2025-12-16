package springboot.giftledger.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springboot.giftledger.auth.dto.StatisticsDto;
import springboot.giftledger.auth.service.StatisticsService;

@Tag(name = "통계", description = "연령대별 평균 축의금 통계 API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "이벤트 통계 조회", description = "연령대, 이벤트 종류, 액션 타입별 평균 축의금을 조회합니다")
    @GetMapping("/event")
    public ResponseEntity<StatisticsDto> getEventStatistics(
            @RequestParam String ages,
            @RequestParam String eventType,
            @RequestParam(defaultValue = "GIVE") String actionType){
        StatisticsDto statisticsDto = statisticsService.getEventStatistics(ages, eventType, actionType);
        return ResponseEntity.ok(statisticsDto);
    }

}