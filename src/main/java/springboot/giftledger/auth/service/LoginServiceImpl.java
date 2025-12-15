package springboot.giftledger.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import springboot.giftledger.auth.dto.LoginResultDto;
import springboot.giftledger.repository.MemberRepository;
import springboot.giftledger.security.JwtUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

//    private final MemberRepository memberRepository;

    @Override
    public LoginResultDto login(String email, String password) {

        log.info("로그인 email: {}, password: {}", email, password);

        LoginResultDto loginResultDto = new LoginResultDto();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            log.info("success authentication email: {}, password: {}", email, password);

            String username = authentication.getName();
            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList();

            log.info("토큰 생성 시작");
            String token = jwtUtil.createToken(username, roles);

            log.info("토큰 생성 완료 email: {}, password: {}", email, password);

            return LoginResultDto.builder()
                    .result("success")
                    .token(token)
                    .build();

        } catch (Exception e) {
            log.error("fail authentication email: {}, password: {}", email, password);
            log.error("에러 상세 정보",e);
            loginResultDto.setResult("fail");

            return LoginResultDto.builder()
                    .result("fail")
                    .token(null)
                    .build();
        }
    }
}
