package sw2.lab6.teletok.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sw2.lab6.teletok.dto.LikePost;
import sw2.lab6.teletok.entity.Post;
import sw2.lab6.teletok.entity.PostLike;
import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.repository.PostLikeRepository;
import sw2.lab6.teletok.repository.PostRepository;
import sw2.lab6.teletok.repository.TokenRepository;

import java.util.HashMap;
import java.util.Optional;

@RestController
@CrossOrigin
public class LikeWebService {

    @Autowired
    PostLikeRepository postLikeRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    PostRepository postRepository;

    @PostMapping(value = "/ws/post/like", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity saveLike(
            @RequestBody LikePost likePost,
            @RequestParam(value = "fetchId", required = false) boolean fetchId) {

        HashMap<String, Object> responseMap = new HashMap<>();
        Optional<Token> token = tokenRepository.findTokenByCode(likePost.getToken());
        Optional<Post> post = postRepository.findById(likePost.getPostid());
        PostLike postLike = new PostLike();


        if (token.isPresent()) {
            postLike.setUser(token.get().getUser());
        }else{
            responseMap.put("error", "TOKEN_INVALID");
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }

        if (post.isPresent()) {

            postLike.setPost(post.get());
            postLikeRepository.save(postLike);

            if (fetchId) {
                responseMap.put("commentId", postLike.getId());
            }
            responseMap.put("status", "LIKE_CREATED");
            return new ResponseEntity(responseMap, HttpStatus.OK);
        } else {
            responseMap.put("error", "POST_NOT_FOUND");
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }



    }


}
