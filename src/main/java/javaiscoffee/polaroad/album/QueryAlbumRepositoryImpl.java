package javaiscoffee.polaroad.album;

import com.querydsl.jpa.impl.JPAQueryFactory;
import ext.javaiscoffee.polaroad.album.QAlbum;
import ext.javaiscoffee.polaroad.album.albumCard.QAlbumCard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import javaiscoffee.polaroad.album.albumCard.AlbumCard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class QueryAlbumRepositoryImpl implements QueryAlbumRepository{

    private final JPAQueryFactory queryFactory;

    public QueryAlbumRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Slice<Album> findAlbumSlicedByMemberId(Long memberId, Pageable pageable) {
        QAlbum album = QAlbum.album;

        List<Album> albumList = queryFactory
                .selectFrom(album)
                .where(album.member.memberId.eq(memberId))
                .orderBy(album.createdTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(album)
                .where(album.member.memberId.eq(memberId))
                .fetchCount();

        boolean hasNextPage = pageable.getPageNumber() < (int) Math.ceil((double) totalCount / pageable.getPageSize()) - 1;

        return new SliceImpl<>(albumList, pageable, hasNextPage);
    }

    @Override
    public Slice<AlbumCard> findAlbumCardSlicedByAlbum(Album albumId, Pageable pageable) {
        QAlbumCard albumCard = QAlbumCard.albumCard;

        List<AlbumCard> albumCardList = queryFactory
                .selectFrom(albumCard)
                .where(albumCard.album.eq(albumId))
                .orderBy(albumCard.card.createdTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(albumCard)
                .where(albumCard.album.eq(albumId))
                .fetchCount();

        boolean hasNextPage = pageable.getPageNumber() < (int) Math.ceil((double) totalCount / pageable.getPageSize()) - 1;

        return new SliceImpl<>(albumCardList, pageable, hasNextPage);
    }

}
