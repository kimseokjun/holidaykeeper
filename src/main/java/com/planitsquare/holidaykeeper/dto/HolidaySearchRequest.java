package com.planitsquare.holidaykeeper.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;

@Getter
public class HolidaySearchRequest {

	// 필수 조건
	private Integer year;
	private String countryCode;

	// 선택 조건
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate from;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate to;

	private String type;

	private Boolean fixed;
	private Boolean global;

}
