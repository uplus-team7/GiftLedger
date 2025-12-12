package springboot.giftledger.analysis.service;

import springboot.giftledger.analysis.dto.DashboardDto;
import springboot.giftledger.analysis.dto.PatternDto;
import springboot.giftledger.analysis.dto.RecentEventDto;
import springboot.giftledger.analysis.dto.RelationDto;

import java.util.List;

public interface AnalysisService {

    DashboardDto getDashboard(String email);
    List<RecentEventDto> getRecentEvents(String email);

    PatternDto getPattern(String email, int year);

    RelationDto getRelation(String email);
}
