package com.finalproject_sns.config.filter;

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

    // 실제 필터링 로직, 토근의 인증정보를 SecurityContext에 저장하기 위한 역할 수행
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더의 토큰 가져오기
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("====start=====");

        // 토큰이 없을 때
        if (authorizationHeader == null) {
            log.info("null={}");
            request.setAttribute("exception", ErrorCode.INVALID_PERMISSION.name());
            filterChain.doFilter(request, response);
            return;
        }


        // bearer로 시작하는 토큰이 아닌 경우
        if (!authorizationHeader.startsWith("Bearer ")) {
            log.error("authorizationHeader={}", authorizationHeader);
            request.setAttribute("exception", ErrorCode.INVALID_TOKEN.name());
            filterChain.doFilter(request, response);
            return;
        }

        // token 분리
        String token;

        try {
            token = authorizationHeader.split(" ")[1];
            log.info("token={}", token);

            // 토큰 만료된 경우(catch로 예외 넘기기)
            if (JwtTokenUtil.isExpired(token, secretKey)) {
                log.info("JwtTokenUtil.isExpired");
                request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.name());
                return;
            }

            // Token에서 UserName꺼내기 (JwtTokenUtil의 Claim에서 꺼냄)
            String userName = JwtTokenUtil.getUserName(token, secretKey);

            // UserDetail 가져오기
            User user = userService.getUserByUserName(userName);

            // 개찰구( post 요철 열어주기) - 특정 조건에서만 열어주기
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(new SimpleGrantedAuthority(user.getRole().name()))); // Role 바인딩
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // 권한 부여
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.name());
            filterChain.doFilter(request, response);
        }
    }
}
