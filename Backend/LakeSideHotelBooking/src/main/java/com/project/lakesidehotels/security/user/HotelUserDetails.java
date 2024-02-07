package com.project.lakesidehotels.security.user;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.lakesidehotels.entities.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelUserDetails implements UserDetails {

	private Long id;
	private String email;
	private String password;
	private Collection<GrantedAuthority> authorities;
	
	/**
	 * This is a static method that takes a User object and converts it into a
	 * HotelUserDetails object.
	 * 
	 * It extracts the necessary information from the User object, such as id,
	 * email, password, and roles.
	 * 
	 * It creates a list of GrantedAuthority objects based on the user's roles.
	 * 
	 * It returns a new HotelUserDetails object with the extracted information.
	 */
	
	public static HotelUserDetails buildUserDetails(User user) {
		List<GrantedAuthority> authorities=user.getRoles()
				.stream()
				.map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());
		return new HotelUserDetails(
				user.getId(),
				user.getEmail(),
				user.getPassword(),
				authorities);
				
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return password;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

}
