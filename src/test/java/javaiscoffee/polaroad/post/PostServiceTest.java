package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.login.LoginService;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.post.card.CardSaveDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
class PostServiceTest {
    @MockBean
    private LoginService loginService;
    @MockBean private PostService postService;

    @BeforeEach
    void setup() {

        RegisterDto registerDto = new RegisterDto();
        registerDto.setEmail("aaa@naver.com");
        registerDto.setName("박자바");
        registerDto.setNickname("자바커피");
        registerDto.setPassword("a123123!");
        loginService.register(registerDto);
    }

    @Test
    @DisplayName("포스트 생성 성공 테스트 1. 카드, 해쉬태그 있을 경우")
    public void successCreatePost () {
        //포스트 정보 설정
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setTitle("꽃놀이 명당 추천");
        postSaveDto.setRoutePoint("좌표-좌표;좌표-좌표");
        postSaveDto.setThumbnailIndex(0);
        postSaveDto.setConcept(PostConcept.CITY);
        postSaveDto.setRegion(PostRegion.SEOUL);

        //카드 배열 설정
        CardSaveDto cardSaveDto = new CardSaveDto();
        cardSaveDto.setContent("1번카드");
        cardSaveDto.setImage("카드이미지");
        cardSaveDto.setLocation("서울특별시");
        cardSaveDto.setLatitude(1.23);
        cardSaveDto.setLongtitude(1.23);
        ArrayList<CardSaveDto> cards = new ArrayList<>();
        cards.add(cardSaveDto);
        postSaveDto.setCards(cards);

        //해쉬태그 배열 설정
        ArrayList<String> tags = new ArrayList<>();
        tags.add("1번 태그");
        tags.add("2번 태그");
        postSaveDto.setHashtags(tags);

        when(postService.savePost(any(PostSaveDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(new Post(), HttpStatus.OK));

        ResponseEntity<Post> response = postService.savePost(postSaveDto, 1l);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("포스트 생성 성공 테스트 2. 카드, 해쉬태그 없을 경우")
    public void successCreatePostWithNoCardAndHashtag () {
        //포스트 정보 설정
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setTitle("꽃놀이 명당 추천");
        postSaveDto.setRoutePoint("좌표-좌표;좌표-좌표");
        postSaveDto.setThumbnailIndex(0);
        postSaveDto.setConcept(PostConcept.CITY);
        postSaveDto.setRegion(PostRegion.SEOUL);

        when(postService.savePost(any(PostSaveDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(new Post(), HttpStatus.OK));

        ResponseEntity<Post> response = postService.savePost(postSaveDto, 1l);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("포스트 생성 실패 테스트 1. 멤버가 없을 경우")
    public void failedByNotMemberWhenCreatePost () {
        //포스트 정보 설정
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setTitle("꽃놀이 명당 추천");
        postSaveDto.setRoutePoint("좌표-좌표;좌표-좌표");
        postSaveDto.setThumbnailIndex(0);
        postSaveDto.setConcept(PostConcept.CITY);
        postSaveDto.setRegion(PostRegion.SEOUL);

        //카드 배열 설정
        CardSaveDto cardSaveDto = new CardSaveDto();
        cardSaveDto.setContent("1번카드");
        cardSaveDto.setImage("카드이미지");
        cardSaveDto.setLocation("서울특별시");
        cardSaveDto.setLatitude(1.23);
        cardSaveDto.setLongtitude(1.23);
        ArrayList<CardSaveDto> cards = new ArrayList<>();
        cards.add(cardSaveDto);
        postSaveDto.setCards(cards);

        //해쉬태그 배열 설정
        ArrayList<String> tags = new ArrayList<>();
        tags.add("1번 태그");
        tags.add("2번 태그");
        postSaveDto.setHashtags(tags);

        when(postService.savePost(any(PostSaveDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        ResponseEntity<Post> response = postService.savePost(postSaveDto, 10l);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("포스트 생성 실패 테스트 2. 해쉬태그가 개수가 최대 한도를 초과 할 경우")
    public void failedByManyHashtagsWhenCreatePost () {
        //포스트 정보 설정
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setTitle("꽃놀이 명당 추천");
        postSaveDto.setRoutePoint("좌표-좌표;좌표-좌표");
        postSaveDto.setThumbnailIndex(0);
        postSaveDto.setConcept(PostConcept.CITY);
        postSaveDto.setRegion(PostRegion.SEOUL);

        //카드 배열 설정
        CardSaveDto cardSaveDto = new CardSaveDto();
        cardSaveDto.setContent("1번카드");
        cardSaveDto.setImage("카드이미지");
        cardSaveDto.setLocation("서울특별시");
        cardSaveDto.setLatitude(1.23);
        cardSaveDto.setLongtitude(1.23);
        ArrayList<CardSaveDto> cards = new ArrayList<>();
        cards.add(cardSaveDto);
        postSaveDto.setCards(cards);

        //해쉬태그 배열 설정
        ArrayList<String> tags = new ArrayList<>();
        tags.add("1번 태그");
        tags.add("2번 태그");
        tags.add("3번 태그");
        tags.add("4번 태그");
        tags.add("5번 태그");
        tags.add("6번 태그");
        tags.add("7번 태그");
        tags.add("8번 태그");
        tags.add("9번 태그");
        tags.add("10번 태그");
        tags.add("11번 태그");
        postSaveDto.setHashtags(tags);

        when(postService.savePost(any(PostSaveDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));


        ResponseEntity<Post> response = postService.savePost(postSaveDto, 10l);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    @Test
    @DisplayName("포스트 생성 실패 테스트 3. 카드가 최대 개수를 초과할 경우")
    public void failedByManyCardsWhenCreatePost () {
        //포스트 정보 설정
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setTitle("꽃놀이 명당 추천");
        postSaveDto.setRoutePoint("좌표-좌표;좌표-좌표");
        postSaveDto.setThumbnailIndex(0);
        postSaveDto.setConcept(PostConcept.CITY);
        postSaveDto.setRegion(PostRegion.SEOUL);

        //카드 배열 설정
        CardSaveDto cardSaveDto = new CardSaveDto();
        cardSaveDto.setContent("1번카드");
        cardSaveDto.setImage("카드이미지");
        cardSaveDto.setLocation("서울특별시");
        cardSaveDto.setLatitude(1.23);
        cardSaveDto.setLongtitude(1.23);
        ArrayList<CardSaveDto> cards = new ArrayList<>();
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        cards.add(cardSaveDto);
        postSaveDto.setCards(cards);

        //해쉬태그 배열 설정
        ArrayList<String> tags = new ArrayList<>();
        tags.add("1번 태그");
        tags.add("2번 태그");
        postSaveDto.setHashtags(tags);

        when(postService.savePost(any(PostSaveDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        ResponseEntity<Post> response = postService.savePost(postSaveDto, 10l);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("포스트 생성 실패 테스트 4. 썸네일 번호가 잘못된 경우")
    public void failedByWrongThumbnailIndexWhenCreatePost () {
        //포스트 정보 설정
        PostSaveDto postSaveDto = new PostSaveDto();
        postSaveDto.setTitle("꽃놀이 명당 추천");
        postSaveDto.setRoutePoint("좌표-좌표;좌표-좌표");
        postSaveDto.setThumbnailIndex(10);
        postSaveDto.setConcept(PostConcept.CITY);
        postSaveDto.setRegion(PostRegion.SEOUL);

        //카드 배열 설정
        CardSaveDto cardSaveDto = new CardSaveDto();
        cardSaveDto.setContent("1번카드");
        cardSaveDto.setImage("카드이미지");
        cardSaveDto.setLocation("서울특별시");
        cardSaveDto.setLatitude(1.23);
        cardSaveDto.setLongtitude(1.23);
        ArrayList<CardSaveDto> cards = new ArrayList<>();
        cards.add(cardSaveDto);
        postSaveDto.setCards(cards);

        //해쉬태그 배열 설정
        ArrayList<String> tags = new ArrayList<>();
        tags.add("1번 태그");
        tags.add("2번 태그");
        postSaveDto.setHashtags(tags);

        when(postService.savePost(any(PostSaveDto.class), any(Long.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        ResponseEntity<Post> response = postService.savePost(postSaveDto, 10l);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}