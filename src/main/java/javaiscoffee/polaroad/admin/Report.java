package javaiscoffee.polaroad.admin;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import javaiscoffee.polaroad.member.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "reports")
@ToString(exclude = {"member", "admin"})
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    @NotNull @Setter
    private Long targetId;  // 이중 관계이므로 join을 하지 않음
    @NotNull @Setter
    @Enumerated(EnumType.STRING)
    private ReportTargetType targetType;
    @Setter
    @Column(length = 500)
    private String reason;
    @NotNull @Setter
    @ManyToOne
    @JoinColumn(name = "member_id")
    @JsonBackReference
    private Member member;
    @Setter
    @ManyToOne
    @JoinColumn(name = "admin_id")
    @JsonBackReference
    private Member admin;
    @NotNull @Setter
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    @NotNull @Setter
    private LocalDateTime createdTime;
    @NotNull @Setter
    private LocalDateTime updatedTime;

    @PrePersist
    public void PrePersist() {
        this.status = ReportStatus.INCOMPLETE;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }
}
