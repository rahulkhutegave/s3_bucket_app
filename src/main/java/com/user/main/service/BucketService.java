package com.user.main.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Service
public class BucketService {

	private AmazonS3 s3client;

	@Value("${amazon.s3.endpoint}")
	private String endpointUrl;

	@Value("${amazon.s3.bucket-name}")
	private String bucketName;

	@Value("${amazon.s3.accesskey}")
	private String accessKey;

	@Value("${amazon.s3.secretkey}")
	private String secretKey;

	@PostConstruct
	private void initializeAmazonClient() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
//		BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		this.s3client = new AmazonS3Client(credentials);
//		this.s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
//				.build();
	}

	public String uploadFile(MultipartFile multipartFile) {
		String fileUrl = "";
		try {
			File file = convertMultiPartToFile(multipartFile);
			String fileName = generateFileName(multipartFile);
			fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
			uploadFileTos3bucket(fileName, file);
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileUrl;
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private String generateFileName(MultipartFile multiPart) {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public String downloadFile(String fileName) {
		S3Object object = this.s3client.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = object.getObjectContent();
		try {
			String stringFromTextFile = new BufferedReader(new InputStreamReader(inputStream)).lines()
					.collect(Collectors.joining("\n"));
			System.err.println(">>>>>data from File>>>>>> " + "\n" + stringFromTextFile);
			FileUtils.copyInputStreamToFile(inputStream, new File("C:\\users\\demo.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "download success!!";
	}

}
