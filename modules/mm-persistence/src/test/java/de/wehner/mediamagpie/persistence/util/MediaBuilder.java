package de.wehner.mediamagpie.persistence.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.Date;

import de.wehner.mediamagpie.core.util.ExceptionUtil;
import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.entity.User.Role;

public class MediaBuilder {

    private User owner = new User("Bela Birne", "bela.birne@testmail.com", Role.USER);
    private String name = "test media";
    private URI mediaFileUri = new File("../mm-conductor/src/test/resources/images/4x1600.jpg").toURI();
    private Date creationDate = new Date();
    private String mediaType = "image/jpeg";

    public Media build() {
        try {
            Media media = Media.createWithHashValue(owner, name, mediaFileUri, creationDate, mediaType);
            return media;
        } catch (FileNotFoundException e) {
            throw ExceptionUtil.convertToRuntimeException(e);
        }
    }
}
