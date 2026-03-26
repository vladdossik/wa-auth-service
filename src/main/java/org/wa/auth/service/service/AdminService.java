package org.wa.auth.service.service;

import java.util.UUID;

public interface AdminService {
    void blockUser(UUID externalId);
    void unblockUser(UUID externalId);
    boolean isBlocked(UUID externalId);
}
