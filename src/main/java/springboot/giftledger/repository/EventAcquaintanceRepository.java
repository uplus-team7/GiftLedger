package springboot.giftledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot.giftledger.entity.EventAcquaintance;

public interface EventAcquaintanceRepository extends JpaRepository<EventAcquaintance, Long> {

}
