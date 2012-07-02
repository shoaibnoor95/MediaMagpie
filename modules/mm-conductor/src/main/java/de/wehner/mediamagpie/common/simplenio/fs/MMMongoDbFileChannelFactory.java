package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.wehner.mediamagpie.common.simplenio.channels.MMSeekableByteChannel;
import de.wehner.mediamagpie.common.simplenio.file.MMOpenOption;
import de.wehner.mediamagpie.common.simplenio.file.MMStandardOpenOption;

public class MMMongoDbFileChannelFactory {

    public static MMSeekableByteChannel newFileChannel(MMMongoPath mongoPath, Set<? extends MMOpenOption> options) throws IOException {
        File localFile = open(mongoPath, options);
        return new MMUnixFileChannel(localFile, options);
    }

    protected static File open(MMMongoPath mongoPath, Set<? extends MMOpenOption> options) throws IOException {
        // create file if not present now
//        File file = mongoPath.getPath();
//        if (options.contains(MMStandardOpenOption.CREATE_NEW) && !file.exists()) {
//            file.createNewFile();
//        }
//        return file;
        return null;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
