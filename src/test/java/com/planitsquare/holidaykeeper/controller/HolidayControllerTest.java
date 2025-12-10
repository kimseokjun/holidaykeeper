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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planitsquare.holidaykeeper.dto.HolidayDto;
import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.dto.PageResponse;
import com.planitsquare.holidaykeeper.service.HolidayService;

@WebMvcTest(HolidayController.class)
class HolidayControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper; // Json으로 변환해주기 위해서 사용

	@MockitoBean
	private HolidayService holidayService;

	@Test
	@DisplayName("POST /api/holidays/search - 연도와 국가로 검색")
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

		PageResponse<HolidayDto> response = PageResponse.<HolidayDto>builder()
			.content(List.of(holiday1))
			.page(0)
			.size(20)
			.totalElements(1)
			.totalPages(1)
			.first(true)
			.last(true)
			.build();

		when(holidayService.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
			.thenReturn(response);

		HolidaySearchRequest request = HolidaySearchRequest.builder()
			.year(2025)
			.countryCode("KR")
			.build();

		mockMvc.perform(post("/api/holidays/search")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.param("page", "0")
				.param("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content[0].countryCode").value("KR"));
	}

	@Test
	@DisplayName("POST /api/holidays/refresh - 재동기화")
	void refreshHolidays() throws Exception {

		when(holidayService.refreshHolidays(2025, "KR")).thenReturn(15);

		mockMvc.perform(post("/api/holidays/refresh")
				.param("year", "2025")
				.param("countryCode", "KR"))
			.andExpect(status().isOk())
			.andExpect(content().string("덮어쓰기 성공"));
	}

}
