package de.wehner.mediamagpie.aws.s3;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3ObjectIterator implements Iterator<S3ObjectSummary> {

    private static final Logger LOG = LoggerFactory.getLogger(S3ObjectIterator.class);

    private static final int MAX_OBJECTS_PER_READ = 50;
    private final AmazonS3 _s3;
    private final String _bucketName;
    private final String _prefix;
    private ObjectListing _lastObjectListing;
    private int _objectSummaryIndex = 0;
    /** The object to lock on */
    protected final Object lock;

    public S3ObjectIterator(AmazonS3 s3, String bucketName, String prefix) {
        super();
        _s3 = s3;
        _bucketName = bucketName;
        _prefix = prefix;
        lock = this;
    }

    @Override
    public boolean hasNext() {
        synchronized (lock) {
            readNextIfNecessary();
            return (_objectSummaryIndex < _lastObjectListing.getObjectSummaries().size());
        }
    }

    @Override
    public S3ObjectSummary next() {
        synchronized (lock) {
            readNextIfNecessary();
            S3ObjectSummary s3ObjectSummary = _lastObjectListing.getObjectSummaries().get(_objectSummaryIndex++);
            return s3ObjectSummary;
        }
    }

    private void readNextIfNecessary() {
        if (_lastObjectListing == null || _objectSummaryIndex >= _lastObjectListing.getObjectSummaries().size()) {
            readNextObjectSummaries();
        }
    }

    private void readNextObjectSummaries() {
        if (_lastObjectListing == null) {
            // retrieve initial ObjectListing from S3
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(_bucketName).withPrefix(_prefix)
            /* .withDelimiter(S3MediaRepository.KEY_DELIMITER) */.withMaxKeys(MAX_OBJECTS_PER_READ);
            _lastObjectListing = _s3.listObjects(listObjectsRequest);
            filterZeroLengthObjects();
            LOG.info("list objects in '" + _bucketName + "'#'" + _prefix + "' and got " + _lastObjectListing.getObjectSummaries().size() + " items.");
            return;
        }

        // does we need to load next bunch of object summaries?
        if (_lastObjectListing.isTruncated()) {
            // list next ObjectListing
            _lastObjectListing = _s3.listNextBatchOfObjects(_lastObjectListing);
            LOG.info("list next buch of objects in '" + _bucketName + "', '" + _prefix + "'.");
            _objectSummaryIndex = 0;
        }
    }

    private void filterZeroLengthObjects() {
        for (int i = _lastObjectListing.getObjectSummaries().size() - 1; i >= 0; i--) {
            if (_lastObjectListing.getObjectSummaries().get(i).getSize() == 0) {
                LOG.info("remove zero length object '" + _lastObjectListing.getObjectSummaries().get(i).getKey() + "' from ObjectListing.");
                _lastObjectListing.getObjectSummaries().remove(i);
            }
        }
    }

    @Override
    public void remove() {
        throw new RuntimeException("Not supported operation");
    }

}
