package springboot.giftledger.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import springboot.giftledger.entity.Event;

public interface EventRepository extends JpaRepository<Event, Long>{

    Event findByEventId(Long eventId);

    @Query("""
        select e from Event e
        join fetch e.eventAcquaintances
        where e.eventId = :eventId
            and e.member.email = :email
    """)
    Event findDetailsByEventId(
            @Param("email") String email,
            @Param("eventId") Long eventId);
}
