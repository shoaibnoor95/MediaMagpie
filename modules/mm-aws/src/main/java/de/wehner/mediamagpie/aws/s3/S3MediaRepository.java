package de.wehner.mediamagpie.aws.s3;

import java.util.Iterator;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import de.wehner.mediamagpie.api.MediaExport;

public class S3MediaRepository {

    private final AmazonS3 _s3;

    public S3MediaRepository(AWSCredentials credentials) {
        _s3 = new AmazonS3Client(credentials);
    }

    /** export functionality */
    public void addMedia(MediaExport mediaExport){
        
    }
    
    /** import functionality */
    
    public Iterator<MediaExport> iteratorPhotos() {
        return null;
    }

    public Iterator<MediaExport> iteratorVideos() {
        return null;
    }
}
