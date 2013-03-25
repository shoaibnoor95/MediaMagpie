package de.wehner.mediamagpie.aws.s3;

import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3ObjectTuple {

    private final S3ObjectSummary _dataObject;
    private final S3ObjectSummary _metaObject;

    public S3ObjectTuple(S3ObjectSummary dataObject, S3ObjectSummary metaObject) {
        super();
        _dataObject = dataObject;
        _metaObject = metaObject;
    }

    public S3ObjectSummary getDataObject() {
        return _dataObject;
    }

    public S3ObjectSummary getMetaObject() {
        return _metaObject;
    }
}