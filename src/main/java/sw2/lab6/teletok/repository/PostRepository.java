package sw2.lab6.teletok.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sw2.lab6.teletok.entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query(value = "SELECT p.id, p.description, p.creation_date, p.media_url, u.username, count(l.post_id) as \"likeCount\", count(c.post_id) as \"commentCount\"\n" +
            "FROM post p INNER JOIN post_comment c ON (c.post_id = p.id) \n" +
            "INNER JOIN post_like l ON (p.id = l.post_id)\n" +
            "INNER JOIN user u ON (l.user_id = u.id)\n" +
            "WHERE p.description = ? AND u.username = ?", nativeQuery = true)
    List<Post> obtenerPost(String description, String username);
}
