package de.wehner.mediamagpie.common.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.junit.Test;

import de.wehner.mediamagpie.core.util.TimeUtil;

public class TimeUtilTest {

    @Test
    public void testBuildFileNameWithTimeStamp() {
        Calendar calendar = new GregorianCalendar(Locale.US);
        calendar.set(2010, 4, 20, 8, 5, 17);
        Date date = calendar.getTime();
        assertEquals(new File("a100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("a.txt"), date, null));
        assertEquals(new File("12100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("12.txt"), date, null));
        assertEquals(new File("/a100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("/a.txt"), date, null));
        assertEquals(new File("./a100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("./a.txt"), date, null));
        assertEquals(new File(".a100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File(".a.txt"), date, null));
        assertEquals(new File("path/a100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("path/a.txt"), date, null));
        assertEquals(new File("/path/a100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("/path/a.txt"), date, null));
        if (File.pathSeparator.equals("\\")) {
            assertEquals(new File("d:\\path\\a100520_080517.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("d:\\path\\a.txt"), date, null));
        }
    }
}
