package com.planitsquare.holidaykeeper.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.planitsquare.holidaykeeper.dto.HolidayDto;
import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.dto.PageResponse;
import com.planitsquare.holidaykeeper.service.HolidayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

	private final HolidayService holidayService;

	//과제 2번
	@GetMapping
	public ResponseEntity<PageResponse<HolidayDto>> searchHolidays(
		@ModelAttribute HolidaySearchRequest request,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size
	) {

		Pageable pageable = PageRequest.of(page, size);
		PageResponse<HolidayDto> response = holidayService.searchHolidays(request, pageable);

		return ResponseEntity.ok(response);
	}

	//과제 3번
	@PostMapping("/refresh")
	public ResponseEntity<String> refreshHolidays(
		@RequestParam int year,
		@RequestParam String countryCode
	) {

		holidayService.refreshHolidays(year, countryCode);
		return ResponseEntity.ok("덮어쓰기 성공");
	}

	//과제 4번
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteHolidays(
		@RequestParam int year,
		@RequestParam String countryCode
	) {

		holidayService.deleteHolidays(year, countryCode);
		return ResponseEntity.ok("삭제 성공");
	}

}
