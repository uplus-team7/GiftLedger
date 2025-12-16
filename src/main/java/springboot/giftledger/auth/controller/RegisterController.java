package springboot.giftledger.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springboot.giftledger.auth.dto.LoginResultDto;
import springboot.giftledger.auth.service.RegisterService;
import springboot.giftledger.dto.MemberDto;

@Tag(name = "회원가입", description = "회원가입 API")
@RestController
@RequiredArgsConstructor
@Slf4j
public class RegisterController {

    private final RegisterService registerService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록하고 JWT 토큰을 발급받습니다")
    @PostMapping("/users")
    public ResponseEntity<LoginResultDto> register(@RequestBody  MemberDto memberDto){
        log.info(" 회원 가입 요청 email={} name={} ", memberDto.getEmail(), memberDto.getName());

        LoginResultDto loginResultDto = registerService.register(memberDto);

        if("success".equals(loginResultDto.getResult())){
            return ResponseEntity.ok(loginResultDto);
        } else {
            return ResponseEntity.badRequest().body(loginResultDto);
        }
    }
}