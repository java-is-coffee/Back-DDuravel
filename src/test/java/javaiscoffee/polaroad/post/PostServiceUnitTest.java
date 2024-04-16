package javaiscoffee.polaroad.post;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import jakarta.persistence.EntityManager;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.member.*;
import javaiscoffee.polaroad.post.card.Card;
import javaiscoffee.polaroad.post.card.CardSaveDto;
import javaiscoffee.polaroad.post.card.CardService;
import javaiscoffee.polaroad.post.good.PostGood;
import javaiscoffee.polaroad.post.good.PostGoodBatchUpdator;
import javaiscoffee.polaroad.post.good.PostGoodId;
import javaiscoffee.polaroad.post.good.PostGoodRepository;
import javaiscoffee.polaroad.post.hashtag.Hashtag;
import javaiscoffee.polaroad.post.hashtag.HashtagService;
import javaiscoffee.polaroad.post.hashtag.PostHashtag;
import javaiscoffee.polaroad.post.hashtag.PostHashtagId;
import javaiscoffee.polaroad.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PostServiceUnitTest {
    @Mock private LoginService loginService;
    @InjectMocks
    private PostService postService;
    @Mock private PostRepository postRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private PostGoodRepository postGoodRepository;
    @Mock private PostGoodBatchUpdator postGoodBatchUpdator;
    @Mock private CardService cardService;
    @Mock private HashtagService hashtagService;
    @Mock private RedisService redisService;
    @Mock EntityManager entityManager;
    private Member testMember1;
    private Member testMember2;
    private FixtureMonkey sut = FixtureMonkey.builder().build();
    ArbitraryBuilder<PostSaveDto> successPostBuilder = sut.giveMeBuilder(PostSaveDto.class)
            .set("thumbnailIndex", 9)
            .size("cards", 10)
            .size("hashtags", 10);
    ArbitraryBuilder<PostSaveDto> failPostBuilder = sut.giveMeBuilder(PostSaveDto.class)
            .set("thumbnailIndex", -1)
            .size("cards", 11)
            .size("hashtags", 11);


    @BeforeEach
    void setup() {
        testMember1 = new Member(1L,"aaa@naver.com","aaa","신짱구","짱구르미","프로필",0,0,0, MemberRole.USER, LocalDateTime.now(),LocalDateTime.now(), MemberStatus.ACTIVE,null,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
        testMember2 = new Member(2L,"bbb@naver.com","bbb","김철수","철수세미","프로필",0,0,0, MemberRole.USER, LocalDateTime.now(),LocalDateTime.now(), MemberStatus.ACTIVE,null,new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    }

    @Test
    @DisplayName("포스트 생성 성공 테스트")
    public void successCreatePost () {
        //given
        PostSaveDto postSaveDto = successPostBuilder.sample();
        log.info("postSaveDto = {}", postSaveDto);
        Post testPost = new Post(1L, "제목", testMember1, "", 0, 0, 0, PostConcept.FOOD, PostRegion.BUSAN, PostStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        BeanUtils.copyProperties(postSaveDto,testPost);
        testPost.setMember(testMember1);
        //when
        when(memberRepository.findById(any(Long.class))).thenReturn(Optional.of(testMember1));
        when(postRepository.save(any(Post.class))).thenReturn(testPost);
        when(hashtagService.savePostHashtag(any(String.class),any(Post.class))).thenReturn(new PostHashtag(new PostHashtagId(1L,1L),new Hashtag(),testPost));
        when(cardService.saveCard(any(Card.class))).thenReturn(new Card());
        doNothing().when(redisService).saveCachingPostInfo(any(PostInfoCachingDto.class), any(Long.class));
        ResponseEntity<Post> response = postService.savePost(postSaveDto, 1L);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMember()).isEqualTo(testMember1);
    }

    @Test
    @DisplayName("포스트 생성 실패 테스트")
    public void failedCreatePost() {
        //given
        PostSaveDto postSaveDto = failPostBuilder.sample();
        log.info("postSaveDto = {}", postSaveDto);
        Post testPost = new Post(1L, "제목", testMember1, "", 0, 0, 0, PostConcept.FOOD, PostRegion.BUSAN, PostStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        BeanUtils.copyProperties(postSaveDto,testPost);
        testPost.setMember(testMember1);
        //when & then
        Assertions.assertThatThrownBy(() -> postService.savePost(postSaveDto,1L)).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("포스트 수정 성공 테스트")
    public void successEditPost() {
        //given
        PostSaveDto postSaveDto = successPostBuilder.sample();
        PostSaveDto editPostDto = successPostBuilder.sample();
        log.info("postSaveDto = {}", postSaveDto);
        log.info("editPostDto = {}", editPostDto);
        Post savedPost = new Post(1L, "제목", testMember1, "", 0, 0, 0, PostConcept.FOOD, PostRegion.BUSAN, PostStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());;
        Post editedPost = Post.builder().postId(1L).member(testMember2).build();
        BeanUtils.copyProperties(postSaveDto,savedPost);
        BeanUtils.copyProperties(editPostDto,editedPost);
        //when
        when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(savedPost));
        when(memberRepository.findById(any(Long.class))).thenReturn(Optional.of(testMember1));
        when(hashtagService.editPostHashtags(any(List.class), eq(savedPost))).thenReturn(new ArrayList<>());
        when(cardService.editCards(any(List.class), eq(savedPost), eq(testMember1))).thenReturn(new ArrayList<>());
        doNothing().when(redisService).updateCachingPost(any(PostInfoCachingDto.class), any(Long.class));
        //then
        ResponseEntity<Post> response = postService.editPost(editPostDto, testMember1.getMemberId(), savedPost.getPostId());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo(editPostDto.getTitle());
        assertThat(response.getBody().getRoutePoint()).isEqualTo(editPostDto.getRoutePoint());
        assertThat(response.getBody().getThumbnailIndex()).isEqualTo(editPostDto.getThumbnailIndex());
        assertThat(response.getBody().getConcept()).isEqualTo(editPostDto.getConcept());
        assertThat(response.getBody().getRegion()).isEqualTo(editPostDto.getRegion());
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 1. 글 작성자가 아닌 경우")
    public void failedEditPostByNotWritter() {
        //given
        PostSaveDto postSaveDto = successPostBuilder.sample();
        PostSaveDto editPostDto = successPostBuilder.sample();
        log.info("postSaveDto = {}", postSaveDto);
        log.info("editPostDto = {}", editPostDto);
        Post savedPost = new Post(1L, "제목", testMember1, "", 0, 0, 0, PostConcept.FOOD, PostRegion.BUSAN, PostStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());;
        Post editedPost = Post.builder().postId(1L).member(testMember2).build();
        BeanUtils.copyProperties(postSaveDto,savedPost);
        BeanUtils.copyProperties(editPostDto,editedPost);
        //when
        when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(savedPost));
        when(memberRepository.findById(any(Long.class))).thenReturn(Optional.of(testMember1));
        //then
        Assertions.assertThatThrownBy(() -> postService.editPost(editPostDto, testMember2.getMemberId(), savedPost.getPostId())).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 2. 글 작성자가 활동 상태가 아닌 경우")
    public void failedEditPostByWritterStatus() {
        //given
        PostSaveDto postSaveDto = successPostBuilder.sample();
        PostSaveDto editPostDto = successPostBuilder.sample();
        log.info("postSaveDto = {}", postSaveDto);
        log.info("editPostDto = {}", editPostDto);
        Post savedPost = new Post(1L, "제목", testMember1, "", 0, 0, 0, PostConcept.FOOD, PostRegion.BUSAN, PostStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());;
        Post editedPost = Post.builder().postId(1L).member(testMember2).build();
        BeanUtils.copyProperties(postSaveDto,savedPost);
        BeanUtils.copyProperties(editPostDto,editedPost);
        testMember1.setStatus(MemberStatus.SUSPENDED);
        //when
        when(postRepository.findById(any(Long.class))).thenReturn(Optional.of(savedPost));
        when(memberRepository.findById(any(Long.class))).thenReturn(Optional.of(testMember1));
        //then
        Assertions.assertThatThrownBy(() -> postService.editPost(editPostDto, testMember1.getMemberId(), savedPost.getPostId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트 3. 썸네일 번호가 잘못된 경우")
    public void failedEditPostByWrongContent() {
        //given
        PostSaveDto postSaveDto = successPostBuilder.sample();
        PostSaveDto editPostDto = failPostBuilder.sample();
        log.info("postSaveDto = {}", postSaveDto);
        log.info("editPostDto = {}", editPostDto);
        Post savedPost = new Post(1L, "제목", testMember1, "", 0, 0, 0, PostConcept.FOOD, PostRegion.BUSAN, PostStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());;
        Post editedPost = Post.builder().postId(1L).member(testMember2).build();
        BeanUtils.copyProperties(postSaveDto,savedPost);
        BeanUtils.copyProperties(editPostDto,editedPost);
        //when
        //then
        Assertions.assertThatThrownBy(() -> postService.editPost(editPostDto, testMember1.getMemberId(), savedPost.getPostId())).isInstanceOf(BadRequestException.class);
    }
    @Test
    @DisplayName("포스트 수정 실패 테스트 4. 카드 개수또는 해쉬태그 개수가 잘못된 경우")
    public void failedEditPostByWrongCards() {
        //given
        PostSaveDto postSaveDto = successPostBuilder.sample();
        PostSaveDto editPostDto = failPostBuilder.sample();
        editPostDto.setThumbnailIndex(0);
        log.info("postSaveDto = {}", postSaveDto);
        log.info("editPostDto = {}", editPostDto);
        Post savedPost = new Post(1L, "제목", testMember1, "", 0, 0, 0, PostConcept.FOOD, PostRegion.BUSAN, PostStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());;
        Post editedPost = Post.builder().postId(1L).member(testMember2).build();
        BeanUtils.copyProperties(postSaveDto,savedPost);
        BeanUtils.copyProperties(editPostDto,editedPost);
        //when
        //then
        Assertions.assertThatThrownBy(() -> postService.editPost(editPostDto, testMember1.getMemberId(), savedPost.getPostId())).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("포스트 삭제 성공 테스트")
    public void successPostDelete() {
        //given
        MemberSimpleInfoDto simpleMemberInfoDto = new MemberSimpleInfoDto(1L, MemberStatus.ACTIVE);
        PostSimpleInfoDto simplePostInfoDto = new PostSimpleInfoDto(1L, testMember1.getMemberId(), PostStatus.ACTIVE);
        //when
        when(postRepository.getPostSimpleInfo(any(Long.class))).thenReturn(Optional.of(simplePostInfoDto));
        when(memberRepository.getMemberSimpleInfo(any(Long.class))).thenReturn(Optional.of(simpleMemberInfoDto));
        doNothing().when(postRepository).updatePostStatus(any(Long.class),any(PostStatus.class));
        doNothing().when(memberRepository).addMemberPostNumber(any(Long.class),any(Integer.class));
        //then
        assertThat(postService.deletePost(simplePostInfoDto.getPostId(), simpleMemberInfoDto.getMemberId()).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 1. 포스트가 삭제되어있는 경우")
    public void failedPostDeleteByDeletedPost() {
        //given
        MemberSimpleInfoDto simpleMemberInfoDto = new MemberSimpleInfoDto(1L, MemberStatus.ACTIVE);
        PostSimpleInfoDto simplePostInfoDto = new PostSimpleInfoDto(1L, testMember1.getMemberId(), PostStatus.DELETED);
        //when
        when(postRepository.getPostSimpleInfo(any(Long.class))).thenReturn(Optional.of(simplePostInfoDto));
        //then
        Assertions.assertThatThrownBy(() -> postService.deletePost(simplePostInfoDto.getPostId(), simplePostInfoDto.getMemberId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 2. 멤버가 활동 상태가 아닌 경우")
    public void failedPostDeleteByNotActiveMember() {
        //given
        MemberSimpleInfoDto simpleMemberInfoDto = new MemberSimpleInfoDto(1L, MemberStatus.SUSPENDED);
        PostSimpleInfoDto simplePostInfoDto = new PostSimpleInfoDto(1L, testMember1.getMemberId(), PostStatus.ACTIVE);
        //when
        when(postRepository.getPostSimpleInfo(any(Long.class))).thenReturn(Optional.of(simplePostInfoDto));
        when(memberRepository.getMemberSimpleInfo(any(Long.class))).thenReturn(Optional.of(simpleMemberInfoDto));
        //then
        Assertions.assertThatThrownBy(() -> postService.deletePost(simplePostInfoDto.getPostId(), simplePostInfoDto.getMemberId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트 3. 포스트 작성자가 아닌 경우")
    public void failedPostDeleteByNotWritter() {
        //given
        MemberSimpleInfoDto simpleMemberInfoDto = new MemberSimpleInfoDto(1L, MemberStatus.ACTIVE);
        PostSimpleInfoDto simplePostInfoDto = new PostSimpleInfoDto(1L, testMember1.getMemberId(), PostStatus.ACTIVE);
        //when
        when(postRepository.getPostSimpleInfo(any(Long.class))).thenReturn(Optional.of(simplePostInfoDto));
        when(memberRepository.getMemberSimpleInfo(any(Long.class))).thenReturn(Optional.of(simpleMemberInfoDto));
        //then
        Assertions.assertThatThrownBy(() -> postService.deletePost(simplePostInfoDto.getPostId(), testMember2.getMemberId())).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("포스트 추천 생성 성공 테스트")
    public void successGoodPostSave() {
        //given
        MemberSimpleInfoDto simpleMemberInfoDto = new MemberSimpleInfoDto(1L, MemberStatus.ACTIVE);
        PostSimpleInfoDto simplePostInfoDto = new PostSimpleInfoDto(1L, testMember2.getMemberId(), PostStatus.ACTIVE);
        PostGoodId postGoodId = new PostGoodId(simpleMemberInfoDto.getMemberId(), simplePostInfoDto.getPostId());
        //when
        when(postRepository.getPostSimpleInfo(any(Long.class))).thenReturn(Optional.of(simplePostInfoDto));
        when(memberRepository.getMemberSimpleInfo(any(Long.class))).thenReturn(Optional.of(simpleMemberInfoDto));
        when(entityManager.getReference(eq(Member.class), eq(simpleMemberInfoDto.getMemberId()))).thenReturn(testMember1);
        when(entityManager.getReference(eq(Post.class), eq(simplePostInfoDto.getPostId()))).thenReturn(new Post());
        when(postGoodRepository.existsById(any(PostGoodId.class))).thenReturn(false);
        when(postGoodRepository.save(any(PostGood.class))).thenReturn(null);
        doNothing().when(postGoodBatchUpdator).increasePostGoodCount(any(Long.class));
        //then
        postService.postGoodToggle(simpleMemberInfoDto.getMemberId(), simplePostInfoDto.getPostId());
        verify(postRepository).getPostSimpleInfo(simpleMemberInfoDto.getMemberId());
        verify(memberRepository).getMemberSimpleInfo(simplePostInfoDto.getPostId());
        verify(postGoodRepository).existsById(postGoodId);
    }

    @Test
    @DisplayName("포스트 추천 삭제 성공 테스트")
    public void successGoodPostDelete() {
        //given
        MemberSimpleInfoDto simpleMemberInfoDto = new MemberSimpleInfoDto(1L, MemberStatus.ACTIVE);
        PostSimpleInfoDto simplePostInfoDto = new PostSimpleInfoDto(1L, testMember2.getMemberId(), PostStatus.ACTIVE);
        PostGoodId postGoodId = new PostGoodId(simpleMemberInfoDto.getMemberId(), simplePostInfoDto.getPostId());
        //when
        when(postRepository.getPostSimpleInfo(any(Long.class))).thenReturn(Optional.of(simplePostInfoDto));
        when(memberRepository.getMemberSimpleInfo(any(Long.class))).thenReturn(Optional.of(simpleMemberInfoDto));
        when(entityManager.getReference(eq(Member.class), eq(simpleMemberInfoDto.getMemberId()))).thenReturn(testMember1);
        when(entityManager.getReference(eq(Post.class), eq(simplePostInfoDto.getPostId()))).thenReturn(new Post());
        when(postGoodRepository.existsById(any(PostGoodId.class))).thenReturn(true);
        doNothing().when(postGoodRepository).deleteById(any(PostGoodId.class));
        doNothing().when(postGoodBatchUpdator).decreasePostGoodCount(any(Long.class));
        //then
        postService.postGoodToggle(simpleMemberInfoDto.getMemberId(), simplePostInfoDto.getPostId());
        verify(postRepository).getPostSimpleInfo(simpleMemberInfoDto.getMemberId());
        verify(memberRepository).getMemberSimpleInfo(simplePostInfoDto.getPostId());
        verify(postGoodRepository).existsById(postGoodId);
    }
}