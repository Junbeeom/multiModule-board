package kube.board.comment.controller;


import kube.board.comment.service.CommentService;
import kube.board.comment.service.request.CommentCreateRequest;
import kube.board.comment.service.response.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SortComparator;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/v1/comments/{commentId}")
    public CommentResponse read(@PathVariable("commentId") Long commentId) {
        return commentService.read(commentId);
    }

    @PostMapping("/v1/comments")
    public CommentResponse create(@RequestBody CommentCreateRequest request) {
        return commentService.create(request);
    }

    @DeleteMapping("/v1/comments/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }
}
