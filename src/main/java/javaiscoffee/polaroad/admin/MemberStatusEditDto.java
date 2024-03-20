package javaiscoffee.polaroad.admin;

import javaiscoffee.polaroad.member.MemberStatus;
import lombok.Data;

@Data
public class MemberStatusEditDto {
    private MemberStatus status;
    private String reason;
}
