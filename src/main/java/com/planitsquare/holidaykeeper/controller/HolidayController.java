package com.planitsquare.holidaykeeper.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.planitsquare.holidaykeeper.dto.HolidayDto;
import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.dto.PageResponse;
import com.planitsquare.holidaykeeper.service.HolidayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Holiday API", description = "공휴일 관리 API")
@RestController
@RequestMapping("/api/holidays")
@RequiredArgsConstructor
public class HolidayController {

	private final HolidayService holidayService;

	@Operation(summary = "공휴일 검색", description = "연도, 국가, 기간, 타입 등으로 공휴일을 검색합니다.")
	@PostMapping("/search")
	public ResponseEntity<PageResponse<HolidayDto>> searchHolidays(
		@RequestBody HolidaySearchRequest request,
		@Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size
	) {

		Pageable pageable = PageRequest.of(page, size);
		PageResponse<HolidayDto> response = holidayService.searchHolidays(request, pageable);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "공휴일 재동기화", description = "특정 연도와 국가의 공휴일 데이터를 외부 API에서 다시 가져와 Upsert합니다.")
	@PostMapping("/refresh")
	public ResponseEntity<String> refreshHolidays(
		@Parameter(description = "연도", example = "2025") @RequestParam int year,
		@Parameter(description = "국가 코드", example = "KR") @RequestParam String countryCode
	) {

		holidayService.refreshHolidays(year, countryCode);
		return ResponseEntity.ok("덮어쓰기 성공");
	}

	@Operation(summary = "공휴일 삭제", description = "특정 연도와 국가의 공휴일 데이터를 삭제합니다.")
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteHolidays(
		@Parameter(description = "연도", example = "2025") @RequestParam int year,
		@Parameter(description = "국가 코드", example = "KR") @RequestParam String countryCode
	) {

		holidayService.deleteHolidays(year, countryCode);
		return ResponseEntity.ok("삭제 성공");
	}

}
