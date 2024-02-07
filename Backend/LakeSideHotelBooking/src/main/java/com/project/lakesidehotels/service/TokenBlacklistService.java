package com.project.lakesidehotels.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService implements ITokenBlacklistService{

	private final Set<String> blacklistedTokens = new HashSet<>();
	
	@Override
	public void addToBlacklist(String token) {
		blacklistedTokens.add(token);
	}

	@Override
	public boolean isTokenBlacklisted(String token) {
		return blacklistedTokens.contains(token);
	}

}
