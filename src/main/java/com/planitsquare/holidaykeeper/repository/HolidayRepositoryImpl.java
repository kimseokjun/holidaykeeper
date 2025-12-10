package com.planitsquare.holidaykeeper.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.planitsquare.holidaykeeper.dto.request.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.entity.Holiday;
import com.planitsquare.holidaykeeper.entity.QHoliday;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Holiday> searchHolidays(HolidaySearchRequest request, Pageable pageable) {

		QHoliday holiday = QHoliday.holiday;

		BooleanBuilder builder = new BooleanBuilder();

		//Year가 존재하면
		if (request.getYear() != null) {
			LocalDate startOfYear = LocalDate.of(request.getYear(), 1, 1);
			LocalDate endOfYear = LocalDate.of(request.getYear(), 12, 31);
			builder.and(holiday.date.between(startOfYear, endOfYear));
		}

		// CountryCode가 존재하면
		if (request.getCountryCode() != null && !request.getCountryCode().isBlank()) {
			builder.and(holiday.country.countryCode.eq(request.getCountryCode()));
		}

		// --이후에도 계속 있으면 빌더 만들고.. 반복
		if (request.getFrom() != null) {
			builder.and(holiday.date.goe(request.getFrom()));
		}
		if (request.getTo() != null) {
			builder.and(holiday.date.loe(request.getTo()));
		}

		if (request.getType() != null && !request.getType().isBlank()) {
			builder.and(holiday.types.contains(request.getType()));
		}

		if (request.getFixed() != null) {
			builder.and(holiday.fixed.eq(request.getFixed()));
		}

		if (request.getGlobal() != null) {
			builder.and(holiday.global.eq(request.getGlobal()));
		}

		List<Holiday> content = queryFactory
			.selectFrom(holiday)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(holiday.date.asc())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(holiday.count())
			.from(holiday)
			.where(builder);

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

}
