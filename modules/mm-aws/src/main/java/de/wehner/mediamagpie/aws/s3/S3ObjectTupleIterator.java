package de.wehner.mediamagpie.aws.s3;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.S3ObjectSummary;

/**
 * This is a layer on top of <code>S3ObjectIterator</code> to read a pair of <code>S3ObjectTuple</code>s, one for the data stream and one
 * containing the meta information.
 * 
 * @author ralfwehner
 * 
 */
public class S3ObjectTupleIterator implements Iterator<S3ObjectTuple> {

    private static final Logger LOG = LoggerFactory.getLogger(S3ObjectTupleIterator.class);

    private final S3ObjectIterator _s3ObjectIterator;

    private S3ObjectSummary _pendingDataObject1;
    private S3ObjectSummary _pendingDataObject2;

    private S3ObjectSummary _pendingMetaDataObject;

    /** The object to lock on */
    protected final Object lock;

    public S3ObjectTupleIterator(S3ObjectIterator s3ObjectIterator) {
        super();
        _s3ObjectIterator = s3ObjectIterator;
        lock = this;
    }

    @Override
    public boolean hasNext() {
        synchronized (lock) {
            readNextIfNecessary();
            return (_pendingDataObject1 != null);
        }
    }

    private void readNextIfNecessary() {
        if (_pendingDataObject1 == null || (_pendingMetaDataObject == null && _pendingDataObject2 == null)) {
            if (_s3ObjectIterator.hasNext()) {
                S3ObjectSummary s3ObjectSummary = _s3ObjectIterator.next();

                // TODO rwe: the bucketName is not relevant. Remove this..
                //String bucketName = s3ObjectSummary.getBucketName();
                String key = s3ObjectSummary.getKey();
                //LOG.debug("Got S3ObjectSummary with name/key '" + bucketName + "/" + key + "'");

                if (key.endsWith(S3MediaExportRepository.METADATA_FILE_EXTENSION)) {
                    // metadata was found...
                    if (_pendingMetaDataObject != null) {
                        LOG.warn("internal error: a metadata object was read before an now a data object is expected!");
                    } else {
                        _pendingMetaDataObject = s3ObjectSummary;
                    }
                } else {
                    // a dataobject was found
                    if (_pendingDataObject1 == null) {
                        _pendingDataObject1 = s3ObjectSummary;
                    } else {
                        _pendingDataObject2 = s3ObjectSummary;
                    }
                    if (_pendingMetaDataObject == null) {
                        readNextIfNecessary();
                    }
                }
            }
        }
    }

    @Override
    public S3ObjectTuple next() {
        synchronized (lock) {
            readNextIfNecessary();
            S3ObjectTuple s3ObjectTuple = new S3ObjectTuple(_pendingDataObject1, _pendingMetaDataObject);
            _pendingDataObject1 = _pendingDataObject2;
            _pendingDataObject2 = null;
            _pendingMetaDataObject = null;
            return s3ObjectTuple;
        }
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub

    }
}
