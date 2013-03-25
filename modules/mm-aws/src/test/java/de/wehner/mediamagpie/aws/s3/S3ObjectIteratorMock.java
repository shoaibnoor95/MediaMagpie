package de.wehner.mediamagpie.aws.s3;

import static org.mockito.Mockito.*;

import java.util.ArrayDeque;
import java.util.Deque;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3ObjectIteratorMock extends S3ObjectIterator {

    private final Deque<S3ObjectSummary> objectSummaries = new ArrayDeque<S3ObjectSummary>();

    public S3ObjectIteratorMock(String bucketName) {
        super(null, bucketName, null);
    }

    public void addS3ObjectSummary(String key) {
        S3ObjectSummary s3ObjectSummary = mock(S3ObjectSummary.class);
        when(s3ObjectSummary.getBucketName()).thenReturn(_bucketName);
        when(s3ObjectSummary.getKey()).thenReturn(key);
        objectSummaries.addLast(s3ObjectSummary);
    }

    @Override
    public boolean hasNext() {
        return !objectSummaries.isEmpty();
    }

    @Override
    public S3ObjectSummary next() {
        return objectSummaries.removeFirst();
    }

}
