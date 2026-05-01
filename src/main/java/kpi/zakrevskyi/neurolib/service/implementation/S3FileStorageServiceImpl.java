package kpi.zakrevskyi.neurolib.service.implementation;

import java.io.IOException;
import java.io.InputStream;
import kpi.zakrevskyi.neurolib.domain.FileType;
import kpi.zakrevskyi.neurolib.service.FileStorageService;
import kpi.zakrevskyi.neurolib.service.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;

    @Value("${app.aws.s3.path}")
    private String path;

    @Value("${app.aws.s3.bucket-name}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file, FileType fileType, String ownerId) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String key = buildObjectKey(fileType, ownerId, file.getOriginalFilename());

        try {
            deleteObjectsByOwner(fileType, ownerId);

            PutObjectRequest.Builder putBuilder = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentLength(file.getSize());

            if (StringUtils.hasText(file.getContentType())) {
                putBuilder.contentType(file.getContentType());
            }

            try (InputStream inputStream = file.getInputStream()) {
                s3Client.putObject(putBuilder.build(), RequestBody.fromInputStream(inputStream, file.getSize()));
            }
            return constructFileUrl(key);
        } catch (IOException | SdkException ex) {
            log.error("Failed to store file in S3: {}", ex.getMessage());
            throw new BadRequestException("Failed to store file");
        }
    }

    @Override
    public void deleteAllByOwner(FileType fileType, String ownerId) {
        try {
            deleteObjectsByOwner(fileType, ownerId);
        } catch (SdkException ex) {
            log.error("Failed to delete files by owner from S3: {}", ex.getMessage());
            throw new BadRequestException("Failed to delete file");
        }
    }

    private String buildObjectKey(FileType fileType, String ownerId, String originalFilename) {
        String extension = extractExtension(originalFilename);
        String base = fileType.getFolderName() + "/" + ownerId;
        return StringUtils.hasText(extension) ? base + "." + extension : base;
    }

    private void deleteObjectsByOwner(FileType fileType, String ownerId) {
        String base = fileType.getFolderName() + "/" + ownerId;
        String continuationToken = null;

        do {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(base)
                .continuationToken(continuationToken)
                .build();
            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            for (S3Object object : response.contents()) {
                s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(object.key())
                        .build()
                );
            }

            continuationToken = response.nextContinuationToken();
        } while (continuationToken != null);
    }

    private String constructFileUrl(String key) {
        return constructBaseUrl() + bucket + "/" + key;
    }

    private String constructBaseUrl() {
        if (StringUtils.hasText(path)) {
            return path.endsWith("/") ? path : path + "/";
        }
        return s3Client.utilities()
            .getUrl(GetUrlRequest.builder().bucket(bucket).key("").build())
            .toString();
    }

    private String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return "";
        }
        String fileName = originalFilename.replace("\\", "_").replace("/", "_").trim();
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase();
    }
}
