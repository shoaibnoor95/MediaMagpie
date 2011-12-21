package de.wehner.mediamagpie.conductor.webapp.tags;

import static org.fest.assertions.Assertions.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Test;

import de.wehner.mediamagpie.conductor.webapp.tags.DateTag;

public class DateTagTest {
    
    @Test
    public void testDoStartTag() throws ParseException {
        DateTag tag = new DateTag();
        assertThat(tag.getText()).isEqualTo("n.a.");
        tag.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2009-04-06 10:11:09"));
        assertThat(tag.getText()).isEqualTo("2009-04-06 10:11:09");
    }
}
