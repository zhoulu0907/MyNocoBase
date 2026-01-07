package com.nocobase.auth.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.nocobase.auth.dto.AuthResponse;
import com.nocobase.auth.dto.LoginRequest;
import com.nocobase.auth.dto.RegisterRequest;
import com.nocobase.auth.util.JwtTokenProvider;
import com.nocobase.user.entity.Role;
import com.nocobase.user.entity.User;
import com.nocobase.user.entity.UserRole;
import com.nocobase.user.mapper.RoleMapper;
import com.nocobase.user.mapper.UserMapper;
import com.nocobase.user.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.nocobase.user.entity.table.RoleTableDef.ROLE;
import static com.nocobase.user.entity.table.UserTableDef.USER;

/**
 * Authentication Service
 * Handles user registration and login using MyBatis-Flex
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        QueryWrapper userQuery = QueryWrapper.create()
                .where(USER.USERNAME.eq(request.getUsername()));
        User existingUser = userMapper.selectOneByQuery(userQuery);

        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email already exists
        QueryWrapper emailQuery = QueryWrapper.create()
                .where(USER.EMAIL.eq(request.getEmail()));
        User existingEmail = userMapper.selectOneByQuery(emailQuery);

        if (existingEmail != null) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus("active");

        // Insert user using MyBatis-Flex
        userMapper.insert(user);

        // Assign default role (find "user" role)
        QueryWrapper roleQuery = QueryWrapper.create()
                .where(ROLE.NAME.eq("user"));
        Role userRole = roleMapper.selectOneByQuery(roleQuery);

        if (userRole != null) {
            // Create user-role association
            UserRole userRoleAssociation = new UserRole();
            userRoleAssociation.setUserId(user.getId());
            userRoleAssociation.setRoleId(userRole.getId());
            userRoleMapper.insert(userRoleAssociation);
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }

    /**
     * Login user
     */
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        // Get user details
        QueryWrapper userQuery = QueryWrapper.create()
                .where(USER.USERNAME.eq(request.getUsername()));
        User user = userMapper.selectOneByQuery(userQuery);

        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}
