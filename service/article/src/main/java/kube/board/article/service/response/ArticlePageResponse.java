package kube.board.article.service.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticlePageResponse {
    private List<ArticleResponse> articles;
    private Long articleCount;

    public static ArticlePageResponse of(List<ArticleResponse> article, Long articleCount) {
        ArticlePageResponse response = new ArticlePageResponse();
        response.articles = article;
        response.articleCount = articleCount;
        return response;
    }

}
