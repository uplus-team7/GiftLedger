package springboot.giftledger.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springboot.giftledger.entity.Member;
import springboot.giftledger.repository.MemberRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
//    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if(optionalMember.isEmpty()) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다." + email);
        }

        Member member = optionalMember.get();

//        if(){
//
//        }


        return MyUserDetails.builder()
                .id(member.getMemberId())
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(Collections.singletonList(
                        new SimpleGrantedAuthority(member.getRole().name())
                ))
                .build();
    }
}
