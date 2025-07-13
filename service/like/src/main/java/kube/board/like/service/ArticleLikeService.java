package kube.board.like.service;

import kube.board.like.entity.ArticleLike;
import kube.board.like.entity.ArticleLikeCount;
import kube.board.like.repository.ArticleLikeCountRepository;
import kube.board.like.repository.ArticleLikeRepository;
import kube.board.like.service.response.ArticleLikeResponse;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeCountRepository articleLikeCountRepositroy;

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow();
    }

    /**
     * update 구문
     */
    @Transactional
    public void likePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        int result = articleLikeCountRepositroy.increase(articleId);
        if (result == 0) {
            // 최초 요청 시에는 update 되는 레코드가 없으므로, 1로 초기화한다.
            // 트래픽이 순식간에 몰릴 수 있는 상황에는 유실 될 수 있으므로, 게시글 생성 시점에 미리 0으로 초기화 할 수 있다.
            articleLikeCountRepositroy.save(
                    ArticleLikeCount.init(articleId, 1L)
            );
        }
    }

    @Transactional
    public void unlikePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    articleLikeCountRepositroy.decrease(articleId);
                });
    }


    /**
     * select ... for update + update
     */
    @Transactional
    public void likePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
        ArticleLikeCount articleLikeCount = articleLikeCountRepositroy.findLockedByArticleId(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepositroy.save(articleLikeCount); // 명시적 save
    }

    @Transactional
    public void unlikePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike ->  {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepositroy.findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }

    /**
     * select ... for update + update
     */
    @Transactional
    public void likeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepositroy.findById(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepositroy.save(articleLikeCount); // 명시적인 save
    }

    @Transactional
    public void unlikeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepositroy.findById(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }

    public Long count(Long articleId) {
        return articleLikeCountRepositroy.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);
    }

}
