package com.planitsquare.holidaykeeper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Country {

    @Id
    @Column(length = 2)
    private String countryCode;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Holiday> holidays = new ArrayList<>();

    public static Country of(String countryCode, String name) {
        return Country.builder()
                .countryCode(countryCode)
                .name(name)
                .build();
    }
}
