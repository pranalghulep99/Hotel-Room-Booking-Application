package com.project.lakesidehotels.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponseDto {
	
	private Long id;
	private String email;
	private String token;
	private String type = "Bearer";
	private List<String> roles;

	public JwtResponseDto(Long id, String email, String token, List<String> roles) {
	        this.id = id;
	        this.email = email;
	        this.token = token;
	        this.roles = roles;
	    }
}