package springboot.giftledger.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import springboot.giftledger.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>{
	
	
	Page<Event> findByAcquaintanceMemberEmail(String email,Pageable pageable);

}
