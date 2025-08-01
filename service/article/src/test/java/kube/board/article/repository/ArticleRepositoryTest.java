package kube.board.article.repository;

import kube.board.article.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Slf4j
@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;
    
    @Test
    @DisplayName("게시글 조회 테스트")
    void findAllTest() {
        List<Article> articles = articleRepository.findAll(1L, 129999L, 30L);
        log.info("articles = {}", articles.size());
        for(Article article : articles) {
            log.info("article = {}", article);
        }
    }
    
    @Test
    @DisplayName("게시글 count 조회 테스트")
    void findCountTest() {
        Long count = articleRepository.count(1L, 10000L);
        log.info("count = {}", count);

    }
    
    @Test
    @DisplayName("무한")
    void countTest() {

    }
    
    @Test
    @DisplayName("무한스크롤테스트 종합")        
    void findInfiniteScrollTest() {
        List<Article> articles = articleRepository.findAllInfiniteScroll(1L, 30L);
        for (Article article : articles) {
            log.info("articleId = {}", article.getArticleId());
        }

        Long lastArticleId = articles.getLast().getArticleId();
        List<Article> articles2 = articleRepository.findAllInfiniteScroll(1L, 30L, lastArticleId);
        for (Article article : articles2) {
            log.info("articleId = {}", article.getArticleId());
        }
    }

}