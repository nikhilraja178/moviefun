package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

public class S3Store implements BlobStore {

    private PutObjectRequest putObjectRequest;
    AmazonS3Client s3Client;
    String photoStorageBucket;

    public S3Store(AmazonS3Client s3Client, String photoStorageBucket) {
        this.s3Client = s3Client;
        this.photoStorageBucket = photoStorageBucket;

    }

    @Override
    public void put(Blob blob) throws IOException {
        if(s3Client.doesBucketExist(photoStorageBucket)){
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(blob.contentType);
            s3Client.putObject(photoStorageBucket.toString(), blob.name, blob.inputStream, metadata);
        }

    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if(s3Client.doesBucketExist(photoStorageBucket)){

            S3Object input = s3Client.getObject(photoStorageBucket.toString(), name.toString());
            Blob blob = new Blob(name, input.getObjectContent(), input.getObjectMetadata().getContentType());
            return Optional.of(blob);
        }
        return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
