package com.finalproject_sns.config;

import com.finalproject_sns.domain.User;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.service.UserService;
import com.finalproject_sns.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 권한 여부
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        // token 분리
        String token;
        try {
            token = authorizationHeader.split(" ")[1];
        } catch (Exception e) {
            log.info("token 추출에 실패 했습니다");
            filterChain.doFilter(request, response);
            return;
        }


        // token 만료됐는지 check
        if (JwtTokenUtil.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token
        // claim에서 userName 꺼내기
        String userName = JwtTokenUtil.getUserName(token, secretKey);

        // UserDetail 가져오기
        User user = userService.getUserByUserName(userName);

        // 개찰구( post 요철 열어주기) - 특정 조건에서만 열어주기
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getRole().name()))); // Role 바인딩
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 권한 부여
        filterChain.doFilter(request, response);
    }
}
