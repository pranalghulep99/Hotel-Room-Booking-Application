package com.project.lakesidehotels.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.lakesidehotels.entities.Role;
import com.project.lakesidehotels.entities.User;
import com.project.lakesidehotels.exception.RoleAlreadyExistException;
import com.project.lakesidehotels.service.IRoleService;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/roles")
public class RoleController {

	@Autowired
	private IRoleService roleService;

	/** Get all the roles from the database */
	@GetMapping("/all-roles")
	public ResponseEntity<List<Role>> getAllRoles() {
		return new ResponseEntity<List<Role>>(roleService.getRoles(), HttpStatus.FOUND);
	}

	/** Create new role */
	@PostMapping("/create-new-role")
	public ResponseEntity<String> createRole(@RequestBody Role theRole) {
		try {
			roleService.createRole(theRole);
			return ResponseEntity.ok("New Role created successfully !");
		} catch (RoleAlreadyExistException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@DeleteMapping("/delete/{roleId}")
	public void deleteRole(@PathVariable("roleId") Long roleId) {
		roleService.deleteRole(roleId);
	}

	/* Method to remove all Users from the Role */
	@PostMapping("/remove-all-users-from-role/{roleId}")
	public Role removeAllUserFromRole(@PathVariable("roleId") Long roleId) {
		return roleService.removeAllUsersFromRole(roleId);
	}

	/* Remove Single User from Role */
	@PostMapping("/remove-user-from-role")
	public User removeUserFromRole(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId) {
		return roleService.removeUserFromRole(userId, roleId);
	}

	/* Assign new Role to User */
	@PostMapping("/assign-user-to-role")
	public User assignRoleToUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId) {
		return roleService.assignRoleToUser(userId, roleId);
	}

}
