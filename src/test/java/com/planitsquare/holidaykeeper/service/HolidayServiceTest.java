package com.planitsquare.holidaykeeper.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.planitsquare.holidaykeeper.dto.HolidayDto;
import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.dto.PageResponse;
import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.entity.Holiday;
import com.planitsquare.holidaykeeper.repository.HolidayRepository;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

	@Mock
	private HolidayRepository holidayRepository;

	@InjectMocks
	private HolidayService holidayService;

	@Test
	@DisplayName("검색 결과가 DTO로 변환되는지 확인")
	void searchHolidaysReturnsDto() {

		Country korea = Country.of("KR", "South Korea");
		Holiday holiday1 = Holiday.of(korea, LocalDate.of(2025, 1, 1), "신정", "New Year's Day", true, true, null, null,
			"Public");
		Holiday holiday2 = Holiday.of(korea, LocalDate.of(2025, 3, 1), "삼일절", "Independence Movement Day", true, true,
			null, null, "Public");

		Page<Holiday> holidayPage = new PageImpl<>(List.of(holiday1, holiday2));

		when(holidayRepository.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
			.thenReturn(holidayPage);

		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.build();

		PageResponse<HolidayDto> response = holidayService.searchHolidays(request, PageRequest.of(0, 10));

		assertThat(response.getContent()).hasSize(2);
		assertThat(response.getContent().get(0)).isInstanceOf(HolidayDto.class);
		assertThat(response.getContent().get(0).getCountryCode()).isEqualTo("KR");
		assertThat(response.getContent().get(0).getCountryName()).isEqualTo("South Korea");
	}

	@Test
	@DisplayName("페이징 정보가 올바르게 반환되는지 확인")
	void searchHolidaysReturnsPagingInfo() {

		Country korea = Country.of("KR", "South Korea");
		Holiday holiday1 = Holiday.of(korea, LocalDate.of(2025, 1, 1), "신정", "New Year's Day", true, true, null, null,
			"Public");
		Holiday holiday2 = Holiday.of(korea, LocalDate.of(2025, 3, 1), "삼일절", "Independence Movement Day", true, true,
			null, null, "Public");
		Holiday holiday3 = Holiday.of(korea, LocalDate.of(2025, 8, 15), "광복절", "Liberation Day", true, true, null, null,
			"Public");

		PageRequest pageRequest = PageRequest.of(0, 2);
		Page<Holiday> holidayPage = new PageImpl<>(List.of(holiday1, holiday2, holiday3), pageRequest, 3);

		when(holidayRepository.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
			.thenReturn(holidayPage);

		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.build();

		PageResponse<HolidayDto> response = holidayService.searchHolidays(request, pageRequest);

		assertThat(response.getPage()).isEqualTo(0);
		assertThat(response.getSize()).isEqualTo(2);
		assertThat(response.getTotalElements()).isEqualTo(3);
		assertThat(response.getTotalPages()).isEqualTo(2);
		assertThat(response.isFirst()).isTrue();
		assertThat(response.isLast()).isFalse();
	}

}
