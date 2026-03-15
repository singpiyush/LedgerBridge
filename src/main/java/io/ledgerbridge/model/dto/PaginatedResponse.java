package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaginatedResponse<T>(
    List<T> data,
    Pagination pagination
) {
    public record Pagination(
        @JsonProperty("has_more") boolean hasMore,
        @JsonProperty("next_cursor") String nextCursor,
        @JsonProperty("total_count") Long totalCount
    ) {}

    public static <T> PaginatedResponse<T> of(List<T> data, boolean hasMore, String nextCursor, Long totalCount) {
        return new PaginatedResponse<>(data, new Pagination(hasMore, nextCursor, totalCount));
    }
}
