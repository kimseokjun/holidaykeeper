package com.planitsquare.holidaykeeper.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.planitsquare.holidaykeeper.dto.request.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.entity.Holiday;

@DataJpaTest
@Import({com.planitsquare.holidaykeeper.config.QuerydslConfig.class, HolidayRepositoryImpl.class})
class HolidayRepositoryTest {

	@Autowired
	private HolidayRepository holidayRepository;

	@Autowired
	private CountryRepository countryRepository;

	private Country korea;
	private Country usa;

	@BeforeEach
	void setUp() {

		korea = countryRepository.save(Country.of("KR", "South Korea"));
		usa = countryRepository.save(Country.of("US", "United States"));

		holidayRepository.save(
			Holiday.of(korea, LocalDate.of(2025, 1, 1), "신정", "New Year's Day", true, true, null, null, "Public"));
		holidayRepository.save(
			Holiday.of(korea, LocalDate.of(2025, 3, 1), "삼일절", "Independence Movement Day", true, true, null, null,
				"Public"));
		holidayRepository.save(
			Holiday.of(korea, LocalDate.of(2025, 8, 15), "광복절", "Liberation Day", true, true, null, null, "Public"));

		holidayRepository.save(
			Holiday.of(usa, LocalDate.of(2025, 1, 1), "New Year's Day", "New Year's Day", true, true, null, null,
				"Public"));
		holidayRepository.save(
			Holiday.of(usa, LocalDate.of(2025, 7, 4), "Independence Day", "Independence Day", true, true, null, null,
				"Public"));
	}

	@Test
	@DisplayName("연도와 국가로 검색")
	void searchByYearAndCountry() {
		//2025, KR이 요청으로 들어옴
		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.build();

		Page<Holiday> result = holidayRepository.searchHolidays(request, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent()).allMatch(h -> h.getCountry().getCountryCode().equals("KR"));
	}

	@Test
	@DisplayName("기간 필터 검색")
	void searchByDateRange() {

		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.from(LocalDate.of(2025, 1, 1))
			.to(LocalDate.of(2025, 3, 31))
			.build();

		Page<Holiday> result = holidayRepository.searchHolidays(request, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(2);
	}

	@Test
	@DisplayName("타입 필터 검색")
	void searchByType() {

		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.type("Public")
			.build();

		Page<Holiday> result = holidayRepository.searchHolidays(request, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent()).allMatch(h -> h.getTypes().contains("Public"));
	}

	@Test
	@DisplayName("고정 공휴일 필터 검색")
	void searchByFixed() {

		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.fixed(true)
			.build();

		Page<Holiday> result = holidayRepository.searchHolidays(request, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getContent()).allMatch(h -> h.getFixed() == true);
	}

	@Test
	@DisplayName("페이징 테스트")
	void testPagination() {

		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.build();

		Page<Holiday> result = holidayRepository.searchHolidays(request, PageRequest.of(0, 2));

		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(3);
		assertThat(result.getTotalPages()).isEqualTo(2);
		assertThat(result.isFirst()).isTrue();
		assertThat(result.isLast()).isFalse();
	}

	@Test
	@DisplayName("조건 없이 검색")
	void searchWithoutConditions() {

		HolidaySearchRequest request = HolidaySearchRequest.builder().build();

		Page<Holiday> result = holidayRepository.searchHolidays(request, PageRequest.of(0, 10));

		assertThat(result.getContent()).hasSize(5);
	}

}
