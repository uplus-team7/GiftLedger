package springboot.giftledger.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getTokenFromHeader(request);

        Claims claims = null;
        if (token != null) {
            claims = jwtUtil.validateToken(token);
        }

        if (claims != null) {

            List<?> roles = (List<?>) claims.get("roles"); // List<String> 으로 token 에 넣었지만 꺼낼 때는 ? 으로 우선 List 를 만든다.

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(roleName -> (String) roleName)  // <?> -> <String>
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            System.out.println("authorities");
            System.out.println(authorities);
            
            UsernamePasswordAuthenticationToken authenticationToken = jwtUtil.getAuthentication(token);

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }

        filterChain.doFilter(request, response);
    }
}

