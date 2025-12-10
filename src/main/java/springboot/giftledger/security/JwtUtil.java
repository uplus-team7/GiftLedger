package springboot.giftledger.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final MyUserDetailsService myUserDetailsService;

    private SecretKey secretKey;

    @Value("$jwt.secret")
    private String secretKeyStr;

    private final long tokenValidDuration = 1000 * 60 * 60 * 24;

    @PostConstruct
    protected void init() {
        secretKey = new SecretKeySpec(
                secretKeyStr.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String createToken(String email, List<String> roles) {

        Date now = new Date();

        String token = Jwts.builder()
                .subject(email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + tokenValidDuration))
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();

        return token;
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(this.getEmailFromToken(token));
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "",userDetails.getAuthorities());
    }


    public Claims validateToken(String token) {
        // 로그인하지 않은 상태에서 들어온 잘못된 토큰(형식 오류, 서명 오류 등)에 대해
        // 예외 로그를 길게 남기지 않으려면 try-catch로 한 번 감싸주는 것이 좋다.
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 만료 여부 체크
            if (claims.getExpiration() != null && claims.getExpiration().before(new Date())) {
                return null; // 만료된 토큰
            }

            return claims;   // 유효한 토큰이면 Claims 반환

        } catch (Exception e) {
            // MalformedJwtException, SignatureException, IllegalArgumentException 등
            // 어떤 예외가 나든 "유효하지 않은 토큰"으로 처리
            return null;
        }
    }


    /*
     * Util Method
     */

    public String getEmailFromToken(String token) {

        String subject = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token).getPayload()
                .getSubject();

        return subject;
    }

    public String getTokenFromHeader(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }


}
