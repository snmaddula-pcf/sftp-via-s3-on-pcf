package snmaddula.pcfgyan;

import static com.amazonaws.regions.Regions.fromName;
import static com.amazonaws.services.s3.AmazonS3ClientBuilder.standard;
import static org.apache.commons.io.IOUtils.toByteArray;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.Setter;
/**
 * @author snmaddula
 */
@Setter
@RestController
@ConfigurationProperties("vcap.services.sftp-poc-s3.credentials")
public @SpringBootApplication class App {

	private String region, bucket, accessKeyId, secretAccessKey;

	@PutMapping("/put")
	public void push(MultipartFile file) throws Exception {
		s3client().putObject(new PutObjectRequest(bucket, file.getOriginalFilename(), file.getInputStream(), new ObjectMetadata()));
	}

	@GetMapping("/get")
	public void pull(String fileName, HttpServletResponse res) throws Exception {
		res.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		res.getOutputStream().write(toByteArray(s3client().getObject(new GetObjectRequest(bucket, fileName)).getObjectContent()));
	}
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	@Bean
	public AmazonS3 s3client() {
		return standard().withRegion(fromName(region))
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey))).build();
	}

}
