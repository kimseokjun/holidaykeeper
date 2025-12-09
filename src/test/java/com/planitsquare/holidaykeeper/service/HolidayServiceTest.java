package com.planitsquare.holidaykeeper.service;

import com.planitsquare.holidaykeeper.client.NagerDateClient;
import com.planitsquare.holidaykeeper.dto.HolidayDto;
import com.planitsquare.holidaykeeper.dto.HolidayResponse;
import com.planitsquare.holidaykeeper.dto.HolidaySearchRequest;
import com.planitsquare.holidaykeeper.dto.PageResponse;
import com.planitsquare.holidaykeeper.entity.Country;
import com.planitsquare.holidaykeeper.entity.Holiday;
import com.planitsquare.holidaykeeper.repository.CountryRepository;
import com.planitsquare.holidaykeeper.repository.HolidayRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @Mock
    private HolidayRepository holidayRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private NagerDateClient nagerDateClient;

    @InjectMocks
    private HolidayService holidayService;

    @Test
    @DisplayName("검색 결과가 DTO로 변환되는지 확인")
    void searchHolidaysReturnsDto() {
        Country korea = Country.of("KR", "South Korea");
        Holiday holiday1 = Holiday.of(korea, LocalDate.of(2025, 1, 1), "신정", "New Year's Day", true, true, null, null, "Public");
        Holiday holiday2 = Holiday.of(korea, LocalDate.of(2025, 3, 1), "삼일절", "Independence Movement Day", true, true, null, null, "Public");
        
        Page<Holiday> holidayPage = new PageImpl<>(List.of(holiday1, holiday2));
        
        when(holidayRepository.searchHolidays(any(HolidaySearchRequest.class), any(PageRequest.class)))
                .thenReturn(holidayPage);

        HolidaySearchRequest request = HolidaySearchRequest.builder()
                .year(2025)
                .countryCode("KR")
                .build();

        PageResponse<HolidayDto> response = holidayService.searchHolidays(request, PageRequest.of(0, 10));

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0)).isInstanceOf(HolidayDto.class);
        assertThat(response.getContent().get(0).getCountryCode()).isEqualTo("KR");
        assertThat(response.getContent().get(0).getCountryName()).isEqualTo("South Korea");
    }

    @Test
    @DisplayName("재동기화 - Upsert 동작 확인")
    void refreshHolidays_upsert() {
        Country korea = Country.of("KR", "South Korea");
        Holiday existing = Holiday.of(korea, LocalDate.of(2025, 1, 1), "구정", "New Year's Day", true, true, null, null, "Public");
        
        when(countryRepository.findById("KR")).thenReturn(Optional.of(korea));
        when(holidayRepository.findByCountryAndYear(korea, 2025)).thenReturn(List.of(existing));
        
        List<HolidayResponse> apiHolidays = List.of(
                new HolidayResponse(LocalDate.of(2025, 1, 1), "신정", "New Year's Day", "KR", true, true, null, null, List.of("Public")),
                new HolidayResponse(LocalDate.of(2025, 3, 1), "삼일절", "Independence Movement Day", "KR", true, true, null, null, List.of("Public"))
        );
        when(nagerDateClient.getPublicHolidays(2025, "KR")).thenReturn(apiHolidays);

        int result = holidayService.refreshHolidays(2025, "KR");

        assertThat(result).isEqualTo(2);
        verify(holidayRepository, times(1)).save(any(Holiday.class));
    }

    @Test
    @DisplayName("삭제 - 특정 연도/국가 공휴일 삭제")
    void deleteHolidays() {
        Country korea = Country.of("KR", "South Korea");
        
        when(countryRepository.findById("KR")).thenReturn(Optional.of(korea));

        holidayService.deleteHolidays(2025, "KR");

        verify(holidayRepository, times(1)).deleteByCountryAndYear(korea, 2025);
    }
}
