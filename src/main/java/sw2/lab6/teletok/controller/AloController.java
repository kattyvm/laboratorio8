package sw2.lab6.teletok.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import sw2.lab6.teletok.entity.Post;
import sw2.lab6.teletok.entity.PostLike;
import sw2.lab6.teletok.entity.Token;
import sw2.lab6.teletok.entity.User;
import sw2.lab6.teletok.repository.PostRepository;
import sw2.lab6.teletok.repository.TokenRepository;
import sw2.lab6.teletok.repository.UserRepository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class AloController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    PostRepository postRepository;

    @PostMapping(value = "/ws/user/signIn", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity autentication(
            @RequestBody User user) {
        HashMap<String, String> responseMap = new HashMap<>();

        if (user.getUsername() == null || user.getPassword() == null) {
            responseMap.put("error", "FALTAN DATOS");
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }
        Optional<User> opt = userRepository.findByUsername(user.getUsername());
        if (opt.isPresent()) {
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean match = passwordEncoder.matches(user.getPassword(), opt.get().getPassword());
            if (match) {
                String tokenstr = "1213456";
                responseMap.put("status", "AUTHENTICATED");
                responseMap.put("token", tokenstr);
                Token token = new Token();
                token.setCode(tokenstr);
                token.setUser(opt.get());
                tokenRepository.save(token);
                return new ResponseEntity(responseMap, HttpStatus.OK);
            } else {
                responseMap.put("error", "AUTH_FAILED");
                return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
            }
        } else {
            responseMap.put("error", "AUTH_FAILED");
            return new ResponseEntity(responseMap, HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping(value = "/ws/post/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity obtenerPost(@PathVariable("id") String idStr,
                                      @RequestParam(value = "token", required = false) String token) {
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
        HttpStatus httpStatus;
       /* Boolean valid = false;
        Token tokentk=null;
        if (token != null) {
            Optional<Token> optToken = tokenRepository.findTokenByCode(token);
            if (!optToken.isPresent()) {
                hashMap.put("error", "TOKEN_INVALID");
                httpStatus = HttpStatus.BAD_REQUEST;
                tokentk.
            } else {
                valid = true;

            }
        }*/


        try {
            int id = Integer.parseInt(idStr);
            Optional<Post> opt = postRepository.findById(id);
            if (opt.isPresent()) {
                Post post = opt.get();
                hashMap.put("id", post.getId());
                hashMap.put("description", post.getDescription());
                hashMap.put("creationDate", post.getCreationDate());
                hashMap.put("mediaUrl", post.getMediaUrl());
                hashMap.put("username", post.getUser().getUsername());
                hashMap.put("commentCount", post.getComments().size());
                hashMap.put("likeCount", post.getLikes().size());
                /*if(valid) {
                    List<PostLike> listLike = post.getLikes();
                    Boolean found = false;
                    for (PostLike pl : listLike) {
                        if (pl.getUser() == optToken.get().getUser()) {
                            found = true;
                        }
                    }
                }
                hashMap.put("userlikePost", found);
                */
                hashMap.put("comments", post.getComments());

                httpStatus = HttpStatus.OK;
            } else {
                hashMap.put("error", "POST_NOT_FOUND");
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (NumberFormatException e) {
            hashMap.put("error", "ID_INVALID");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity(hashMap, httpStatus);
    }


}
