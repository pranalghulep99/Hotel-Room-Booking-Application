package com.project.lakesidehotels.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.project.lakesidehotels.security.user.HotelUserDetails;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${auth.token.jwtSecret}")
	private String jwtSecret;

	@Value("${auth.token.expirationInMils}")
	private int jwtExpirationMs;

	/**
	 * generateJwtTokenForUser Method:
	 * 
	 * Takes an Authentication object as a parameter, extracts user details from it,
	 * and generates a JWT for the user.
	 * 
	 * Uses the Jwts builder to create a JWT with the subject set to the user's
	 * username, claims set to the user's roles, and other standard JWT properties.
	 * 
	 * Signs the JWT with the HMAC SHA-256 algorithm using the provided secret key.
	 * Returns the compact representation of the generated JWT.
	 */
	public String generateJwtTokenForUser(Authentication authentication) {
		HotelUserDetails userPrincipal = (HotelUserDetails) authentication.getPrincipal();
		List<String> roles = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		return Jwts.builder().setSubject(userPrincipal.getUsername()).claim("roles", roles).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Generates a Key object for signing and verifying JWTs using the HMAC SHA-256
	 * algorithm.
	 * 
	 * Uses the Keys utility class from the io.jsonwebtoken library to create a key
	 * from the base64-decoded jwtSecret
	 */
	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	/**
	 * Takes a JWT as a parameter and extracts the subject (username) from the JWT's
	 *
	 * claims using the provided secret key.
	 */
	public String getUserNameFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
	}

	/**
	 * Takes a JWT as a parameter and validates its structure and signature.
	 * 
	 * Uses the Jwts.parserBuilder() to build a JWT parser with the provided secret
	 * key.
	 * 
	 * Catches various exceptions that might occur during the parsing process
	 * 
	 * (e.g., if the token is malformed, expired, unsupported, or if there are no
	 * claims).
	 * 
	 * Logs errors if any of these exceptions occur and returns false if the token
	 * is not valid.
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
			return true;
		} catch (MalformedJwtException e) {
			LOGGER.error("Invalid jwt token : {} ", e.getMessage());
		} catch (ExpiredJwtException e) {
			LOGGER.error("Expired token : {} " + e.getMessage());
		} catch (UnsupportedJwtException e) {
			LOGGER.error("This token is not supported: {} " + e.getMessage());
		} catch (IllegalArgumentException e) {
			LOGGER.error("No Claims Found : {} " + e.getMessage());
		}
		return false;
	}
}
