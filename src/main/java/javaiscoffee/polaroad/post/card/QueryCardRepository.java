package javaiscoffee.polaroad.post.card;

import javaiscoffee.polaroad.post.*;

import java.util.List;

public interface QueryCardRepository {
    List<MapCardListDto> getMapCardListByKeyword(String searchKeyword, PostConcept concept, double swLatitude, double neLatitude, double swLongitude, double neLongitude, int pageSize);

    List<MapCardListDto> getMapCardListByHashtag(Long hashtagId, PostConcept concept, double swLatitude, double neLatitude, double swLongitude, double neLongitude, int pageSize);
}
