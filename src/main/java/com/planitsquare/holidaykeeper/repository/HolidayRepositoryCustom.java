package com.planitsquare.holidaykeeper.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.planitsquare.holidaykeeper.dto.request.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.entity.Holiday;

public interface HolidayRepositoryCustom {

	Page<Holiday> searchHolidays(HolidaySearchRequest request, Pageable pageable);

}
