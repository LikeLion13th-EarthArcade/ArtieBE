package com.project.team5backend.global.infra.s3;

import com.project.team5backend.domain.common.storage.FileStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileStorageAdapter implements FileStoragePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String upload(MultipartFile file, String dirName) {
        String fileKey = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
        return fileKey;
    }

    @Override
    public void delete(String fileKey) {
        DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        s3Client.deleteObject(deleteReq);
    }

    @Override
    public void moveToTrash(String fileKey) {
        if (fileKey == null || fileKey.isEmpty()) {
            log.info("이동할 파일이 없습니다.");
            return;
        }
        try {
            String dst = "trash/" + fileKey;
            copy(fileKey, dst);
            delete(fileKey);
            log.info("파일 휴지통 이동 완료: {} -> {}", fileKey, dst);
        } catch (Exception e) {
            log.error("파일 휴지통 이동 실패: {}", fileKey, e);
        }
    }

    private void copy(String src, String dst) {
        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(src)
                .destinationBucket(bucketName)
                .destinationKey(dst)
                .build();
        s3Client.copyObject(copyReq);
    }
}
