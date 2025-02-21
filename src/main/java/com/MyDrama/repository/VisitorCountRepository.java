package com.MyDrama.repository;

import com.MyDrama.entity.VisitorCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VisitorCountRepository extends JpaRepository<VisitorCount, Long> {
     // 특정 날짜의 방문자 수 조회
     Optional<VisitorCount> findByVisitDate(LocalDate date);

     // 방문자 수가 가장 많은 날짜 조회
     Optional<VisitorCount> findTopByOrderByTotalCountDesc();
 
     // 특정 날짜 범위의 방문자 수 통계 조회
     @Query("SELECT v FROM VisitorCount v WHERE v.visitDate >= :startDate AND v.visitDate <= :endDate ORDER BY v.visitDate")
     List<VisitorCount> findVisitorStatsByDateRange(LocalDate startDate, LocalDate endDate);
 
     // 최근 6개월 동안의 월별 방문자 수 합계
     @Query("SELECT FUNCTION('DATE_FORMAT', v.visitDate, '%Y-%m') as month, SUM(v.dailyCount) as total " +
             "FROM VisitorCount v " +
             "GROUP BY FUNCTION('DATE_FORMAT', v.visitDate, '%Y-%m') " +
             "ORDER BY month DESC " +
             "LIMIT 6")
     List<Object[]> findMonthlyStats();
 
     // 특정 기간의 일별 방문자 수 통계 조회
     @Query("SELECT v.visitDate as date, v.dailyCount as count " +
             "FROM VisitorCount v " +
             "WHERE v.visitDate >= :startDate AND v.visitDate <= :endDate " +
             "ORDER BY v.visitDate DESC")
     List<Object[]> findDailyStats(LocalDate startDate, LocalDate endDate);
 }