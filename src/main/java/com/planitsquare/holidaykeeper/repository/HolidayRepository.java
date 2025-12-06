package com.planitsquare.holidaykeeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planitsquare.holidaykeeper.entity.Holiday;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

}
