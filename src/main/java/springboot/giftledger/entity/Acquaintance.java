package springboot.giftledger.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.giftledger.enums.Relation;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acquaintance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long acquaintanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Relation relation;

    @Column(length = 50)
    private String groupName;

    @Column(nullable = false, length = 20)
    private String phone;

}