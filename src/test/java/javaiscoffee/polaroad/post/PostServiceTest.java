package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.config.JpaConfigTest;
import javaiscoffee.polaroad.exception.BadRequestException;
import javaiscoffee.polaroad.exception.ForbiddenException;
import javaiscoffee.polaroad.exception.NotFoundException;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.member.MemberStatus;
import javaiscoffee.polaroad.post.card.CardSaveDto;
import javaiscoffee.polaroad.redis.RedisService;
import javaiscoffee.polaroad.response.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

@Slf4j
@SpringBootTest(properties = {"JWT_SECRET_KEY=3123758a0d7ef02a46cba8bdd3f898dec8afc9f8470341af789d59f3695093be"},classes = {JpaConfigTest.class})
@Transactional(readOnly = true)
class PostServiceTest {
    @Autowired
    private PostService postService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private RedisService redisService;

    private PostSaveDto postSaveDto1;
    private Member member1;
    private Member member2;

    @BeforeEach
    void setup() {
        List<CardSaveDto> cards = new ArrayList<>();
        CardSaveDto cardSaveDto = new CardSaveDto();
        cardSaveDto.setContent("생성테스트");
        cardSaveDto.setImage("사진");
        cardSaveDto.setLocation("위치");
        cardSaveDto.setLatitude(0.1);
        cardSaveDto.setLongitude(0.1);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);

        List<String> hashTags = new ArrayList<>(){{
            add("태그1");
            add("태그2");
        }};

        postSaveDto1 = new PostSaveDto();
        postSaveDto1.setTitle("테스트");
        postSaveDto1.setRoutePoint("좌표-좌표");
        postSaveDto1.setThumbnailIndex(0);
        postSaveDto1.setConcept(PostConcept.CITY);
        postSaveDto1.setRegion(PostRegion.SEOUL);
        postSaveDto1.setCards(cards);
        postSaveDto1.setHashtags(hashTags);

        member1 = new Member("aaa@naver.com","박자바","자바커피","a123123!");
        member1.hashPassword(new BCryptPasswordEncoder());
        member2 = new Member("bbb@naver.com","김책상","장패드","b123123!");
        member2.hashPassword(new BCryptPasswordEncoder());
    }

    @Test
    @Transactional
    @DisplayName("포스트 생성 성공")
    void savePostSuccess() {
        doNothing().when(redisService).saveCachingPostInfo(any(), anyLong());
        memberRepository.save(member1);
        Member findMember = memberRepository.findByEmail(member1.getEmail()).orElseThrow(() -> new BadRequestException(ResponseMessages.SAVE_FAILED.getMessage()));
        postService.savePost(postSaveDto1, findMember.getMemberId());
    }

    @Test
    @Transactional
    @DisplayName("포스트 생성 실패 - 포스트 썸네일 번호가 카드 전체 개수를 넘어가서")
    void savePostFailByThumbnailIndex() {
        memberRepository.save(member1);
        Member findMember = memberRepository.findByEmail(member1.getEmail()).orElseThrow(() -> new BadRequestException(ResponseMessages.SAVE_FAILED.getMessage()));
        postSaveDto1.setThumbnailIndex(2);

        assertThatThrownBy(() -> postService.savePost(postSaveDto1,findMember.getMemberId())).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 생성 실패 - 포스트 해쉬태그 개수가 10개 넘어가서")
    void savePostFailByHashtagNumber() {
        memberRepository.save(member1);
        Member findMember = memberRepository.findByEmail(member1.getEmail()).orElseThrow(() -> new BadRequestException(ResponseMessages.SAVE_FAILED.getMessage()));
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");
        postSaveDto1.getHashtags().add("태그1");

        assertThatThrownBy(() -> postService.savePost(postSaveDto1,findMember.getMemberId())).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 생성 실패 - 포스트 카드 개수가 10개 넘어가서")
    void savePostFailByCardNumber() {
        memberRepository.save(member1);
        Member findMember = memberRepository.findByEmail(member1.getEmail()).orElseThrow(() -> new BadRequestException(ResponseMessages.SAVE_FAILED.getMessage()));
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());
        postSaveDto1.getCards().add(new CardSaveDto());

        assertThatThrownBy(() -> postService.savePost(postSaveDto1,findMember.getMemberId())).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 생성 실패 - 회원이 존재하지 않아서")
    void savePostFailByMember() {
        assertThatThrownBy(() -> postService.savePost(postSaveDto1,1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 수정 성공")
    void editPostSuccess() {
        memberRepository.save(member1);
        Member findMember = memberRepository.findByEmail(member1.getEmail()).get();
        Long postId = postService.savePost(postSaveDto1, findMember.getMemberId());
        postRepository.flush();

        Post savedPost = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        postSaveDto1.setTitle("수정된제목");
        postSaveDto1.setRoutePoint("수정된좌표");
        postSaveDto1.setThumbnailIndex(1);
        postSaveDto1.setConcept(PostConcept.FOOD);
        postSaveDto1.setRegion(PostRegion.BUSAN);

        CardSaveDto cardSaveDto1 = postSaveDto1.getCards().get(0);
        cardSaveDto1.setCardId(1L);
        cardSaveDto1.setContent("수정된카드1");
        CardSaveDto cardSaveDto2 = postSaveDto1.getCards().get(1);
        cardSaveDto2.setCardId(2L);
        cardSaveDto2.setContent("수정된카드2");

        List<String> hashtags = new ArrayList<>(){{
            add("수정된태그1");
            add("수정된태그2");
        }};
        postSaveDto1.setHashtags(hashtags);

        //when
        doNothing().when(redisService).updateCachingPost(any(), anyLong());

        ResponseEntity<Post> editResponse = postService.editPost(postSaveDto1, findMember.getMemberId(), savedPost.getPostId());
        PostInfoDto editedPost = postRepository.getPostInfoById(savedPost.getPostId(),findMember.getMemberId());

        //수정한 포스트 내용 비교
        assertThat(editResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(editedPost.getTitle()).isEqualTo("수정된제목");
        assertThat(editedPost.getRoutePoint()).isEqualTo("수정된좌표");
        assertThat(editedPost.getThumbnailIndex()).isEqualTo(1);
        assertThat(editedPost.getConcept()).isEqualTo(PostConcept.FOOD);
        assertThat(editedPost.getRegion()).isEqualTo(PostRegion.BUSAN);
    }

    @Test
    @Transactional
    @DisplayName("포스트 수정 실패 - 포스트가 존재하지 않음")
    void editPostFailByNotFoundPost() {
        memberRepository.save(member1);
        assertThatThrownBy(() -> postService.editPost(postSaveDto1, 1L, 10L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 수정 실패 - 멤버가 존재하지 않음")
    void editPostFailByNotFoundMember() {
        memberRepository.save(member1);
        Member member = memberRepository.findByEmail(member1.getEmail()).orElseThrow(() -> new NotFoundException(ResponseMessages.NOT_FOUND.getMessage()));
        postService.savePost(postSaveDto1,member.getMemberId());
        assertThatThrownBy(() -> postService.editPost(postSaveDto1, 10L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 수정 실패 - 포스트 생성자가 아닌데 수정하려고 하는 경우")
    void editPostFailByNotWriter() {
        memberRepository.save(member1);
        memberRepository.save(member2);
        Member writer = memberRepository.findByEmail(member1.getEmail()).get();
        Member noWriter = memberRepository.findByEmail(member2.getEmail()).get();
        Long postId = postService.savePost(postSaveDto1, writer.getMemberId());
        assertThatThrownBy(() -> postService.editPost(postSaveDto1, noWriter.getMemberId(), postId)).isInstanceOf(ForbiddenException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 수정 실패 - 생성자가 활동중인 상태가 아닌 경우")
    void editPostFailByWriterNotActive() {
        memberRepository.save(member1);
        Member member = memberRepository.findByEmail(member1.getEmail()).get();
        postService.savePost(postSaveDto1,member.getMemberId());
        member.setStatus(MemberStatus.DELETED);
        memberRepository.updateMember(member);
        assertThatThrownBy(() -> postService.editPost(postSaveDto1, 1L, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @DisplayName("포스트 수정 실패 - 카드 개수 범위 안에 썸네일 번호가 없을 경우")
    void editPostFailByWrongThumbnailIndex() {
        memberRepository.save(member1);
        Member member = memberRepository.findByEmail(member1.getEmail()).get();
        postService.savePost(postSaveDto1,member.getMemberId());
        postSaveDto1.setThumbnailIndex(10);
        assertThatThrownBy(() -> postService.editPost(postSaveDto1, 1L, 1L)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void deletePost() {
    }

    @Test
    void getPostList() {
    }

    @Test
    void getFollowingMemberPosts() {
    }

    @Test
    void getPostInfoById() {
    }

    @Test
    void getPostRankingList() {
    }

    @Test
    void postGoodToggle() {
    }
}