package com.project.lakesidehotels.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.lakesidehotels.entities.Role;
import com.project.lakesidehotels.entities.User;
import com.project.lakesidehotels.exception.RoleAlreadyExistException;
import com.project.lakesidehotels.exception.UserAlreadyExistsException;
import com.project.lakesidehotels.repository.RoleRepository;
import com.project.lakesidehotels.repository.UserRepository;

@Service
public class RoleService implements IRoleService {

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<Role> getRoles() {
		return roleRepository.findAll();
	}

	@Override
	public Role createRole(Role theRole) {
		String roleName = "ROLE_" + theRole.getName().toUpperCase();
		Role role = new Role(roleName);
		if (roleRepository.existsByName(roleName)) {
			throw new RoleAlreadyExistException(theRole.getName() + " role already exists");

		}
		return roleRepository.save(role);
	}

	@Override
	public void deleteRole(Long roleId) {
		this.removeAllUsersFromRole(roleId);
		roleRepository.deleteById(roleId);
	}

	@Override
	public Role findByName(String name) {
		return roleRepository.findByName(name).get();
	}

	@Override
	public User removeUserFromRole(Long userId, Long roleId) {
		Optional<User> user = userRepository.findById(userId);
		Optional<Role> role = roleRepository.findById(roleId);
		if (role.isPresent() && role.get().getUsers().contains(user.get())) {
			role.get().removeUserFromRole(user.get());
			roleRepository.save(role.get());
			return user.get();
		}
		throw new UsernameNotFoundException("User not found");
	}

	@Override
	public User assignRoleToUser(Long userId, Long roleId) {
		Optional<User> user = userRepository.findById(userId);
		Optional<Role> role = roleRepository.findById(roleId);
		if (user.isPresent() && user.get().getRoles().contains(role.get())) {
			throw new UserAlreadyExistsException(
					user.get().getFirstName() + " is already assigned to the" + role.get().getName() + " role");
		}
		if (role.isPresent()) {
			role.get().assignRoleToUser(user.get());
			roleRepository.save(role.get());
		}
		return user.get();
	}

	@Override
	public Role removeAllUsersFromRole(Long roleId) {
		Optional<Role> role = roleRepository.findById(roleId);
		role.ifPresent(Role::removeAllUsersFromRole);
		return roleRepository.save(role.get());
	}

}
