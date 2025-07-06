package kube.board.comment.api;

import kube.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");
    
    @Test
    @DisplayName("생성 테스트")
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));

        System.out.println("commentId=%s".formatted(response1.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));
    }
    
    @Test
    @DisplayName("조회하기")
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 200164605651537920L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    /*  commentId=200164605651537920
        commentId=200164606314237952
        commentId=200164606574284800*/
    
    @Test
    @DisplayName("삭제 테스트")
    void delete() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 200164606574284800L)
                .retrieve();
        
    }
    


    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }




    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }


}
