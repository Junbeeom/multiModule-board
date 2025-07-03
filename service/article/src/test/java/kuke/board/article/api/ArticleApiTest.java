package kuke.board.article.api;

import kube.board.article.entity.Article;
import kube.board.article.service.request.ArticleCreateRequest;
import kube.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest("hi", "my content", 1L, 1L));
        System.out.println("response = " + response);

    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }
    
    @Test
    void readTest() {
        ArticleResponse response = read(198999643385364480L);
        System.out.println("response = " + response);
    }


    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }
    
    @Test
    void updateTest() {
        update(198686044961542144L);
        ArticleResponse response = read(198686044961542144L);
        System.out.println("response" + response);
    }

    void update(Long articleId) {
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 2", "my content 22"))
                .retrieve();
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v1/articles/{articleId}", 198686044961542144L)
                .retrieve();
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long boardId;
        private Long writerId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }

}
