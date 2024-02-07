package com.project.lakesidehotels.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.project.lakesidehotels.dto.JwtResponseDto;
import com.project.lakesidehotels.dto.LoginRequestDto;
import com.project.lakesidehotels.entities.User;
import com.project.lakesidehotels.exception.UserAlreadyExistsException;
import com.project.lakesidehotels.security.jwt.AuthTokenFilter;
import com.project.lakesidehotels.security.jwt.JwtUtils;
import com.project.lakesidehotels.security.user.HotelUserDetails;
import com.project.lakesidehotels.service.ITokenBlacklistService;
import com.project.lakesidehotels.service.IUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins =  "http://localhost:5173" 
		, methods = { RequestMethod.POST, RequestMethod.GET,RequestMethod.OPTIONS })
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
	private final IUserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	@Autowired
	private AuthTokenFilter authTokenFilter;
	@Autowired
	private ITokenBlacklistService tokenBlacklistService;

	@PostMapping("/register-user")
	public ResponseEntity<?> registerUser(@RequestBody User user) {
		try {
			userService.registerUser(user);
			return ResponseEntity.ok("Registration Successful !");
		} catch (UserAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDto request) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtTokenForUser(authentication);
		HotelUserDetails userDetails = (HotelUserDetails) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		return ResponseEntity.ok(new JwtResponseDto(userDetails.getId(), userDetails.getEmail(), jwt, roles));
	}

	@PostMapping("/logout")
	public void logoutUser(HttpServletRequest request) {
		LOGGER.info("logging out user");
		String jwt = authTokenFilter.parseJwt(request);
		if (jwt != null && jwtUtils.validateToken(jwt)) {
			if (!tokenBlacklistService.isTokenBlacklisted(jwt)) {
				tokenBlacklistService.addToBlacklist(jwt);
			}
			SecurityContextHolder.clearContext();
			LOGGER.info("Logout Successfully !!!");
		}
	}
}
