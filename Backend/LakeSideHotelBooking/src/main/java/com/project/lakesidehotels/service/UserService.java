package com.project.lakesidehotels.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.lakesidehotels.entities.Role;
import com.project.lakesidehotels.entities.User;
import com.project.lakesidehotels.exception.UserAlreadyExistsException;
import com.project.lakesidehotels.repository.RoleRepository;
import com.project.lakesidehotels.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private RoleRepository roleRepository;

	/**
	 * Method to register new User
	 * 
	 * First we are checking if user is already exists or not by user's email
	 * parameter
	 * 
	 * if(exists) => throwing exception
	 * 
	 * else{=> encode password of user for security purpose
	 * 
	 * and by default set user's role as ROLE_USER
	 * 
	 * then save user in database }
	 */
	@Override
	public User registerUser(User user) throws UserAlreadyExistsException {
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new UserAlreadyExistsException(user.getEmail() + " already exists");
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Role userRole = roleRepository.findByName("ROLE_USER").get();
		user.setRoles(Collections.singleton(userRole));
		return userRepository.save(user);
	}

	/**
	 * Method to get all the users
	 */
	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	/**
	 * Method to delete user by email
	 * 
	 * @Transactional, Spring ensures that the entire method is executed within a
	 * single transaction.
	 * 
	 * If => completes successfully => transaction is committed,
	 * 
	 * else => exception occurs, the transaction is rolled back.
	 * 
	 * getUser(email) => user not found this method @return
	 * UsernameNotFoundException
	 * 
	 */
	@Transactional
	@Override
	public void deleteUser(String email) {
		User theUser = getUser(email);
		if (theUser != null) {
			userRepository.deleteByEmail(email);
		}
	}

	/**
	 * Getting user by email
	 * 
	 * not found then @return UsernameNotFoundException with message
	 */
	@Override
	public User getUser(String email) {
		// TODO Auto-generated method stub
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

}
