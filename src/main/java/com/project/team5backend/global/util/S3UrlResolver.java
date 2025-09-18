package com.project.team5backend.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class S3UrlResolver {

    private final String baseUrl;

    public S3UrlResolver(
            @Value("${aws.s3.bucket-name}") String bucketName,
            @Value("${aws.s3.region}") String region) {
        this.baseUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);
    }

    public String toImageUrl(String fileKeyOrUrl) {
        if (fileKeyOrUrl.startsWith("http://") || fileKeyOrUrl.startsWith("https://")) {
            return fileKeyOrUrl;
        }
        return baseUrl + fileKeyOrUrl;
    }

    public String toFileKey(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf(".com/") + 1);
    }
}


