package com.project.lakesidehotels.service;

public interface ITokenBlacklistService {
	 void addToBlacklist(String token);
	 boolean isTokenBlacklisted(String token);
}
