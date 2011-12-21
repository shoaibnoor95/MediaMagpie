package de.wehner.mediamagpie.common.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import de.wehner.mediamagpie.common.util.TimeUtil;

public class TimeUtilTest {

    @Test
    public void testBuildFileNameWithTimeStamp() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(0);
        calendar.set(2010, 4, 20);
        Date date = calendar.getTime();
        assertEquals(new File("a100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("a.txt"), date, null));
        assertEquals(new File("12100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("12.txt"), date, null));
        assertEquals(new File("/a100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("/a.txt"), date, null));
        assertEquals(new File("./a100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("./a.txt"), date, null));
        assertEquals(new File(".a100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File(".a.txt"), date, null));
        assertEquals(new File("path/a100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("path/a.txt"), date, null));
        assertEquals(new File("/path/a100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("/path/a.txt"), date, null));
        if (File.pathSeparator.equals("\\")) {
            assertEquals(new File("d:\\path\\a100520_010000.txt"), TimeUtil.buildFileNameWithTimeStamp(new File("d:\\path\\a.txt"), date, null));
        }
    }
}
