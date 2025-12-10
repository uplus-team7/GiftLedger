package springboot.giftledger.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Builder
@Getter
public class MyUserDetails implements UserDetails {

    private final String username; // 이메일
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private final Long id;

}
