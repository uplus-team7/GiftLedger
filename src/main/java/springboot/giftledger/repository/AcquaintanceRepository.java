package springboot.giftledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.giftledger.entity.Acquaintance;
import springboot.giftledger.entity.Member;

public interface AcquaintanceRepository extends JpaRepository<Acquaintance,Long> {
    boolean existsByPhone(String phone);

    Acquaintance findByPhone(String phone);

    Acquaintance findByPhone_AndMember(String phone, Member member);
//    Acquaintance findByPhone_AndMember_Email(String phone, String email);

    Acquaintance findByPhone_AndMember_Email(String phone, String email);
}
