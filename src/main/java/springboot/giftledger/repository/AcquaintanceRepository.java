package springboot.giftledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.giftledger.entity.Acquaintance;

import java.util.List;

public interface AcquaintanceRepository extends JpaRepository<Acquaintance,Long> {
    boolean existsByPhone(String phone);

    Acquaintance findByPhone(String phone);
//    Acquaintance findByPhone_AndMember_Email(String phone, String email);

//    List<Acquaintance> findBy
}
