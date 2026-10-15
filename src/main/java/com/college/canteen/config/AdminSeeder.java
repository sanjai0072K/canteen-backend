package com.college.canteen.config;

import com.college.canteen.entity.Admin;
import com.college.canteen.repository.AdminRepository;
import com.college.canteen.security.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// On every start, makes sure the admin account from application.properties exists.

// To change the admin password: change it in application.properties and restart.
@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final AdminRepository adminRepository;
    private final String username;
    private final String password;

    public AdminSeeder(AdminRepository adminRepository,
                       @Value("${canteen.admin.username:admin}") String username,
                       @Value("${canteen.admin.password:admin123}") String password) {
        this.adminRepository = adminRepository;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        Admin admin = adminRepository.findByUsername(username).orElseGet(Admin::new);
        String salt = PasswordHasher.newSalt();
        admin.setUsername(username);
        admin.setSalt(salt);
        admin.setPasswordHash(PasswordHasher.hash(password, salt));
        adminRepository.save(admin);
        log.info("Admin account ready: username '{}'", username);
    }
}
