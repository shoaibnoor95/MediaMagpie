package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.simplenio.file.MMOpenOption;
import de.wehner.mediamagpie.common.simplenio.file.MMStandardOpenOption;
import de.wehner.mediamagpie.common.test.util.TestEnvironment;

public class MMUnixFileChannel implements SeekableByteChannel {

    private static final Logger LOG = LoggerFactory.getLogger(MMUnixFileChannel.class);

    private final File _file;
    private final Set<? extends MMOpenOption> _options;

    public MMUnixFileChannel(File file, Set<? extends MMOpenOption> options) {
        super();
        _file = file;
        _options = options;
    }

    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void close() throws IOException {
        LOG.debug("close file '" + _file.getPath() + "'.");
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long position() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long size() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
