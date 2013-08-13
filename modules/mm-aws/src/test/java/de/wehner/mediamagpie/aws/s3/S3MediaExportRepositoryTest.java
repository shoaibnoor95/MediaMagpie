package de.wehner.mediamagpie.aws.s3;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.api.MediaStorageInfo;

public class S3MediaExportRepositoryTest {

    private S3MediaExportRepository _s3MediaExportRepository;

    @Mock
    private S3ClientFacade _s3facde;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        _s3MediaExportRepository = new S3MediaExportRepository(_s3facde);

    }

    @Test
    public void test_deleteMediaStoragePath_DeletePathCompletely() {
        final String PATH = "blah/PHOTO/SHA1-123abc";
        final String OBJ_PATH = PATH + "/IMG_123.jpg";
        MediaStorageInfo mediaStorageInfo = new MediaStorageInfo(OBJ_PATH, OBJ_PATH + ".METADATA");
        when(_s3facde.listKeysInPath("bucketName", PATH)).thenReturn(new ArrayList<String>());

        _s3MediaExportRepository.deleteMediaStoragePath("bucketName", mediaStorageInfo);

        // verify, only once the deletePath() of object is called and nothing more
        verify(_s3facde).deletePath("bucketName", OBJ_PATH);
        verify(_s3facde, never()).deletePath("bucketName", PATH);
    }

    @Test
    public void test_deleteMediaStoragePath_DoNotDeletePathCompletely() {
        final String PATH = "blah/PHOTO/SHA1-123abc";
        final String OBJ_PATH = PATH + "/IMG_123.jpg";
        MediaStorageInfo mediaStorageInfo = new MediaStorageInfo(OBJ_PATH, OBJ_PATH + ".METADATA");
        when(_s3facde.listKeysInPath("bucketName", PATH)).thenReturn(Arrays.asList(PATH + "IM_456.jpg", PATH + "IM_456.jpg.METADATA"));

        _s3MediaExportRepository.deleteMediaStoragePath("bucketName", mediaStorageInfo);

        // verify, only once the deletePath() of object is called and nothing more
        verify(_s3facde).deletePath("bucketName", OBJ_PATH);
        verify(_s3facde, never()).deletePath("bucketName", PATH);
    }
}
