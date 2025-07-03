package kuke.board.article.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kube.board.article.ArticleApplication;
import kube.board.article.entity.Article;
import kuke.board.common.snowflake.Snowflake;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = ArticleApplication.class)
public class DataInitializer {
    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate; // JDBC batch insert용.



    Snowflake snowflake = new Snowflake();
    CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    static final int BULK_INSERT_SIZE = 1000;
    static final int EXECUTE_COUNT = 1000;
    @Autowired
    private InternalResourceViewResolver internalResourceViewResolver;


    @Test
    void initialize() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i = 0; i < EXECUTE_COUNT; i++) {
            final int batchIndex = i;
            executorService.submit(() -> {
                    insert();

                // 새로운 JDBC batch 방식
                // batchInsert(batchIndex);

                latch.countDown();
                System.out.println("latch.getCount() = " + latch.getCount());
            });
        }
        latch.await();
        executorService.shutdown();
    }

    void insert() {
        transactionTemplate.executeWithoutResult(status -> {
            for(int i = 0; i < BULK_INSERT_SIZE; i++) {
                Article article = Article.create(
                        snowflake.nextId(),
                        "title" + i,
                        "content" + i,
                        1L,
                        1L
                );
                entityManager.persist(article);
            }
        });
    }
    
    @Test
    @DisplayName("성능 향상 방법")        
    void batchInsert(int batchIndex) {
        List<Article> articles = new ArrayList<>(BULK_INSERT_SIZE);
        int start = batchIndex * BULK_INSERT_SIZE;
        for (int i = 0; i < BULK_INSERT_SIZE; i++) {
            int idx = start + i;
            articles.add(
                    Article.create(
                            snowflake.nextId(),
                            "title" + idx,
                            "content" + idx,
                            1L,
                            1L
                    )
            );
            System.out.println("idx = " + idx);

        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO article (article_id, title, content, user_id, board_id) VALUES (?, ?, ?, ?, ?)",
                articles,
                BULK_INSERT_SIZE,
                (ps, article) -> {
                    ps.setLong(1, article.getArticleId());
                    ps.setString(2, article.getTitle());
                    ps.setString(3, article.getContent());
                    ps.setLong(4, article.getBoardId());
                    ps.setLong(5, article.getWriterId());
                }
        );
        
    }
}
