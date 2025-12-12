package springboot.giftledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.giftledger.entity.Event;
import springboot.giftledger.entity.Member;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>{

    Event findByEventId(Long eventId);

    @Query("""
        select e from Event e
        join fetch e.acquaintance a
        join fetch e.giftLogs
        where a.member.email = :email
        and e.eventId = :eventId
    """)
    List<Event> findDetailsByEventId(
            @Param("email") String email,
            @Param("eventId") Long eventId);
}