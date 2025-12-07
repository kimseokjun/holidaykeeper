package com.planitsquare.holidaykeeper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;        // 데이터 목록
    private int page;                // 현재 페이지 (0부터 시작)
    private int size;                // 페이지 크기
    private long totalElements;      // 전체 데이터 개수
    private int totalPages;          // 전체 페이지 수
    private boolean first;           // 첫 페이지 여부
    private boolean last;            // 마지막 페이지 여부

    public static <T> PageResponse<T> of(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
