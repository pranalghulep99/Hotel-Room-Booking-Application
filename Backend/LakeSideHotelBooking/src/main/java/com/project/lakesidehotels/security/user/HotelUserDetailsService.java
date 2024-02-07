package com.project.lakesidehotels.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.lakesidehotels.entities.User;
import com.project.lakesidehotels.repository.UserRepository;

/**
 * The main purpose of this class is to act as a bridge between Spring Security
 * and your data store (UserRepository).
 * 
 * It implements the logic of loading user details from the data store based on
 * the provided username.
 * 
 * buildUserDetails method of the HotelUserDetails class to convert the User
 * entity into a HotelUserDetails object.
 */
@Service
public class HotelUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not Found"));
		return HotelUserDetails.buildUserDetails(user);
	}

}
