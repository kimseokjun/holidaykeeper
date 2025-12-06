package com.planitsquare.holidaykeeper.config;

import com.planitsquare.holidaykeeper.service.HolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final HolidayService holidayService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("========== Starting initial data load ==========");
        holidayService.loadInitialData();
        log.info("========== Initial data load completed ==========");
    }
}
