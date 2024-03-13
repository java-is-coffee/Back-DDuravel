package javaiscoffee.polaroad.post.card;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "cardId"
)
@Builder
@ToString
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;
    @NotNull @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @NotNull @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @NotNull @Setter
    private int index;      // index
    @Column(length = 255)
    @NotNull @Setter
    private String latitude;//위도
    @Column(length = 255)
    @NotNull @Setter
    private String longtitude;    //경도
    @Column(length = 255)
    @NotNull @Setter
    private String location;//상세 위치
    @Column(length = 255)
    @NotNull @Setter
    private String image;    //이미지 주소
    @Setter
    @Column(columnDefinition = "TEXT")
    private String content; //카드 본문
    @NotNull @Setter
    @Enumerated(EnumType.STRING)
    private CardStatus status;
    private LocalDateTime createdTime;
    @Setter
    private LocalDateTime updatedTime;

    @PrePersist
    public void PrePersist() {
        this.status = CardStatus.ACTIVE;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
}
