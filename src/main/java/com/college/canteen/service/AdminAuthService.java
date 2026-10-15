package com.college.canteen.service;

import com.college.canteen.dto.AdminLoginRequest;
import com.college.canteen.dto.AdminLoginResponse;
import com.college.canteen.entity.Admin;
import com.college.canteen.repository.AdminRepository;
import com.college.canteen.security.AdminTokenStore;
import com.college.canteen.security.PasswordHasher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final AdminTokenStore tokenStore;

    public AdminAuthService(AdminRepository adminRepository, AdminTokenStore tokenStore) {
        this.adminRepository = adminRepository;
        this.tokenStore = tokenStore;
    }

    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.username().trim())
                .orElseThrow(this::wrongCredentials);

        if (!PasswordHasher.matches(request.password(), admin.getSalt(), admin.getPasswordHash())) {
            throw wrongCredentials();
        }
        return new AdminLoginResponse("Admin login successful", tokenStore.issue());
    }

    public void logout(String authorizationHeader) {
        tokenStore.revoke(AdminTokenStore.extract(authorizationHeader));
    }

    private ResponseStatusException wrongCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password");
    }
}
