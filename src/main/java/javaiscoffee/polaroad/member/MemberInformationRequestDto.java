package javaiscoffee.polaroad.member;


import lombok.Data;

@Data
public class MemberInformationRequestDto {
    private Data data;

    @lombok.Data
    public static class Data {
        private Long memberId;
        private String email;
        private String name;
        private String nickname;
        private String profileImage;
    }
}
