package org.wa.auth.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SyncServicePaginatedDto {
    private List<SyncServiceDto> users;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
}
