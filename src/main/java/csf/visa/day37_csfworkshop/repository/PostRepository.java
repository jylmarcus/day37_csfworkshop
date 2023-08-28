package csf.visa.day37_csfworkshop.repository;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import csf.visa.day37_csfworkshop.model.Post;

@Repository
public class PostRepository {

    public static final String SQL_SELECT_POST_BY_ID = "select * from posts where post_id = ?";

    public static final String SQL_INSERT_INTO_POSTS = "insert into posts(post_id, comments, picture) values (?, ?, ?)";

    @Autowired JdbcTemplate template;

    public Optional<Post> selectPostById(String id) {
        List<Post> result = template.query(SQL_SELECT_POST_BY_ID, (ResultSet rs) -> {
            List<Post> results = new LinkedList<>();
            while(rs.next()) {
                Post post = new Post(id, rs.getString("comments"), rs.getBytes("picture"));
                results.add(post);
            }
            return results;
        }, id);

        if(result.isEmpty()) return Optional.empty();

        return Optional.of(result.get(0));
    }

    public void uploadPost(String id, String comments, InputStream is){

        template.update(SQL_INSERT_INTO_POSTS, id, comments, is);
    }
    
}
