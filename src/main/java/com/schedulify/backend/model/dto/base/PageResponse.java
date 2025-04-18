package com.schedulify.backend.model.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;

    private PageMetadata metadata;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageMetadata {

        @JsonProperty("page_number")
        private int pageNumber;

        @JsonProperty("page_size")
        private int pageSize;

        @JsonProperty("total_elements")
        private long totalElements;

        @JsonProperty("total_pages")
        private int totalPages;

        private boolean first;

        private boolean last;

        @JsonProperty("has_next")
        private boolean hasNext;

        @JsonProperty("has_previous")
        private boolean hasPrevious;
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        PageMetadata metadata = PageMetadata.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();

        return PageResponse.<T>builder()
                .content(page.getContent())
                .metadata(metadata)
                .build();
    }
}