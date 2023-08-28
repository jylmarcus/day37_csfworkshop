package csf.visa.day37_csfworkshop.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Repository
public class s3Repository {

    @Autowired
    private AmazonS3 s3;

    /* public ResponseEntity<byte[]> getImage(String id) throws IOException {
        try {
            GetObjectRequest getReq = new GetObjectRequest("nusiss", id);
            S3Object result = s3.getObject(getReq);
            ObjectMetadata metadata = result.getObjectMetadata();
            Map<String, String> userData = metadata.getUserMetadata();
            try (S3ObjectInputStream is = result.getObjectContent()) {
                byte[] buffer = is.readAllBytes();
                return ResponseEntity.status(HttpStatus.OK)
                    .contentLength(metadata.getContentLength())
                    .contentType(MediaType.parseMediaType(metadata.getContentType()))
                    .header("X-name", userData.get("name"))
                    .body(buffer);
            }
        } catch (AmazonS3Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new byte[] {});
        }
    } */

    public String getImage(String id) {
        String key = "post/%s".formatted(id);
        return s3.getUrl("jylmarcus", key).toExternalForm();
    }

    public String getComments(String id) throws IOException {
        try {
            GetObjectRequest getReq = new GetObjectRequest("jylmarcus", "post/%s".formatted(id));
            S3Object result = s3.getObject(getReq);
            ObjectMetadata metadata = result.getObjectMetadata();
            String comments = metadata.getUserMetaDataOf("comments");
            return comments;
        } catch (AmazonS3Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }

    public String saveImage(MultipartFile uploadFile, String comments) {
        Map<String, String> userData = new HashMap<>();
        userData.put("comments", comments);
        userData.put("filename", uploadFile.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(uploadFile.getContentType());
        metadata.setContentLength(uploadFile.getSize());
        metadata.setUserMetadata(userData);

        String id = UUID.randomUUID().toString().substring(0, 8);

        try {
            PutObjectRequest putReq = new PutObjectRequest("jylmarcus", "post/%s".formatted(id), uploadFile.getInputStream(), metadata);
            putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult result = s3.putObject(putReq);
            System.out.printf("result: %s\n", result);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return id;
    }
    
}
