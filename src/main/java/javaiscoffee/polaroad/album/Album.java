package javaiscoffee.polaroad.album;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.album.albumCard.AlbumCard;
import javaiscoffee.polaroad.member.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "albumId"
)
@Builder
@ToString(exclude = "member")
@Table(name = "albums")
public class Album {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long albumId;
    @NotNull @JoinColumn(name = "member_id")
    @Setter @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @NotNull @Setter
    private String name;
    @NotNull @Setter
    private String description; // 간단한 설명
    @Setter
    private LocalDateTime updatedTime;
    @NotNull
    @Setter
    private LocalDateTime createdTime;

    @OneToMany(mappedBy = "album")
    private List<AlbumCard> albumCards;

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.albumCards = new ArrayList<>();
    }
}
