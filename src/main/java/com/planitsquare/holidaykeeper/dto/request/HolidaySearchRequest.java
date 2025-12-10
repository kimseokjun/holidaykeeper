package com.planitsquare.holidaykeeper.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공휴일 검색 요청")
public class HolidaySearchRequest {

	@Schema(description = "연도", example = "2025")
	private Integer year;

	@Schema(description = "국가 코드", example = "KR")
	private String countryCode;

	@Schema(description = "시작 날짜", example = "2025-01-01")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate from;

	@Schema(description = "종료 날짜", example = "2025-12-31")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate to;

	@Schema(description = "공휴일 타입", example = "Public")
	private String type;

	@Schema(description = "고정 공휴일 여부", example = "true")
	private Boolean fixed;

	@Schema(description = "전국 공휴일 여부", example = "true")
	private Boolean global;

}
