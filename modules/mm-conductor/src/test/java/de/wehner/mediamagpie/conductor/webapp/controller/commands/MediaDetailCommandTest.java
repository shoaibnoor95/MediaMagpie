package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

import de.wehner.mediamagpie.persistence.entity.User;
import de.wehner.mediamagpie.persistence.util.MediaBuilder;

public class MediaDetailCommandTest {

    @Test
    public void test() {
        MediaBuilder mediaBuilder = new MediaBuilder();
        MediaDetailCommand createFromMedia = MediaDetailCommand.createFromMedia(mediaBuilder.build());

        // verify, the tricky authority mapping works
        assertThat(createFromMedia).isNotNull();
        assertThat(createFromMedia.getOwner().getAuthorities().iterator().next().getAuthority()).isEqualTo(new User.UserGrantedAuthority("ROLE_USER").getAuthority());
    }
}
