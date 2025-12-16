package springboot.giftledger.login;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import springboot.giftledger.auth.controller.LoginController;
import springboot.giftledger.auth.dto.LoginResultDto;
import springboot.giftledger.auth.service.LoginService;
import springboot.giftledger.dto.MemberDto;
import springboot.giftledger.entity.Member;
import springboot.giftledger.enums.Role;
import springboot.giftledger.repository.MemberRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 로그인 테스트
 * @SpringBootTest - 전체 통합 테스트
 */
@Slf4j
@SpringBootTest
@Transactional
@DisplayName("로그인 테스트")
public class LoginTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private LoginController loginController;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // #1. Repository 테스트
    @Test
    @DisplayName("Repository - 이메일로 회원 조회")
    public void testLoginRepository() {
        log.info("=== [테스트 시작] Repository - 이메일로 회원 조회 ===");

        // given: 테스트 회원 생성
        Member member = Member.builder()
                .email("test@mycom.com")
                .password(passwordEncoder.encode("password123"))
                .name("테스트유저")
                .ages("30대")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member);
        log.info("테스트 회원 생성 완료: {}", member.getEmail());

        // when
        Optional<Member> optionalMember = memberRepository.findByEmail("test@mycom.com");

        // then
        assertTrue(optionalMember.isPresent());
        assertEquals("테스트유저", optionalMember.get().getName());
        assertEquals("test@mycom.com", optionalMember.get().getEmail());

        log.info("✅ [테스트 성공] 회원 조회 성공: {}", optionalMember.get().getName());
        log.info("=== [테스트 종료] Repository - 이메일로 회원 조회 ===\n");
    }

    // #2. Service 테스트 - 로그인 성공
    @Test
    @DisplayName("Service - 로그인 성공")
    public void testLoginServiceSuccess() {
        log.info("=== [테스트 시작] Service - 로그인 성공 ===");

        // given: 회원 생성 (BCrypt로 암호화된 비밀번호)
        Member member = Member.builder()
                .email("service@mycom.com")
                .password(passwordEncoder.encode("password123"))
                .name("서비스테스트")
                .ages("30대")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member);
        log.info("테스트 회원 생성 완료: {}", member.getEmail());

        // when: 로그인 시도
        LoginResultDto result = loginService.login("service@mycom.com", "password123");

        // then: 로그인 성공 확인
        assertNotNull(result);
        assertEquals("success", result.getResult());
        assertNotNull(result.getToken());

        log.info("✅ [테스트 성공] 로그인 결과: {}, 토큰 생성: {}",
                result.getResult(), result.getToken() != null ? "완료" : "실패");
        log.info("=== [테스트 종료] Service - 로그인 성공 ===\n");
    }

    // #3. Service 테스트 - 로그인 실패 (잘못된 비밀번호)
    @Test
    @DisplayName("Service - 로그인 실패 (잘못된 비밀번호)")
    public void testLoginServiceFailWrongPassword() {
        log.info("=== [테스트 시작] Service - 로그인 실패 (잘못된 비밀번호) ===");

        // given: 회원 생성
        Member member = Member.builder()
                .email("fail@mycom.com")
                .password(passwordEncoder.encode("password123"))
                .name("실패테스트")
                .ages("30대")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member);
        log.info("테스트 회원 생성 완료: {}", member.getEmail());

        // when: 잘못된 비밀번호로 로그인 시도
        LoginResultDto result = loginService.login("fail@mycom.com", "wrongpassword");

        // then: 로그인 실패 확인
        assertNotNull(result);
        assertEquals("fail", result.getResult());
        assertNull(result.getToken());

        log.info("✅ [테스트 성공] 잘못된 비밀번호로 로그인 실패 정상 처리");
        log.info("=== [테스트 종료] Service - 로그인 실패 (잘못된 비밀번호) ===\n");
    }

    // #4. Service 테스트 - 로그인 실패 (존재하지 않는 이메일)
    @Test
    @DisplayName("Service - 로그인 실패 (존재하지 않는 이메일)")
    public void testLoginServiceFailNoUser() {
        log.info("=== [테스트 시작] Service - 로그인 실패 (존재하지 않는 이메일) ===");

        // when: 존재하지 않는 이메일로 로그인 시도
        LoginResultDto result = loginService.login("notexist@mycom.com", "password123");

        // then: 로그인 실패 확인
        assertNotNull(result);
        assertEquals("fail", result.getResult());
        assertNull(result.getToken());

        log.info("✅ [테스트 성공] 존재하지 않는 이메일로 로그인 실패 정상 처리");
        log.info("=== [테스트 종료] Service - 로그인 실패 (존재하지 않는 이메일) ===\n");
    }

    // #5. Controller 테스트 - 로그인 성공
    @Test
    @DisplayName("Controller - 로그인 성공")
    public void testLoginControllerSuccess() {
        log.info("=== [테스트 시작] Controller - 로그인 성공 ===");

        // given: 회원 생성
        Member member = Member.builder()
                .email("controller@mycom.com")
                .password(passwordEncoder.encode("password123"))
                .name("컨트롤러테스트")
                .ages("30대")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member);
        log.info("테스트 회원 생성 완료: {}", member.getEmail());

        // when: 로그인 요청 DTO 생성 및 로그인
        MemberDto loginRequest = MemberDto.builder()
                .email("controller@mycom.com")
                .password("password123")
                .build();

        ResponseEntity<LoginResultDto> response = loginController.login(loginRequest);

        // then: 응답 확인
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().getResult());
        assertNotNull(response.getBody().getToken());

        log.info("✅ [테스트 성공] HTTP 상태 코드: {}, 로그인 결과: {}",
                response.getStatusCode().value(), response.getBody().getResult());
        log.info("=== [테스트 종료] Controller - 로그인 성공 ===\n");
    }

    // #6. Controller 테스트 - 로그인 실패
    @Test
    @DisplayName("Controller - 로그인 실패 (잘못된 비밀번호)")
    public void testLoginControllerFail() {
        log.info("=== [테스트 시작] Controller - 로그인 실패 (잘못된 비밀번호) ===");

        // given: 회원 생성
        Member member = Member.builder()
                .email("controllerFail@mycom.com")
                .password(passwordEncoder.encode("password123"))
                .name("컨트롤러실패테스트")
                .ages("30대")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member);
        log.info("테스트 회원 생성 완료: {}", member.getEmail());

        // when: 잘못된 비밀번호로 로그인 시도
        MemberDto loginRequest = MemberDto.builder()
                .email("controllerFail@mycom.com")
                .password("wrongpassword")
                .build();

        ResponseEntity<LoginResultDto> response = loginController.login(loginRequest);

        // then: 401 Unauthorized 응답 확인
        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("fail", response.getBody().getResult());
        assertNull(response.getBody().getToken());

        log.info("✅ [테스트 성공] HTTP 상태 코드: {}, 로그인 결과: {}",
                response.getStatusCode().value(), response.getBody().getResult());
        log.info("=== [테스트 종료] Controller - 로그인 실패 (잘못된 비밀번호) ===\n");
    }

    // #7. Repository 테스트 - 이메일 존재 여부 확인
    @Test
    @DisplayName("Repository - 이메일 존재 여부 확인")
    public void testEmailExists() {
        log.info("=== [테스트 시작] Repository - 이메일 존재 여부 확인 ===");

        // given: 회원 생성
        Member member = Member.builder()
                .email("exists@mycom.com")
                .password(passwordEncoder.encode("password123"))
                .name("존재확인테스트")
                .ages("30대")
                .role(Role.ROLE_USER)
                .build();
        memberRepository.save(member);
        log.info("테스트 회원 생성 완료: {}", member.getEmail());

        // when & then
        boolean exists = memberRepository.existsByEmail("exists@mycom.com");
        boolean notExists = memberRepository.existsByEmail("notexists@mycom.com");

        assertTrue(exists);
        assertFalse(notExists);

        log.info("✅ [테스트 성공] exists@mycom.com 존재: {}, notexists@mycom.com 존재: {}",
                exists, notExists);
        log.info("=== [테스트 종료] Repository - 이메일 존재 여부 확인 ===\n");
    }
}