package sw2.lab6.teletok.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sw2.lab6.teletok.entity.Post;
import sw2.lab6.teletok.repository.PostRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
public class PostWebService {

    @Autowired
    PostRepository postRepository;

    @ResponseBody
    @GetMapping(value = {"/ws/post/list"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity listarPost() {
        List<Post> listaP = postRepository.findAll();
        ResponseEntity responseEntity = new ResponseEntity(listaP, HttpStatus.OK);
        return responseEntity;
    }

    @ResponseBody
    @GetMapping(value = "/ws/post/{description}/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity obtenerPost(@PathVariable("description") String description,
                                      @PathVariable("username") String username) {
        HashMap<String, Object> hashMap = new HashMap<>();
        HttpStatus httpStatus;
        List<Post> listaP = postRepository.findAll();

        try {
            List<Post> lista = postRepository.obtenerPost(description, username);
            if (!lista.isEmpty()) {
                hashMap.put("estado", "ok");
                hashMap.put("estado", lista);
                httpStatus = HttpStatus.OK;
            } else {
                hashMap.put("estado", "ok");
                hashMap.put("msg", listaP);
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (NumberFormatException e) {
            hashMap.put("estado", "error");
            hashMap.put("msg", "El id debe ser numerico.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity(hashMap, httpStatus);
    }

    @ResponseBody
    @PostMapping(value = "/ws/post/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity guardarPost(@RequestBody Post post,
                                      @RequestParam("token") String code,
                                      @RequestParam("description") String description,
                                      @RequestParam("media") String mediaUrl) {
        HashMap<String, Object> hashMap = new HashMap<>();
        HttpStatus httpStatus;

        Optional<Post> opt = postRepository.obtenerPostporParametros(code, description, mediaUrl);
        if (!opt.isPresent()) {
            postRepository.save(post);
            hashMap.put("id", opt.get());
            hashMap.put("estado", "ok");
            hashMap.put("msg", "Post creado exitosamente.");
            httpStatus = HttpStatus.CREATED;
        } else {
            hashMap.put("estado", "error");
            hashMap.put("msg", "El post con id " + post.getId() + " no existe.");
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity(hashMap, httpStatus);
    }

}
