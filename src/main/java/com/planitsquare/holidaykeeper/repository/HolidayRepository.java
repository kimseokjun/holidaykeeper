package com.planitsquare.holidaykeeper.repository;

import com.planitsquare.holidaykeeper.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import com.planitsquare.holidaykeeper.entity.Holiday;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {

	@Query("SELECT h FROM Holiday h WHERE h.country = :country AND YEAR(h.date) = :year")
	List<Holiday> findByCountryAndYear(@Param("country") Country country, @Param("year") int year);

	@Modifying
	@Query("DELETE FROM Holiday h WHERE h.country = :country AND YEAR(h.date) = :year")
	void deleteByCountryAndYear(@Param("country") Country country, @Param("year") int year);
}
