package com.MyDrama.service;

import com.MyDrama.entity.VisitorCount;
import com.MyDrama.repository.VisitorCountRepository;
import com.MyDrama.dto.VisitorCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class VisitorService {

    private final VisitorCountRepository visitorCountRepository;

    // 방문자 수 증가
    public void incrementVisitorCount() {
        LocalDate today = LocalDate.now();
        VisitorCount visitorCount = visitorCountRepository.findByVisitDate(today)
                .orElse(new VisitorCount());

        if (visitorCount.getVisitDate() == null) {
            visitorCount.setVisitDate(today);
            visitorCount.setDailyCount(0);
        }

        visitorCount.setDailyCount(visitorCount.getDailyCount() + 1);
        visitorCount.setTotalCount(getTotalVisitorCount() + 1);

        visitorCountRepository.save(visitorCount);
    }

    // 오늘 방문자 수 조회
    public int getTodayVisitorCount() {
        return visitorCountRepository.findByVisitDate(LocalDate.now())
                .map(VisitorCount::getDailyCount)
                .orElse(0);
    }

    // 전체 방문자 수 조회
    public long getTotalVisitorCount() {
        return visitorCountRepository.findAll().stream()
                .mapToLong(VisitorCount::getDailyCount)
                .sum();
    }

    // 월별 방문자 수 통계 조회
    public Map<String, Long> getMonthlyStats() {
        Map<String, Long> monthlyStats = new LinkedHashMap<>();

        visitorCountRepository.findMonthlyStats().forEach(result -> {
            String month = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            monthlyStats.put(month, count);
        });

        return monthlyStats;
    }

    // 일별 방문자 수 통계 조회
    public Map<String, Long> getDailyStats() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30); // 최근 30일 데이터

        Map<String, Long> dailyStats = new LinkedHashMap<>();

        visitorCountRepository.findDailyStats(startDate, endDate).forEach(result -> {
            LocalDate date = (LocalDate) result[0];
            Long count = ((Number) result[1]).longValue();
            dailyStats.put(date.toString(), count);
        });

        return dailyStats;
    }

    // 최근 7일 방문자 수 조회
    public Map<String, Long> getRecentDailyStats() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6); // 최근 7일

        Map<String, Long> dailyStats = new LinkedHashMap<>();
        List<VisitorCount> visitors = visitorCountRepository.findVisitorStatsByDateRange(startDate, today);

        // 모든 날짜 초기화 (방문자가 없는 날도 포함)
        for (int i = 0; i <= 6; i++) {
            LocalDate date = today.minusDays(i);
            dailyStats.put(date.toString(), 0L);
        }

        // 실제 방문자 수 데이터 반영
        visitors.forEach(visitor ->
                dailyStats.put(visitor.getVisitDate().toString(), (long) visitor.getDailyCount())
        );

        return dailyStats;
    }
}