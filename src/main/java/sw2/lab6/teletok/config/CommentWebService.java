package sw2.lab6.teletok.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import sw2.lab6.teletok.dto.CommentPost;
import sw2.lab6.teletok.entity.Post;
import sw2.lab6.teletok.entity.PostComment;
import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.repository.PostCommentRepository;
import sw2.lab6.teletok.repository.PostRepository;
import sw2.lab6.teletok.repository.TokenRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class CommentWebService {

    @Autowired
    PostCommentRepository postCommentRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    PostRepository postRepository;


    @PostMapping(value = "/ws/post/comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveComment(
            @RequestBody CommentPost commentPost,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseMap = new HashMap<>();
        Token token = tokenRepository.findByCode(commentPost.getToken());
        Optional<Post> post = postRepository.findById(commentPost.getPostid());
        PostComment postComment = new PostComment();


        if (token == null) {
            responseMap.put("error", "TOKEN_INVALID");
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }

        if (post.isPresent()) {
            postComment.setUser(token.getUser());
            postComment.setMessage(commentPost.getMessage());
            postComment.setPost(post.get());
            postCommentRepository.save(postComment);
            if (fetchId) {
                responseMap.put("commentId", postComment.getId());
            }
            responseMap.put("status", "COMMENT_CREATED");
            return new ResponseEntity(responseMap, HttpStatus.OK);
        } else {
            responseMap.put("error", "POST_NOT_FOUND");
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }

    }


}
