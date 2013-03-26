package de.wehner.mediamagpie.aws.s3;

import static org.fest.assertions.Assertions.*;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.aws.s3.in.S3ObjectIteratorMock;
import de.wehner.mediamagpie.aws.s3.in.S3ObjectTupleIterator;

public class S3ObjectTupleIteratorTest {

    private S3ObjectTupleIterator _s3ObjectTupleIterator;

    private S3ObjectIteratorMock _s3ObjectIterator;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        MockitoAnnotations.initMocks(this);
        _s3ObjectIterator = new S3ObjectIteratorMock("myBucket");
        _s3ObjectTupleIterator = new S3ObjectTupleIterator(_s3ObjectIterator);
    }

    @Test
    public void test_next_oneData() {
        _s3ObjectIterator.addS3ObjectSummary("key");

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();
        S3ObjectTuple result = _s3ObjectTupleIterator.next();

        assertThat(result.getDataObject()).isNotNull();
        assertThat(result.getMetaObject()).isNull();

        // no more data any more
        assertThat(_s3ObjectTupleIterator.hasNext()).isFalse();
    }

    @Test
    public void test_next_oneData_oneMetaData() {
        String key = "key";
        _s3ObjectIterator.addS3ObjectSummary(key);
        _s3ObjectIterator.addS3ObjectSummary(key + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();
        S3ObjectTuple result = _s3ObjectTupleIterator.next();

        assertThat(result.getDataObject().getKey()).isEqualTo(key);
        assertThat(result.getMetaObject().getKey()).isEqualTo(key + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        // no more data any more
        assertThat(_s3ObjectTupleIterator.hasNext()).isFalse();
    }

    @Test
    public void test_next_oneData_oneMetaData_oneData() {
        String key1 = "key1";
        String key2 = "key2";
        _s3ObjectIterator.addS3ObjectSummary(key1);
        _s3ObjectIterator.addS3ObjectSummary(key1 + S3MediaExportRepository.METADATA_FILE_EXTENSION);
        _s3ObjectIterator.addS3ObjectSummary(key2);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();
        S3ObjectTuple result1 = _s3ObjectTupleIterator.next();

        assertThat(result1.getDataObject().getKey()).isEqualTo(key1);
        assertThat(result1.getMetaObject().getKey()).isEqualTo(key1 + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();

        S3ObjectTuple result2 = _s3ObjectTupleIterator.next();

        assertThat(result2.getDataObject().getKey()).isEqualTo(key2);
        assertThat(result2.getMetaObject()).isNull();

        // no more data any more
        assertThat(_s3ObjectTupleIterator.hasNext()).isFalse();
    }

    @Test
    public void test_next_oneData_oneMetaData_oneData_oneMetaData() {
        String key1 = "key1";
        String key2 = "key2";
        _s3ObjectIterator.addS3ObjectSummary(key1);
        _s3ObjectIterator.addS3ObjectSummary(key1 + S3MediaExportRepository.METADATA_FILE_EXTENSION);
        _s3ObjectIterator.addS3ObjectSummary(key2);
        _s3ObjectIterator.addS3ObjectSummary(key2 + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();
        S3ObjectTuple result1 = _s3ObjectTupleIterator.next();

        assertThat(result1.getDataObject().getKey()).isEqualTo(key1);
        assertThat(result1.getMetaObject().getKey()).isEqualTo(key1 + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();

        S3ObjectTuple result2 = _s3ObjectTupleIterator.next();

        assertThat(result2.getDataObject().getKey()).isEqualTo(key2);
        assertThat(result2.getMetaObject().getKey()).isEqualTo(key2 + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        // no more data any more
        assertThat(_s3ObjectTupleIterator.hasNext()).isFalse();
    }

    @Test
    public void test_next_oneData_oneData() {
        String key1 = "key1";
        String key2 = "key2";
        _s3ObjectIterator.addS3ObjectSummary(key1);
        _s3ObjectIterator.addS3ObjectSummary(key2);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();
        S3ObjectTuple result1 = _s3ObjectTupleIterator.next();

        assertThat(result1.getDataObject().getKey()).isEqualTo(key1);
        assertThat(result1.getMetaObject()).isNull();

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();

        S3ObjectTuple result2 = _s3ObjectTupleIterator.next();

        assertThat(result2.getDataObject().getKey()).isEqualTo(key2);
        assertThat(result2.getMetaObject()).isNull();

        // no more data any more
        assertThat(_s3ObjectTupleIterator.hasNext()).isFalse();
    }

    @Test
    public void test_next_oneData_oneData_oneMetaData() {
        String key1 = "key1";
        String key2 = "key2";
        _s3ObjectIterator.addS3ObjectSummary(key1);
        _s3ObjectIterator.addS3ObjectSummary(key2);
        _s3ObjectIterator.addS3ObjectSummary(key2 + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();
        S3ObjectTuple result1 = _s3ObjectTupleIterator.next();

        assertThat(result1.getDataObject().getKey()).isEqualTo(key1);
        assertThat(result1.getMetaObject()).isNull();

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();

        S3ObjectTuple result2 = _s3ObjectTupleIterator.next();

        assertThat(result2.getDataObject().getKey()).isEqualTo(key2);
        assertThat(result2.getMetaObject().getKey()).isEqualTo(key2 + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        // no more data any more
        assertThat(_s3ObjectTupleIterator.hasNext()).isFalse();
    }

    @Test
    public void test_next_oneData_oneMetaData_oneData_oneData() {
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        _s3ObjectIterator.addS3ObjectSummary(key1);
        _s3ObjectIterator.addS3ObjectSummary(key1 + S3MediaExportRepository.METADATA_FILE_EXTENSION);
        _s3ObjectIterator.addS3ObjectSummary(key2);
        _s3ObjectIterator.addS3ObjectSummary(key3);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();
        S3ObjectTuple result1 = _s3ObjectTupleIterator.next();

        assertThat(result1.getDataObject().getKey()).isEqualTo(key1);
        assertThat(result1.getMetaObject().getKey()).isEqualTo(key1 + S3MediaExportRepository.METADATA_FILE_EXTENSION);

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();

        S3ObjectTuple result2 = _s3ObjectTupleIterator.next();

        assertThat(result2.getDataObject().getKey()).isEqualTo(key2);
        assertThat(result2.getMetaObject()).isNull();

        assertThat(_s3ObjectTupleIterator.hasNext()).isTrue();

        S3ObjectTuple result3 = _s3ObjectTupleIterator.next();

        assertThat(result3.getDataObject().getKey()).isEqualTo(key3);
        assertThat(result3.getMetaObject()).isNull();

        // no more data any more
        assertThat(_s3ObjectTupleIterator.hasNext()).isFalse();
    }

}
