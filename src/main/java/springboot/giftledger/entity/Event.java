package springboot.giftledger.entity;

import jakarta.persistence.*;
import lombok.*;
import springboot.giftledger.enums.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventType eventType;

    @Column(nullable = false, length = 100)
    private String eventName;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @Column(length = 255)
    private String location;

    @Column(nullable = false)
    private Boolean isOwner;


    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<EventAcquaintance> eventAcquaintances;
}