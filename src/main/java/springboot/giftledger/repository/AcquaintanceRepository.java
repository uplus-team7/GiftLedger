package springboot.giftledger.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Member;

public interface AcquaintanceRepository extends JpaRepository<Acquaintance,Long> {
    
	Page<Acquaintance> findByMemberEmail(String email,Pageable pageable);
	
	Page<Acquaintance> findByMemberEmailAndNameContaining(
	        String email,
	        String keyword,
	        Pageable pageable
	);
	
	boolean existsByPhone(String phone);

    Acquaintance findByPhone(String phone);

    Acquaintance findByPhone_AndMember(String phone, Member member);

    Acquaintance findByPhone_AndMember_Email(String phone, String email);
}
