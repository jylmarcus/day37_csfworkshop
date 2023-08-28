package csf.visa.day37_csfworkshop.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import csf.visa.day37_csfworkshop.model.Post;
import csf.visa.day37_csfworkshop.repository.PostRepository;
import csf.visa.day37_csfworkshop.repository.s3Repository;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping("/api")
@CrossOrigin (origins = "*")
public class PostController {

    @Autowired
    PostRepository postRepo;

    @Autowired
    s3Repository s3Repo;
    

    /* @GetMapping(path = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPost(@PathVariable String id) {
        Optional<Post> opt = postRepo.selectPostById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Post not found");
        }
        String base64ImgString = Base64.getEncoder().encodeToString(opt.get().image());
        String comments = opt.get().comments();
        JsonObject resp = Json.createObjectBuilder()
            .add("id", id)
            .add("image", base64ImgString)
            .add("comments", comments)
            .build();
        
        return ResponseEntity.ok(resp.toString());
    } */

    @GetMapping(path = "/image/{id}")
    public ResponseEntity<String> getPostImageById(@PathVariable String id) throws IOException {
        JsonObject resp = Json.createObjectBuilder()
            .add("image", s3Repo.getImage(id))
            .build();
        return ResponseEntity.ok(resp.toString());
    }

    @GetMapping(path = "/comments/{id}")
    public ResponseEntity<String> getPostCommentsById(@PathVariable String id) throws IOException {
        JsonObject resp = Json.createObjectBuilder()
            .add("comments", s3Repo.getComments(id))
            .build();
        return ResponseEntity.ok(resp.toString());
    }
    //alternative method: use a postDTO with a string comment field and a byte array field
    
    /* @PostMapping(path = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPost(@RequestPart MultipartFile image, @RequestPart String comments) {
        //pass values to repository
        try {
            InputStream is = image.getInputStream();
            String id = UUID.randomUUID().toString().substring(0, 7);
            System.out.println(">>> Post ID: " + id);
            postRepo.uploadPost(id, comments, is);

            JsonObject resp = Json.createObjectBuilder()
				.add("id", id)
				.build();
			return ResponseEntity.ok(resp.toString());
        } catch (IOException ex){
            JsonObject resp = Json.createObjectBuilder()
				.add("error", ex.getMessage())
				.build();
			return ResponseEntity.status(500)
				.body(resp.toString());
        }
    } */

    @PostMapping(path = "/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPost(@RequestPart MultipartFile image, @RequestPart String comments) {
        //pass values to repository
        try {
            String mediaType = image.getContentType();
            InputStream is = image.getInputStream();
            String id = s3Repo.saveImage(image, comments);
            System.out.println(">>> Post ID: " + id);
            postRepo.uploadPost(id, comments, is);

            JsonObject resp = Json.createObjectBuilder()
				.add("id", id)
				.build();
			return ResponseEntity.ok(resp.toString());
        } catch (IOException ex){
            JsonObject resp = Json.createObjectBuilder()
				.add("error", ex.getMessage())
				.build();
			return ResponseEntity.status(500)
				.body(resp.toString());
        }
    }
}
