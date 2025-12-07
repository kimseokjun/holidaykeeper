package com.planitsquare.holidaykeeper.repository;

import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {
    
    Page<Holiday> searchHolidays(HolidaySearchRequest request, Pageable pageable);
}
