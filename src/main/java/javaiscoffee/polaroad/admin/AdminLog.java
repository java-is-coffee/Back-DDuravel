package javaiscoffee.polaroad.admin;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import javaiscoffee.polaroad.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "admin_logs")
public class AdminLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;
    @ManyToOne @Setter
    @JoinColumn(name = "admin_id", nullable = false)
    @JsonBackReference
    private Member admin;
    @Setter
    private Long targetId;  // 멤버, 포스트, 리뷰 ID
    @Setter
    @Enumerated(EnumType.STRING)
    private AdminTargetType targetType; // 대상 타입
    @Setter
    @Enumerated(EnumType.STRING)
    private AdminActionType actionType; // 관리자 행동 종류
    @Setter
    private String actionValue;   //설정한 값
    private String reason;  //이유 기록
    private LocalDateTime createdTime;

    public AdminLog(Member admin, Long targetId, AdminTargetType targetType, AdminActionType actionType, String actionValue, String reason) {
        this.admin = admin;
        this.targetId = targetId;
        this.targetType = targetType;
        this.actionType = actionType;
        this.actionValue = actionValue;
        this.reason = reason;
    }

    @PrePersist
    public void PrePersist() {
        this.createdTime = LocalDateTime.now();
    }
}
