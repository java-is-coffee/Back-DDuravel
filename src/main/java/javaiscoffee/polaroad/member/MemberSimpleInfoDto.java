package javaiscoffee.polaroad.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberSimpleInfoDto {
    private Long memberId;
    private MemberStatus status;
}
