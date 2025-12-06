package com.planitsquare.holidaykeeper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planitsquare.holidaykeeper.entity.Country;

public interface CountryRepository extends JpaRepository<Country, String> {

}
