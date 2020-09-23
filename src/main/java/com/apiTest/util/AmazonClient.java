package com.apiTest.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class AmazonClient {

    private AmazonS3 s3client;

    @Value("${s3.endpoint-url}")
    private String endpointUrl;

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secrettKey;

    @Value("${s3.bucketName}")
    private String bucketName;


    @PostConstruct
    private void initializeAmazon(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secrettKey);
        this.s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion("eu-west-2")
                .build();
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
        fileOutputStream.write(file.getBytes());
        fileOutputStream.close();
        return convertedFile;
    }

    private String generateFileName(MultipartFile multipartFile){
        return new Date().getTime() + "_" + multipartFile.getOriginalFilename();
    }

    public String getFileName(String url){
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        return fileName;
    }

    private void uploadFileToS3Bucket(String fileName, File file){
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String uploadFile(MultipartFile multipartFile){
        String url = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            url = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileToS3Bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private S3Object getFileFromS3(String url){
        String fileName = getFileName(url);
        S3Object s3Object = s3client.getObject(new GetObjectRequest(bucketName, fileName));
        return s3Object;
    }

    // can be used if file download is implemented
    public S3ObjectInputStream getFile(String url) {
        S3Object s3Object = getFileFromS3(url);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        return inputStream;
    }

    public String deleteFileFromS3(String url){
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Deleted image";
    }

}
