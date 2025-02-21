package com.MyDrama.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "visitor_count")
public class VisitorCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate visitDate;

    private int dailyCount;

    // 총 방문자 수
    private long totalCount;
}
