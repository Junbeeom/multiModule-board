package kube.board.article.controller;

import kube.board.article.service.ArticleService;
import kube.board.article.service.request.ArticleCreateRequest;
import kube.board.article.service.request.ArticleUpdateRequest;
import kube.board.article.service.response.ArticlePageResponse;
import kube.board.article.service.response.ArticleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping("/v1/articles/{articleId}")
    public ArticleResponse read(@PathVariable Long articleId) {
        return articleService.read(articleId);
    }

    @GetMapping("/v1/articles")
    public ArticlePageResponse readAll(@RequestParam Long boardId, @RequestParam Long page, @RequestParam Long pageSize) {
        return articleService.readAll(boardId, page, pageSize);
    }

    @PostMapping("/v1/articles")
    public ArticleResponse create(@RequestBody ArticleCreateRequest request) {
        return articleService.create(request);
    }

    @GetMapping("/v1/articles/infinite-scroll")
    public List<ArticleResponse> readAllInfiniteScroll(@RequestParam Long boardId, @RequestParam Long pageSize, @RequestParam(required = false) Long lastArticleId) {
        return articleService.readAllInfiniteScroll(boardId, pageSize, lastArticleId);
    }

    @PutMapping("/v1/articles/{articleId}")
    public ArticleResponse udpate(@PathVariable Long articleId, @RequestBody ArticleUpdateRequest request) {
        return articleService.update(articleId, request);
    }

    @DeleteMapping("/v1/articles/{articleId}")
    public void delete(@PathVariable Long articleId) {
        articleService.delete(articleId);
    }
}
