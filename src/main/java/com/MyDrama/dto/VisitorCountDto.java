package com.MyDrama.dto;

import com.MyDrama.entity.VisitorCount;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;

@Getter
@Setter
public class VisitorCountDto {
    private Long id;
    private LocalDate visitDate;
    private int dailyCount;
    private long totalCount;

    private static ModelMapper modelMapper = new ModelMapper();

    public static VisitorCountDto of(VisitorCount visitorCount) {
        return modelMapper.map(visitorCount, VisitorCountDto.class);
    }

    public VisitorCount toEntity() {
        return modelMapper.map(this, VisitorCount.class);
    }
}
