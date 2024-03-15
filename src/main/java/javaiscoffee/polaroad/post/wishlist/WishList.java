package javaiscoffee.polaroad.post.wishlist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "wish_lists")
@Getter
@NoArgsConstructor
public class WishList {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishListId;

    @ManyToOne @Setter
    @JoinColumn(name = "member_id", nullable = false)
    @JsonBackReference
    private Member member;
    @NotNull @Setter
    private String name;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public WishList(Member member, String name) {
        this.member = member;
        this.name = name;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
}
