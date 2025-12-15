package springboot.giftledger.analysis.service;

import springboot.giftledger.analysis.dto.*;

import java.util.List;

public interface AnalysisService {

    DashboardDto getDashboard(String email);
    List<RecentEventDto> getRecentEvents(String email);

    PatternDto getPattern(String email, int year);

    RelationDto getRelation(String email);

    RecoveryDto getRecovery(String email);
}
