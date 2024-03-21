package javaiscoffee.polaroad.post.wishlist;

import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.post.PostRepository;
import javaiscoffee.polaroad.post.PostStatus;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional(readOnly = true)
public class WishListService {
    private final WishListPostRepository wishListPostRepository;
    private final WishListRepository wishListRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Autowired
    public WishListService(WishListPostRepository wishListPostRepository, WishListRepository wishListRepository, MemberRepository memberRepository, PostRepository postRepository) {
        this.wishListPostRepository = wishListPostRepository;
        this.wishListRepository = wishListRepository;
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    /**
     * 위시리스트 생성
     */
    @Transactional
    public void createWishList(Long memberId, String wishListname) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //현재 사용자 wishList 개수
        int num = wishListRepository.countWishListByMember(member);
        if(num >= 5) throw new BadRequestException(ResponseMessages.WISHNUMBER_OVER.getMessage());
        wishListRepository.save(new WishList(member, wishListname));
    }
    /**
     * 위시리스트에 포스트 추가 및 수정
     * 포스트가 다른 위시리스트에 들어있으면 삭제 후 목표 위시리스트에 추가
     */
    @Transactional
    public void addPostToWishList(Long memberId, Long wishListId, Long postId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        WishList wishList = wishListRepository.findById(wishListId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        // 멤버나 포스트가 삭제된 경우
        if(member.getStatus().equals(MemberStatus.DELETED) || post.getStatus().equals(PostStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //다른 유저의 위시리스트일 경우
        if(!wishList.getMember().equals(member)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        //다른 위시리스트에 들어있으면 삭제
        WishListPost oldWishListPost = wishListPostRepository.findWishListPostByPost(post);
        if(oldWishListPost != null) {
            wishListPostRepository.delete(oldWishListPost);
        }

        //위시리스트에 추가
        WishListPostId wishListPostId = new WishListPostId(wishListId, postId);
        //위시리스트에 포스트 추가
        WishListPost wishListPost = new WishListPost(wishListPostId, wishList, post);
        wishListPostRepository.save(wishListPost);
    }
    /**
     * 위시리스트 이름 수정
     */
    @Transactional
    public void renameWishList(Long memberId, Long wishListId, String newName) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        // 멤버가 삭제된 경우
        if(member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        WishList wishList = wishListRepository.findById(wishListId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //다른 유저의 위시리스트일 경우
        if(!wishList.getMember().equals(member)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        wishList.setName(newName);
    }

    /**
     * 위시리스트에 있는 포스트 삭제
     */
    @Transactional
    public void deletePostFromWishList(Long memberId, Long wishListId, Long postId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        WishList wishList = wishListRepository.findById(wishListId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        // 멤버나 포스트가 삭제된 경우
        if(member.getStatus().equals(MemberStatus.DELETED) || post.getStatus().equals(PostStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        //다른 유저의 위시리스트일 경우
        if(!wishList.getMember().equals(member)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());

        WishListPostId wishListPostId = new WishListPostId(wishListId, postId);
        wishListPostRepository.deleteWishListPostByWishListPostId(wishListPostId);
    }

    /**
     * 위시리스트 삭제
     * 위시리스트포스트 전부 삭제하고 나서 위시리스트 삭제
     */
    @Transactional
    public void deleteWishListWithPost(Long memberId, Long wishListId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());
        WishList wishList = wishListRepository.findById(wishListId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //다른 유저의 위시리스트일 경우
        if(!wishList.getMember().equals(member)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        //위시리스트에 포함되어 있는 모든 위시리스트포스트 삭제
        wishListPostRepository.deleteWishListPostsByWishListWishListId(wishListId);
        //위시리스트 삭제
        wishListRepository.delete(wishList);
    }

    /**
     * 위시리스트 목록을 조회할 때
     */
    public List<WishListDto> getWishLists(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());

        List<WishList> memberWishLists = wishListRepository.findWishListsByMember(member);
        return memberWishLists.stream()
                .map(wishList -> new WishListDto(wishList.getWishListId(), wishList.getName()))
                .collect(toList());
    }

    /**
     * 포스트를 위시리스트에 추가할 때 보여줄 위시리스트 목록 및 포함여부
     */
    public List<WishListAddListDto> getWishListsWithIncluded(Long memberId, Long postId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        if(member.getStatus().equals(MemberStatus.DELETED)) throw new NotFoundException(ResponseMessages.NOT_FOUND.getMessage());

        //기존에 위시리스트에 포스트 포함 정보 조회
        WishListPost oldWishListPost = wishListPostRepository.findWishListPostByPost(post);
        //멤버의 모든 위시리스트 조회
        List<WishList> memberWishLists = wishListRepository.findWishListsByMember(member);
        //현재 포스트가 아무 위시리스트에 포함되어 있지 않을 경우
        if(oldWishListPost == null) {
            return memberWishLists.stream()
                    .map(wishList -> new WishListAddListDto(wishList.getWishListId(), wishList.getName(),false))
                    .collect(toList());
        }
        //포스트가 어느 위시리스트에 포함되어 있는 경우
        else {
            return memberWishLists.stream()
                    .map(wishList -> new WishListAddListDto(wishList.getWishListId(), wishList.getName(), oldWishListPost.getWishList().equals(wishList)))
                    .collect(toList());
        }

    }

    /**
     * 위시리스트에 있는 포스트 목록 조회
     * 본인의 위시리스트 내용만 볼 수 있게 설정
     */
    public WishListPostListResponseDto getWishListPostsInWishList(Long memberId, Long wishListId,int page, int pageSize) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        WishList wishList = wishListRepository.findById(wishListId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        //다른 유저의 위시리스트일 경우
        if(!wishList.getMember().equals(member)) throw new ForbiddenException(ResponseMessages.FORBIDDEN.getMessage());
        //위시리스트에 있는 포스트 목록 조회
        return wishListRepository.getWishListPostDtos(wishListId, page, pageSize);
    }
}
