package com.planitsquare.holidaykeeper.dto;

import com.planitsquare.holidaykeeper.entity.Holiday;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class HolidayDto {

    private Long id;
    private String countryCode;
    private String countryName;
    private LocalDate date;
    private String localName;
    private String name;
    private Boolean fixed;
    private Boolean global;
    private String counties;
    private Integer launchYear;
    private String types;

    public static HolidayDto from(Holiday holiday) {
        return HolidayDto.builder()
                .id(holiday.getId())
                .countryCode(holiday.getCountry().getCountryCode())
                .countryName(holiday.getCountry().getName())
                .date(holiday.getDate())
                .localName(holiday.getLocalName())
                .name(holiday.getName())
                .fixed(holiday.getFixed())
                .global(holiday.getGlobal())
                .counties(holiday.getCounties())
                .launchYear(holiday.getLaunchYear())
                .types(holiday.getTypes())
                .build();
    }
}
