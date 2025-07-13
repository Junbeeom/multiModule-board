package kube.board.view.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ViewApiTest {
    RestClient restClient = RestClient.create("http://localhost:9003");

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("1만번 호출 테스트")
    void viewTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                restClient.post()
                        .uri("/v1/article-views/articles/{articleId}/users/{userId}", 3L, 1L)
                        .retrieve();
                        latch.countDown();

            });
        }

        latch.await();

        Long count = restClient.get()
                .uri("/v1/article-views/articles/{articleId}/count", 3L)
                .retrieve()
                .body(Long.class);

        System.out.println("count" + count);
    }

    @Test
    @DisplayName("레디스 뷰 카운트 테스트")
    void testRedisViewCount() {
        Long articleId = 100L;
        Long userId = 200L;

        // 뷰 증가
        restClient.post()
                .uri("/v1/article-views/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve();

        // 뷰 카운트 조회
        Long count = restClient.get()
                .uri("/v1/article-views/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long.class);

        // 결과 검증
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("레디스 연결 테스트")
    void testRedisConnection() {
        // Redis 연결 확인을 위해 ping 명령 실행
        String pong = stringRedisTemplate.getConnectionFactory().getConnection().ping();
        assertNotNull(pong);
        System.out.println("pong = " + pong);
        assertEquals("PONG", pong);
    }
}
