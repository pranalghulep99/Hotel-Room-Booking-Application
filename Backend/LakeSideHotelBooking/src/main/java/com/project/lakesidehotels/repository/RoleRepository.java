package com.project.lakesidehotels.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.lakesidehotels.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{

	Optional<Role> findByName(String role);

	boolean existsByName(String roleName);


}
