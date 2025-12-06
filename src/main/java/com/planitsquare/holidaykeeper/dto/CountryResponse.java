package com.planitsquare.holidaykeeper.dto;


import com.planitsquare.holidaykeeper.entity.Country;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponse {

    private String countryCode;
    private String name;

    public Country toEntity() {
        return Country.of(countryCode, name);
    }
}
