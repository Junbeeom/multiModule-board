package kube.board.like.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Table(name = "article_like_count")
@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleLikeCount {
    @Id
    private Long articleId; //shard Key
    private Long likeCount;

    @Version
    private Long version;

    public static ArticleLikeCount init(Long articleId, Long likeCount) {
        ArticleLikeCount articleLikeCount = new ArticleLikeCount();
        articleLikeCount.articleId = articleId;
        articleLikeCount.likeCount = likeCount;
        articleLikeCount.version = 0L;
        return articleLikeCount;
    }

    public void increase() {
        this.likeCount++;
    }

    public void decrease() {
        this.likeCount--;
    }
}
