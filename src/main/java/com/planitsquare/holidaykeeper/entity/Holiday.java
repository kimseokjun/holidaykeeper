package com.planitsquare.holidaykeeper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "holidays",
        indexes = {
                @Index(name = "idx_country_year", columnList = "country_code, year"),
                @Index(name = "idx_date", columnList = "date")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_country_date", columnNames = {"country_code", "date"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = false)
    private Country country;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String localName; // 새해, 설날 등

    @Column(nullable = false)
    private String name; // New Year's Day 등

    @Column
    private Boolean fixed;

    @Column
    private Boolean global;

    @Column
    private String counties; // 특정 지역만 해당하는 경우

    @Column
    private Integer launchYear;

    @Column
    private String types; // ["Public"] -> "Public" 형태로 저장

    public static Holiday of(Country country, LocalDate date, String localName,
                            String name, Boolean fixed, Boolean global, 
                            String counties, Integer launchYear, String types) {
        return Holiday.builder()
                .country(country)
                .date(date)
                .localName(localName)
                .name(name)
                .fixed(fixed)
                .global(global)
                .counties(counties)
                .launchYear(launchYear)
                .types(types)
                .build();
    }
}
