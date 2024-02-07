package com.project.lakesidehotels.service;

import java.util.List;

import com.project.lakesidehotels.entities.User;
import com.project.lakesidehotels.exception.UserAlreadyExistsException;

public interface IUserService {
	
	User registerUser(User user) throws UserAlreadyExistsException;

	List<User> getUsers();
	
	void deleteUser(String email);
	
	User getUser(String email);
}
