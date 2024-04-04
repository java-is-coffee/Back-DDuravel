package javaiscoffee.polaroad.member;

import jakarta.persistence.*;
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
        return Optional.ofNullable(query.getSingleResult());
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
    public void addMemberPostNumber(Long memberId, int changeNumber) {
        em.createQuery("UPDATE Member m Set m.postNumber = m.postNumber + :changeNumber WHERE m.memberId = :memberId")
                .setParameter("changeNumber", changeNumber)
                .setParameter("memberId", memberId)
                .executeUpdate();
    }
}