package com.vhskillpro.backend.utils.r2;

import java.io.InputStream;
import java.time.Duration;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class R2Service {
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  public R2Service(S3Client s3Client, S3Presigner s3Presigner) {
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
  }

  /**
   * Generates a presigned URL for accessing an object in the specified S3 bucket. The URL allows
   * temporary access to the object for the given expiration time.
   *
   * @param bucketName the name of the S3 bucket containing the object
   * @param objectKey the key (path) of the object within the bucket
   * @param expirationInSeconds the duration in seconds for which the presigned URL is valid
   * @return a presigned URL as a String that grants temporary access to the object
   */
  public String generatePresignedUrl(
      String bucketName, String objectKey, long expirationInSeconds) {
    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(expirationInSeconds))
            .getObjectRequest(getObjectRequest)
            .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }

  /**
   * Uploads a file to the specified S3 bucket.
   *
   * @param bucketName the name of the S3 bucket to upload the file to
   * @param objectKey the key (path/filename) under which to store the file in the bucket
   * @param inputStream the input stream containing the file data to upload
   * @param contentLength the length of the content in bytes
   * @param contentType the MIME type of the file being uploaded
   */
  public void uploadFile(
      String bucketName,
      String objectKey,
      InputStream inputStream,
      long contentLength,
      String contentType) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .contentType(contentType)
            .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
  }

  /**
   * Deletes a file from the specified S3 bucket.
   *
   * @param bucketName the name of the S3 bucket from which the file will be deleted
   * @param objectKey the key (path) of the file to delete within the bucket
   */
  public void deleteFile(String bucketName, String objectKey) {
    s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(objectKey).build());
  }
}
