package com.twitter.monolith.service;

import com.twitter.monolith.dto.CurrentUserResponse;
import com.twitter.monolith.entity.AppUser;

public interface UserService {
    AppUser findOrCreate(String auth0Id, String email, String preferredUsername);
    CurrentUserResponse getCurrentUser(String auth0Id);
    CurrentUserResponse updateUsername(String auth0Id, String displayName);
}
