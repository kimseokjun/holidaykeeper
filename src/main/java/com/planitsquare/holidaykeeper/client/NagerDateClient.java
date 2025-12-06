package com.planitsquare.holidaykeeper.client;

import com.planitsquare.holidaykeeper.dto.CountryResponse;
import com.planitsquare.holidaykeeper.dto.HolidayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NagerDateClient {

    private final RestClient restClient;

    /**
     * 모든 국가 목록 조회
     */
    public List<CountryResponse> getAvailableCountries() {

        return restClient.get()
                .uri("/AvailableCountries")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    /**
     * 특정 국가의 특정 연도 공휴일 조회
     */
    public List<HolidayResponse> getPublicHolidays(int year, String countryCode) {

        return restClient.get()
                .uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
