package javaiscoffee.polaroad.member;

import jakarta.persistence.*;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.post.PostMemberInfoDto;
import javaiscoffee.polaroad.response.ResponseMessages;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Table(name = "member")
public class JpaMemberRepository implements MemberRepository {
    private final EntityManager em;

    public JpaMemberRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Member member) {
        em.persist(member);
    }

    @Override
    public void delete(Member member) {
        em.remove(member);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(em.find(Member.class,id));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m WHERE m.email = :email", Member.class);
        query.setParameter("email", email);
        try {
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(m) FROM Member m WHERE m.email = :email", Long.class);
        query.setParameter("email", email);
        long count = query.getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m WHERE m.memberId = :memberId", Member.class);
        query.setParameter("memberId", memberId);
        List<Member> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Follow findMemberFollow(FollowId id) {
        return em.find(Follow.class, id);
    }

    @Override
    public Follow saveMemberFollow(Follow follow) {
        em.persist(follow);
        em.flush();
        return follow;
    }

    @Override
    public boolean deleteMemberFollow(Follow follow) {
        try {
            em.remove(follow);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Member updateMember(Member updatedMember) {
        em.merge(updatedMember);
        return updatedMember;
    }

    @Override
    public Optional<MemberSimpleInfoDto> getMemberSimpleInfo(Long memberId) {
        List<MemberSimpleInfoDto> results = em.createQuery("SELECT new javaiscoffee.polaroad.member.MemberSimpleInfoDto(m.memberId, m.status) " +
                        "FROM Member m WHERE m.memberId = :memberId", MemberSimpleInfoDto.class)
                .setParameter("memberId", memberId)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public PostMemberInfoDto getPostMemberInfoByMemberId(Long memberId) {
        TypedQuery<PostMemberInfoDto> query = em.createQuery("SELECT m.memberId, m.name, m.nickname, m.profileImage FROM Member m WHERE m.memberId = :memberId", PostMemberInfoDto.class);
        query.setParameter("memberId", memberId);
        List<PostMemberInfoDto> results = query.getResultList();
        return results.get(0);
    }

    @Override
    public void addMemberPostNumber(Long memberId, int changeNumber) {
        em.createQuery("UPDATE Member m Set m.postNumber = m.postNumber + :changeNumber WHERE m.memberId = :memberId")
                .setParameter("changeNumber", changeNumber)
                .setParameter("memberId", memberId)
                .executeUpdate();
    }

    public FollowingMemberResponseDto getFollowingMemberInfo(Long followingMemberId, int page, int pageSize) {
        List<FollowingMemberInfoDto> memberInfoDtoList = em.createQuery(
                        "select new javaiscoffee.polaroad.member.FollowingMemberInfoDto(f.followedMember.memberId, f.followedMember.nickname, f.followedMember.profileImage, f.createdTime) " +
                                "from Follow f " +
                                "where f.followingMember.memberId = :followingMemberId order by f.createdTime desc", FollowingMemberInfoDto.class)
                .setParameter("followingMemberId", followingMemberId)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize + 1)
                .getResultList();
        boolean hasNext = memberInfoDtoList.size() == pageSize + 1;
        return new FollowingMemberResponseDto(memberInfoDtoList, hasNext);
    }

    @Override
    public MemberBasicInfoDto getMemberMiniProfileDto(Long memberId) {
        try{
        return em.createQuery("select new javaiscoffee.polaroad.member.MemberBasicInfoDto(m.name,m.nickname,m.profileImage,m.postNumber,m.followedNumber,m.followingNumber,m.status) from Member m where m.memberId = :memberId", MemberBasicInfoDto.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
        } catch (NoResultException e) {
            throw new NotFoundException(ResponseMessages.MEMBER_NOT_FOUND.getMessage());
        }
    }

    public boolean existsFollowing(Long memberId, Long targetId) {
        Long count = em.createQuery("select count(f) from Follow f where f.followingMember.memberId = :memberId and f.followedMember.memberId = :targetId", Long.class)
                .setParameter("memberId", memberId)
                .setParameter("targetId", targetId)
                .getSingleResult();
        return count > 0;
    }
}