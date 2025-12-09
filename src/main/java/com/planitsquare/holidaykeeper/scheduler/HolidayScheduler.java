package com.planitsquare.holidaykeeper.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.repository.CountryRepository;
import com.planitsquare.holidaykeeper.service.HolidayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HolidayScheduler {

	private final HolidayService holidayService;
	private final CountryRepository countryRepository;

	/*
	 매년 1월 2일 01:00  전년도·금년도 공휴일 데이터 자동 동기화
	 */
	@Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
	public void syncHolidays() {

		log.info("=== 공휴일 자동 동기화 시작 ===");

		int currentYear = LocalDate.now().getYear(); // 금년도
		int previousYear = currentYear - 1;  //전년도

		log.info("동기화 대상: {}년, {}년", previousYear, currentYear);

		List<Country> countries = countryRepository.findAll();
		log.info("동기화 대상 국가: {}개", countries.size());

		int totalSynced = 0;

		for (Country country : countries) {
			try {
				// 전년도 동기화
				holidayService.refreshHolidays(previousYear, country.getCountryCode());

				// 금년도 동기화
				holidayService.refreshHolidays(currentYear, country.getCountryCode());

				totalSynced++;
			} catch (Exception e) {
				log.error("{} 동기화 실패: {}", country.getCountryCode(), e.getMessage());
			}
		}

		log.info("=== 공휴일 자동 동기화 완료: {}개 국가 ===", totalSynced);
	}

	/*
	  테스트용: 1분마다 실행
	 */
	//@Scheduled(cron = "0 */1 * * * ?", zone = "Asia/Seoul")
	public void syncHolidaysTest() {

		log.info("=== 테스트: 공휴일 동기화 실행 ===");

		int currentYear = LocalDate.now().getYear();
		List<Country> countries = countryRepository.findAll();

		// 테스트: 한국만 동기화
		Country korea = countries.stream()
			.filter(c -> c.getCountryCode().equals("KR"))
			.findFirst()
			.orElse(null);

		if (korea != null) {
			holidayService.refreshHolidays(currentYear, korea.getCountryCode());
			log.info("테스트 동기화 완료: KR {}년", currentYear);
		}
	}

}
