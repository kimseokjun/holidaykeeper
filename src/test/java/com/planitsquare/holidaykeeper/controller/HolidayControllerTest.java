package com.planitsquare.holidaykeeper.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.planitsquare.holidaykeeper.dto.HolidayDto;
import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.dto.PageResponse;
import com.planitsquare.holidaykeeper.service.HolidayService;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private HolidayService holidayService;

	@Test
	@DisplayName("GET /api/holidays - 연도와 국가로 검색")
	void searchHolidaysByYearAndCountry() throws Exception {

		HolidayDto holiday1 = HolidayDto.builder()
			.id(1L)
			.countryCode("KR")
			.countryName("South Korea")
			.date(LocalDate.of(2025, 1, 1))
			.localName("신정")
			.name("New Year's Day")
			.fixed(true)
			.global(true)
			.types("Public")
			.build();

		HolidayDto holiday2 = HolidayDto.builder()
			.id(2L)
			.countryCode("KR")
			.countryName("South Korea")
			.date(LocalDate.of(2025, 3, 1))
			.localName("삼일절")
			.name("Independence Movement Day")
			.fixed(true)
			.global(true)
			.types("Public")
			.build();

		PageResponse<HolidayDto> response = PageResponse.<HolidayDto>builder()
			.content(List.of(holiday1, holiday2))
			.page(0)
			.size(20)
			.totalElements(2)
			.totalPages(1)
			.first(true)
			.last(true)
			.build();

		when(holidayService.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
			.thenReturn(response);

		mockMvc.perform(get("/api/holidays")
				.param("year", "2025")
				.param("countryCode", "KR"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content.length()").value(2))
			.andExpect(jsonPath("$.content[0].countryCode").value("KR"))
			.andExpect(jsonPath("$.totalElements").value(2))
			.andExpect(jsonPath("$.page").value(0));
	}

	@Test
	@DisplayName("GET /api/holidays - 페이징 파라미터 적용")
	void searchHolidaysWithPagination() throws Exception {

		HolidayDto holiday = HolidayDto.builder()
			.id(1L)
			.countryCode("KR")
			.countryName("South Korea")
			.date(LocalDate.of(2025, 1, 1))
			.localName("신정")
			.name("New Year's Day")
			.fixed(true)
			.global(true)
			.types("Public")
			.build();

		PageResponse<HolidayDto> response = PageResponse.<HolidayDto>builder()
			.content(List.of(holiday))
			.page(0)
			.size(1)
			.totalElements(2)
			.totalPages(2)
			.first(true)
			.last(false)
			.build();

		when(holidayService.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
			.thenReturn(response);

		mockMvc.perform(get("/api/holidays")
				.param("year", "2025")
				.param("countryCode", "KR")
				.param("page", "0")
				.param("size", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.size").value(1))
			.andExpect(jsonPath("$.totalElements").value(2))
			.andExpect(jsonPath("$.totalPages").value(2));
	}

	@Test
	@DisplayName("GET /api/holidays - 기간 필터")
	void searchHolidaysByDateRange() throws Exception {

		HolidayDto holiday = HolidayDto.builder()
			.id(1L)
			.countryCode("KR")
			.countryName("South Korea")
			.date(LocalDate.of(2025, 1, 1))
			.localName("신정")
			.name("New Year's Day")
			.fixed(true)
			.global(true)
			.types("Public")
			.build();

		PageResponse<HolidayDto> response = PageResponse.<HolidayDto>builder()
			.content(List.of(holiday))
			.page(0)
			.size(20)
			.totalElements(1)
			.totalPages(1)
			.first(true)
			.last(true)
			.build();

		when(holidayService.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
			.thenReturn(response);

		mockMvc.perform(get("/api/holidays")
				.param("year", "2025")
				.param("countryCode", "KR")
				.param("from", "2025-01-01")
				.param("to", "2025-01-31"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content.length()").value(1))
			.andExpect(jsonPath("$.content[0].date").value("2025-01-01"));
	}

	@Test
	@DisplayName("GET /api/holidays - 타입 필터")
	void searchHolidaysByType() throws Exception {

		HolidayDto holiday = HolidayDto.builder()
			.id(1L)
			.countryCode("KR")
			.countryName("South Korea")
			.date(LocalDate.of(2025, 1, 1))
			.localName("신정")
			.name("New Year's Day")
			.fixed(true)
			.global(true)
			.types("Public")
			.build();

		PageResponse<HolidayDto> response = PageResponse.<HolidayDto>builder()
			.content(List.of(holiday))
			.page(0)
			.size(20)
			.totalElements(1)
			.totalPages(1)
			.first(true)
			.last(true)
			.build();

		when(holidayService.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
			.thenReturn(response);

		mockMvc.perform(get("/api/holidays")
				.param("year", "2025")
				.param("countryCode", "KR")
				.param("type", "Public"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").isArray())
			.andExpect(jsonPath("$.content[0].types").value("Public"));
	}

}
