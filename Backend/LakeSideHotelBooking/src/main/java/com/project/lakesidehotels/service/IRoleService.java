package com.project.lakesidehotels.service;

import java.util.List;

import com.project.lakesidehotels.entities.Role;
import com.project.lakesidehotels.entities.User;

public interface IRoleService {
	
	List<Role> getRoles();

	Role createRole(Role theRole);

	void deleteRole(Long id);

	Role findByName(String name);
	
	User removeUserFromRole(Long userId, Long roleId);
	
	User assignRoleToUser(Long userId, Long roleId);
	
	Role removeAllUsersFromRole(Long roleId);
}
