package com.planitsquare.holidaykeeper.service;

import com.planitsquare.holidaykeeper.client.NagerDateClient;
import com.planitsquare.holidaykeeper.dto.CountryResponse;
import com.planitsquare.holidaykeeper.dto.HolidayResponse;
import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.entity.Holiday;
import com.planitsquare.holidaykeeper.repository.CountryRepository;
import com.planitsquare.holidaykeeper.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

    private final NagerDateClient nagerDateClient;
    private final CountryRepository countryRepository;
    private final HolidayRepository holidayRepository;

    /**
     * 최초 데이터 적재: 2020~2025년 전체 국가 공휴일 수집
     */
    public void loadInitialData() {
        log.info("Starting initial data load...");

        // 1. 국가 목록 가져오기 및 저장
        saveCountries();

        // 2. 각 국가의 2020~2025년 공휴일 저장
        List<Country> countries = countryRepository.findAll();
        int totalHolidays = 0;
        
        for (Country country : countries) {
            for (int year = 2020; year <= 2025; year++) {
                int saved = loadHolidaysForCountryAndYear(country, year);
                totalHolidays += saved;
            }
        }

        log.info("Initial data load completed. Total holidays saved: {}", totalHolidays);
    }

    /**
     * 국가 데이터 저장
     */
    @Transactional
    public void saveCountries() {
        List<CountryResponse> countryResponses = nagerDateClient.getAvailableCountries();
        log.info("Fetched {} countries", countryResponses.size());

        List<Country> countries = countryResponses.stream()
                .map(CountryResponse::toEntity)
                .toList();

        countryRepository.saveAll(countries);
        log.info("Saved {} countries to database", countries.size());
    }

    /**
     * 특정 국가의 특정 연도 공휴일 적재 (별도 트랜잭션)
     */
    @Transactional
    public int loadHolidaysForCountryAndYear(Country country, int year) {
        try {
            List<HolidayResponse> holidayResponses = nagerDateClient.getPublicHolidays(year, country.getCountryCode());
            
            if (holidayResponses == null || holidayResponses.isEmpty()) {
                return 0;
            }

            List<Holiday> holidays = holidayResponses.stream()
                    .map(response -> response.toEntity(country))
                    .toList();
            
            holidayRepository.saveAll(holidays);
            log.info("Saved {} holidays for {} in {}", holidays.size(), country.getCountryCode(), year);
            
            return holidays.size();
        } catch (Exception e) {
            log.warn("Failed to fetch holidays for {} in {}: {}", country.getCountryCode(), year, e.getMessage());
            return 0;
        }
    }
}
