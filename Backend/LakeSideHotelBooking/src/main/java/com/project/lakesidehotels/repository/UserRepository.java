package com.project.lakesidehotels.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.lakesidehotels.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	boolean existsByEmail(String email);

	void deleteByEmail(String email);

	Optional<User> findByEmail(String email);

}
