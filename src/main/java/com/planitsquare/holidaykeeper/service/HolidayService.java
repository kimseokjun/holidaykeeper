package com.planitsquare.holidaykeeper.service;

import com.planitsquare.holidaykeeper.client.NagerDateClient;
import com.planitsquare.holidaykeeper.dto.CountryResponse;
import com.planitsquare.holidaykeeper.dto.HolidayDto;
import com.planitsquare.holidaykeeper.dto.HolidayResponse;
import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.dto.PageResponse;
import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.entity.Holiday;
import com.planitsquare.holidaykeeper.repository.CountryRepository;
import com.planitsquare.holidaykeeper.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public void loadInitialData() {
        log.info("Starting initial data load...");

        saveCountries();

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

    @Transactional(readOnly = true)
    public PageResponse<HolidayDto> searchHolidays(HolidaySearchRequest request, Pageable pageable) {
        Page<Holiday> holidayPage = holidayRepository.searchHolidays(request, pageable);
        Page<HolidayDto> dtoPage = holidayPage.map(HolidayDto::from);
        
        return PageResponse.of(dtoPage);
    }
}
