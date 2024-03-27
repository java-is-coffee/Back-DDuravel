package javaiscoffee.polaroad.post.card;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.album.albumCard.AlbumCard;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.post.Post;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

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
    private int cardIndex;      // index
    @NotNull @Setter
    private double latitude; // 위도
    @NotNull @Setter
    private double longitude; // 경도
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

    @OneToMany(mappedBy = "card")
    private List<AlbumCard> albumCards;

    @PrePersist
    public void PrePersist() {
        this.status = CardStatus.ACTIVE;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
}
