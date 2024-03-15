package javaiscoffee.polaroad.post.hashtag;

import javaiscoffee.polaroad.post.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 포스트 생성, 수정할 때 사용되는 서비스
 */
@Service
@Slf4j
public class HashtagService {
    private final HashtagRepository hashtagRepository;  //해쉬태그 정보만 저장
    private final PostHashtagRepository postHashtagRepository;  //포스트의 해쉬태그 정보 저장

    @Autowired
    public HashtagService(HashtagRepository hashtagRepository, PostHashtagRepository postHashtagRepository) {
        this.hashtagRepository = hashtagRepository;
        this.postHashtagRepository = postHashtagRepository;
    }

    /**
     * 해쉬태그 종류를 생성하는 메서드
     * 새로운 해쉬태그를 저장할 때 사용
     */
    public Hashtag saveHashtag(String name) {
        Hashtag hashtag = new Hashtag();
        hashtag.setName(name);
        hashtagRepository.save(hashtag);
        return hashtag;
    }


    /**
     * 포스트 생성시 사용하는 해쉬태그 저장 메서드
     */
    public PostHashtag savePostHashtag(String tagName, Post post) {
        Hashtag hashtag = hashtagRepository.findByName(tagName)
                .orElseGet(() -> hashtagRepository.save(new Hashtag(tagName)));
        PostHashtag postHashtag = new PostHashtag(new PostHashtagId(hashtag.getHashtagId(), post.getPostId()), hashtag, post);
        log.info("저장된 postHashtag = {}",postHashtag);
        return postHashtagRepository.save(postHashtag);
    }

    /**
     * 포스트 수정할 때 해쉬태그 수정하는 메서드
     */
    public void editPostHashtags(List<String> updatedHashtags, Post post) {
        List<PostHashtag> oldPostHashtag = postHashtagRepository.findByPost_PostId(post.getPostId());

        //기존 해쉬태그와 수정된 해쉬태그 리스트 비교해서 추가해야 할 해쉬태그만 골라내기
        Set<String> updatedTagNames = new HashSet<>(updatedHashtags);
        oldPostHashtag.forEach(tag -> {
            if(!updatedTagNames.contains(tag.getHashtag().getName())) {
                postHashtagRepository.delete(tag); // 더 이상 사용되지 않는 태그 삭제
            }
            else {
                updatedHashtags.remove(tag.getHashtag().getName()); // 이미 존재하는 태그는 목록에서 삭제
            }
        });

        //새로 추가해야 할 해쉬태그 추가
        updatedHashtags.forEach(tagName -> {
            Hashtag hashtag = hashtagRepository.findByName(tagName)
                    .orElseGet(() -> hashtagRepository.save(new Hashtag(tagName)));
            PostHashtag postHashtag = new PostHashtag(new PostHashtagId(hashtag.getHashtagId(), post.getPostId()), hashtag, post);
            postHashtagRepository.save(postHashtag);
        });
    }

    public Long getHashtagIdByName(String tagName) {
        Hashtag hashtag = hashtagRepository.findByName(tagName).orElse(null);
        if(hashtag==null) return null;
        return hashtag.getHashtagId();
    }
}
