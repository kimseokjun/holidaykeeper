package com.planitsquare.holidaykeeper.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Slf4j
@Service
@RequiredArgsConstructor
public class HolidayService {

	private final NagerDateClient nagerDateClient;
	private final CountryRepository countryRepository;
	private final HolidayRepository holidayRepository;

	public void loadInitialData() {

		log.info("초기 데이터 적재 시작");

		saveCountries();

		List<Country> countries = countryRepository.findAll();
		int totalHolidays = 0;

		for (Country country : countries) {
			for (int year = 2020; year <= 2025; year++) {
				int saved = loadHolidaysForCountryAndYear(country, year);
				totalHolidays += saved;
			}
		}

		log.info("초기 데이터 적재 완료. 총 {}개 공휴일 저장", totalHolidays);
	}

	@Transactional
	public void saveCountries() {

		List<CountryResponse> countryResponses = nagerDateClient.getAvailableCountries();
		log.info("{}개 국가 조회", countryResponses.size());

		List<Country> countries = countryResponses.stream()
			.map(CountryResponse::toEntity)
			.toList();

		countryRepository.saveAll(countries);
		log.info("{}개 국가 저장 완료", countries.size());
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
			log.info("{} {}년 공휴일 {}개 저장", country.getCountryCode(), year, holidays.size());

			return holidays.size();
		} catch (Exception e) {
			log.warn("{} {}년 공휴일 조회 실패: {}", country.getCountryCode(), year, e.getMessage());
			return 0;
		}
	}

	@Transactional(readOnly = true)
	public PageResponse<HolidayDto> searchHolidays(HolidaySearchRequest request, Pageable pageable) {

		Page<Holiday> holidayPage = holidayRepository.searchHolidays(request, pageable);
		Page<HolidayDto> dtoPage = holidayPage.map(HolidayDto::from);

		return PageResponse.of(dtoPage);
	}

	@Transactional
	public int refreshHolidays(int year, String countryCode) {
		Country country = countryRepository.findById(countryCode)
			.orElseThrow(() -> new IllegalArgumentException("국가를 찾을 수 없습니다: " + countryCode));

		List<HolidayResponse> apiHolidays = nagerDateClient.getPublicHolidays(year, countryCode);
		if (apiHolidays == null || apiHolidays.isEmpty()) {
			log.info("{} {}년 공휴일 데이터 없음", countryCode, year);
			return 0;
		}

		List<Holiday> existingHolidays = holidayRepository.findByCountryAndYear(country, year);
		
		int insertCount = 0;
		int updateCount = 0;

		for (HolidayResponse apiHoliday : apiHolidays) {
			Holiday existing = existingHolidays.stream()
				.filter(h -> h.getDate().equals(apiHoliday.getDate()) 
						&& h.getName().equals(apiHoliday.getName()))
				.findFirst()
				.orElse(null);

			if (existing != null) {
				updateHoliday(existing, apiHoliday);
				updateCount++;
			} else {
				holidayRepository.save(apiHoliday.toEntity(country));
				insertCount++;
			}
		}

		List<Holiday> toDelete = existingHolidays.stream()
			.filter(existing -> apiHolidays.stream()
				.noneMatch(api -> api.getDate().equals(existing.getDate()) 
						&& api.getName().equals(existing.getName())))
			.toList();
		
		holidayRepository.deleteAll(toDelete);
		int deleteCount = toDelete.size();

		log.info("{} {}년 재동기화 완료 - 추가: {}, 수정: {}, 삭제: {}", 
			countryCode, year, insertCount, updateCount, deleteCount);

		return insertCount + updateCount;
	}

	private void updateHoliday(Holiday existing, HolidayResponse apiData) {
		existing.update(
			apiData.getLocalName(),
			apiData.getFixed(),
			apiData.getGlobal(),
			apiData.getCounties(),
			apiData.getLaunchYear(),
			apiData.getTypesAsString()
		);
	}

}
