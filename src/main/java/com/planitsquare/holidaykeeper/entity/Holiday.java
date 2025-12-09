package com.planitsquare.holidaykeeper.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "holidays",
	indexes = {
		@Index(name = "idx_country_date", columnList = "country_code, date"),
		@Index(name = "idx_date", columnList = "date")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uk_country_date_name", columnNames = {"country_code", "date", "name", "counties"})
	})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Holiday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "country_code", nullable = false)
	private Country country;

	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	private String localName; // 새해, 설날 등

	@Column(nullable = false)
	private String name; // New Year's Day 등

	@Column
	private Boolean fixed;

	@Column
	private Boolean global;

	@Column
	private String counties; // 특정 지역만 해당하는 경우

	@Column
	private Integer launchYear;

	@Column
	private String types; // ["Public"] -> "Public" 형태로 저장

	public static Holiday of(Country country, LocalDate date, String localName,
		String name, Boolean fixed, Boolean global,
		String counties, Integer launchYear, String types) {

		return Holiday.builder()
			.country(country)
			.date(date)
			.localName(localName)
			.name(name)
			.fixed(fixed)
			.global(global)
			.counties(counties)
			.launchYear(launchYear)
			.types(types)
			.build();
	}

	public void update(String localName, Boolean fixed, Boolean global,
		String counties, Integer launchYear, String types) {

		this.localName = localName;
		this.fixed = fixed;
		this.global = global;
		this.counties = counties;
		this.launchYear = launchYear;
		this.types = types;
	}

}
