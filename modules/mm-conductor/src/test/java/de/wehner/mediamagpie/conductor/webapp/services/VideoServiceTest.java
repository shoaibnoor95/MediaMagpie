package de.wehner.mediamagpie.conductor.webapp.services;

import org.junit.Test;

public class VideoServiceTest {

    /*
     * see: http://paulrouget.com/e/converttohtml5video/
     * 
     */
    // ffmpeg -i /Users/ralfwehner/projects/wehner/mediamagpie/modules/mm-conductor/src/main/webapp/static/MVI_2734.MOV -q:a 0 -strict -2 /Users/ralfwehner/projects/wehner/mediamagpie/modules/mm-conductor/src/main/webapp/static/MVI_2734.MP4
    // ffmpeg -i MVI_2734.MOV -acodec libvorbis -ac 2 -ab 96k -ar 44100   -b 345k -s 640x360 MVI_2734.webm
    @Test
    public void testConvertMOV2MP4() {
        
    }
}
