package org.wa.auth.service.service;

import org.wa.auth.service.dto.AdminUserBlockResponse;

import java.util.UUID;

public interface AdminService {
    AdminUserBlockResponse blockUser(UUID externalId);

    AdminUserBlockResponse unblockUser(UUID externalId);

    boolean isUserBlocked(UUID externalId);
}
