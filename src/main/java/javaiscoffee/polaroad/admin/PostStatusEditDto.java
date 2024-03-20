package javaiscoffee.polaroad.admin;

import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.PostStatus;
import lombok.Data;

@Data
public class PostStatusEditDto {
    private PostStatus status;
    private String reason;
}
