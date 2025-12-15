package springboot.giftledger.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springboot.giftledger.entity.EventAcquaintance;

public interface EventAcquaintanceRepository extends JpaRepository<EventAcquaintance, Long> {
	
	Optional<EventAcquaintance> findByEvent_EventId(long eventId);
//	Optional<EventAcquaintance> findByEventIdAndAcquaintanceId(long eventId, long acquaintanceId);
	
	@Query("""
		    select ea
		    from EventAcquaintance ea
		    join fetch ea.acquaintance
		    where ea.event.eventId = :eventId
		""")
		List<EventAcquaintance> findAllByEventIdWithAcquaintance(@Param("eventId") Long eventId);

}
