package springboot.giftledger.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springboot.giftledger.auth.dto.LoginResultDto;
import springboot.giftledger.auth.service.LoginService;
import springboot.giftledger.dto.MemberDto;

@Tag(name = "인증", description = "로그인 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
    @PostMapping("/login")
    public ResponseEntity<LoginResultDto> login(@RequestBody MemberDto memberDto){

        log.info("로그인 요청 email: {}, password: {}", memberDto.getEmail(), memberDto.getPassword());
        LoginResultDto loginResultDto = loginService.login(memberDto.getEmail(), memberDto.getPassword());

        log.info("loginResultDto: {}", loginResultDto);

        if ("success".equals(loginResultDto.getResult())) {
            return ResponseEntity.ok(loginResultDto);
        }
        else {
            return ResponseEntity.status(401).body(loginResultDto);
        }
    }
}