package javaiscoffee.polaroad.post;

import javaiscoffee.polaroad.security.CustomUserDetails;
import javaiscoffee.polaroad.wrapper.RequestWrapperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/write")
    public ResponseEntity<Post> savePost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RequestWrapperDto<PostSaveDto> requestWrapperDto) {
        Long memberId = userDetails.getMemberId();
        PostSaveDto postSaveDto = requestWrapperDto.getData();
        log.info("저장하려는 포스트 Dto = {}",postSaveDto);
        return postService.savePost(postSaveDto, memberId);
    }

    @PostMapping("/edit/{postId}")
    public ResponseEntity<Post> editPost(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody RequestWrapperDto<PostSaveDto> requestWrapperDto, @PathVariable(name = "postId") Long postId) {
        Long memberId = userDetails.getMemberId();
        PostSaveDto postSaveDto = requestWrapperDto.getData();
        log.info("수정하려는 포스트 Dto = {}",postSaveDto);
        return postService.editPost(postSaveDto, memberId, postId);
    }

    @PostMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "postId") Long postId) {
        Long memberId = userDetails.getMemberId();
        return postService.deletePost(postId, memberId);
    }
}
