package org.wa.auth.service.repository;

import org.wa.auth.service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wa.auth.service.model.RoleEnum;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleEnum name);
}
