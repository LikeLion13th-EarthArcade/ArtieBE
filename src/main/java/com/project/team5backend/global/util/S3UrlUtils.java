package com.project.team5backend.global.util;

import org.springframework.beans.factory.annotation.Value;


public class S3UrlUtils {

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    private final String baseUrl;

    public S3UrlUtils(
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.region}") String region) {
        this.bucket = bucketName;
        this.region = region;
        this.baseUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }

    public String toImageUrl(String fileKey) {
        return baseUrl + fileKey;
    }

    public String toFileKey(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf(".com/") + 5);
    }
}

