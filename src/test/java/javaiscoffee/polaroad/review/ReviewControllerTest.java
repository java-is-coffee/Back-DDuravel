package javaiscoffee.polaroad.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import javaiscoffee.polaroad.login.RegisterDto;
import javaiscoffee.polaroad.member.JpaMemberRepository;
import javaiscoffee.polaroad.member.Member;
import javaiscoffee.polaroad.member.MemberRepository;
import javaiscoffee.polaroad.post.Post;
import javaiscoffee.polaroad.security.CustomUserDetails;
import javaiscoffee.polaroad.security.JwtTokenProvider;
import javaiscoffee.polaroad.security.TokenDto;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(properties = {"JWT_SECRET_KEY=3123755132fdfds4daas4551af789d59f36977df5093be12c2314515135ddasg1f5k12hdfhjk412bh531uiadfi14b14bwebs52"})
@AutoConfigureMockMvc
@Transactional
public class ReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReviewService reviewService;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JpaMemberRepository jpaMemberRepository;

    private ReviewDto reviewDto;
    private Long memberId;
    private Long postId;
    private String myJwtToken;
    private static final FixtureMonkey fm = FixtureMonkey.builder()
            .objectIntrospector(ConstructorPropertiesArbitraryIntrospector.INSTANCE)
            .build();

    @BeforeEach
    void setup() {
        Member member = fm.giveMeBuilder(Member.class)
                .set("memberId", 1L)
                .set("name", "박자바")
                .set("nickname","자바커피")
                .set("email","aaa@naver.com")
                .set("password", "a123123!")
                .sample();
        memberRepository.save(member);
        Post post = fm.giveMeBuilder(Post.class)
                .set("postId", 1L).sample();
        ReviewDto dto = fm.giveMeBuilder(ReviewDto.class)
                .set("memberId", member.getMemberId())
                .set("postId", post.getPostId())
                .sample();

        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);

        CustomUserDetails customUserDetails = fm.giveMeBuilder(CustomUserDetails.class)
                .set("memberId", 1L).sample();

        Authentication auth = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(auth);
        String jwtToken = "your_generated_jwt_token_here";
//        TokenDto tokenDto = jwtTokenProvider.generateToken(auth);


        this.memberId = member.getMemberId();
        this.postId = post.getPostId();
        this.reviewDto = dto;
        this.myJwtToken = jwtToken;
    }

//    @Test
    @DisplayName("리뷰 작성 성공")
//    @WithCustomMockUser
    public void writeReviewSuccess() throws Exception{
        ResponseReviewDto responseReviewDto = new ResponseReviewDto();
        when(reviewService.createReview(reviewDto, 1L, 1L)).thenReturn(responseReviewDto);

        MockHttpServletRequestBuilder requestBuilder = post("/write/{postId}", postId)
                .header("Authorization", "Bearer " + myJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewDto));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.reviewId").exists())
                .andReturn();
    }

}
