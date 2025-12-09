package com.planitsquare.holidaykeeper.dto;

import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.entity.Holiday;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayResponse {

    private LocalDate date;
    private String localName;
    private String name;
    private String countryCode;
    private Boolean fixed;
    private Boolean global;
    private List<String> counties;
    private Integer launchYear;
    private List<String> types;

    public Holiday toEntity(Country country) {
        String typesString = types != null && !types.isEmpty() 
                ? String.join(",", types) 
                : null;
        
        String countiesString = counties != null && !counties.isEmpty()
                ? String.join(",", counties)
                : null;
        
        return Holiday.of(
                country,
                date,
                localName,
                name,
                fixed,
                global,
                countiesString,
                launchYear,
                typesString
        );
    }

    public String getTypesAsString() {
        return types != null && !types.isEmpty() 
                ? String.join(",", types) 
                : null;
    }

    public String getCounties() {
        return counties != null && !counties.isEmpty()
                ? String.join(",", counties)
                : null;
    }
}
