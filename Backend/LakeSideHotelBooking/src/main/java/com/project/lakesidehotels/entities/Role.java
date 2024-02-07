package com.project.lakesidehotels.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@JsonIgnore
	@ManyToMany(mappedBy = "roles")
	private Collection<User> users = new HashSet<>();
	
	public Role(String name) {
		this.name=name;
	}

//	Helpers methods to Assign roles to User
	public void assignRoleToUser(User user) {
		user.getRoles().add(this);
		this.getUsers().add(user);
	}

//	Helpers methods to Remove role of User
	public void removeUserFromRole(User user) {
		user.getRoles().remove(this);
		this.getUsers().remove(user);
	}

//	Helpers methods to Remove all Users from particular role
	public void removeAllUsersFromRole() {
		if (this.getUsers() != null) {
			List<User> roleUsers = this.getUsers().stream().toList();
			roleUsers.forEach((user) -> this.removeUserFromRole(user));
		}
	}

	public String getName() {
		return name != null ? name : "";
	}

}
