package org.wa.auth.service.repository;

import org.springframework.stereotype.Repository;
import org.wa.auth.service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.wa.auth.service.model.RoleEnum;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleEnum name);
}
