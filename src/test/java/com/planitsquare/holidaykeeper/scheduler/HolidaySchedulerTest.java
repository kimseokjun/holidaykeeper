package com.planitsquare.holidaykeeper.scheduler;

import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.repository.CountryRepository;
import com.planitsquare.holidaykeeper.service.HolidayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidaySchedulerTest {

    @Mock
    private HolidayService holidayService;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private HolidayScheduler holidayScheduler;

    @Test
    @DisplayName("배치 스케줄러 - 공휴일 동기화 실행")
    void syncHolidaysTest() {
        Country korea = Country.of("KR", "South Korea");
        
        when(countryRepository.findAll()).thenReturn(List.of(korea));
        when(holidayService.refreshHolidays(anyInt(), eq("KR"))).thenReturn(15);

        holidayScheduler.syncHolidaysTest();

        verify(holidayService, times(1)).refreshHolidays(LocalDate.now().getYear(), "KR");
    }
}
