package springboot.giftledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.PayMethod;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long giftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_acquaintance_id", nullable = false)
    private EventAcquaintance eventAcquaintance;

    @Column(nullable = false)
    private long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayMethod payMethod;

    @Column(columnDefinition = "TEXT")
    private String memo;

}
