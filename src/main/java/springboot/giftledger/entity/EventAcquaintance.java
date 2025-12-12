package springboot.giftledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAcquaintance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventAcquaintanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acquaintance_id", nullable = false)
    private Acquaintance acquaintance;
}
