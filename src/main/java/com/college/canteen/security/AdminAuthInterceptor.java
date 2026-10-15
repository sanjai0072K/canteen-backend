package com.college.canteen.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

// Blocks every /api/admin/** request that does not carry a valid admin token
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final AdminTokenStore tokenStore;

    public AdminAuthInterceptor(AdminTokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        // browser pre-flight requests carry no token
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = AdminTokenStore.extract(request.getHeader("Authorization"));
        if (token != null && tokenStore.isValid(token)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"Admin login required\"}");
        return false;
    }
}
