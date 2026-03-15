package io.ledgerbridge.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Paginated response wrapper")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaginatedResponse<T>(
    @Schema(description = "List of items for the current page")
    List<T> data,

    @Schema(description = "Pagination metadata")
    Pagination pagination
) {
    @Schema(description = "Cursor-based pagination metadata")
    public record Pagination(
        @Schema(description = "Whether more items exist beyond this page", example = "true")
        @JsonProperty("has_more") boolean hasMore,

        @Schema(description = "Cursor to use for the next page (null if no more pages)", example = "txn_abc123")
        @JsonProperty("next_cursor") String nextCursor,

        @Schema(description = "Approximate total count of matching items", example = "142")
        @JsonProperty("total_count") Long totalCount
    ) {}

    public static <T> PaginatedResponse<T> of(List<T> data, boolean hasMore, String nextCursor, Long totalCount) {
        return new PaginatedResponse<>(data, new Pagination(hasMore, nextCursor, totalCount));
    }
}
