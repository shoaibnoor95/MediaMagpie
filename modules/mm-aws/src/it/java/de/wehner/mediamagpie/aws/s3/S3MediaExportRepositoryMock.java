package de.wehner.mediamagpie.aws.s3;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.wehner.mediamagpie.api.MediaExport;
import de.wehner.mediamagpie.api.MediaExportResults;
import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.MediaExportFactory;
import de.wehner.mediamagpie.persistence.entity.Media;

public class S3MediaExportRepositoryMock extends S3MediaExportRepository {

    private final List<MediaExport> _mediasInBucket = new ArrayList<MediaExport>();
    private final List<MediaExport> _mediasPushedToS3 = new ArrayList<MediaExport>();

    public S3MediaExportRepositoryMock() {
        super((S3ClientFacade) null);
    }

    public S3MediaExportRepositoryMock addMediaOnS3(Media media) {
        MediaExportFactory mediaExportFactory = new MediaExportFactory();
        try {
            _mediasInBucket.add(mediaExportFactory.create(media));
        } catch (FileNotFoundException e) {
            ExceptionUtil.convertToRuntimeException(e);
        }
        return this;
    }

    public List<MediaExport> getMediasPushedToS3() {
        return _mediasPushedToS3;
    }

    @Override
    public MediaExportResults addMedia(String userName, MediaExport mediaExport) {
        _mediasPushedToS3.add(mediaExport);
        return null;
    }

    @Override
    public Iterator<MediaExport> iteratorPhotos(String user) {
        return _mediasInBucket.iterator();
    }

    @Override
    public Iterator<MediaExport> iteratorVideos() {
        return null;
    }

}
