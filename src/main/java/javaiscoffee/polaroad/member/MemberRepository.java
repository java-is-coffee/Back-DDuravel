package javaiscoffee.polaroad.member;

import java.util.Optional;

public interface MemberRepository {
    public Optional<Member> findByEmail(String email);  //이메일로 Member 찾기
    boolean existsByEmail(String email);    //이메일로 Member 존재 여부 확인

    //이메일로 Member 찾기 spring security 위해 추가
    Optional<Member> findByMemberId(Long memberId);

    public Optional<Member> findById(Long id);

    public Follow findMemberFollow(FollowId id);
    public Follow saveMemberFollow(Follow follow);
    public boolean deleteMemberFollow(Follow follow);

    public void save(Member newMember);

    public void delete(Member member);
    public Member updateMember(Member updatedMember);

}


