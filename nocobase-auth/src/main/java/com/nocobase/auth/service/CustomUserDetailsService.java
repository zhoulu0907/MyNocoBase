package com.nocobase.auth.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.nocobase.user.entity.User;
import com.nocobase.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.nocobase.user.entity.table.UserTableDef.USER;

/**
 * Custom UserDetailsService Implementation
 * Uses MyBatis-Flex to load user data for Spring Security
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Use MyBatis-Flex QueryWrapper with static TableDef
        QueryWrapper query = QueryWrapper.create()
                .where(USER.USERNAME.eq(username));

        User user = userMapper.selectOneByQuery(query);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // TODO: Load user roles from users_roles table
        // For now, return user with default role
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!"active".equals(user.getStatus()))
                .build();
    }
}
