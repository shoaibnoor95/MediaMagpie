package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.wehner.mediamagpie.common.simplenio.file.MMOpenOption;
import de.wehner.mediamagpie.common.simplenio.file.MMStandardOpenOption;

public class MMUnixFileChannel extends AbstractInterruptibleChannel implements SeekableByteChannel {

    private static final Logger LOG = LoggerFactory.getLogger(MMUnixFileChannel.class);

    private final File _file;
    private final Set<? extends MMOpenOption> _options;
    private final Object positionLock = new Object();
    private final OutputStream _os;
    private final InputStream _is;
    private long _position;

    public MMUnixFileChannel(File file, Set<? extends MMOpenOption> options) throws FileNotFoundException {
        super();
        _file = file;
        _options = options;
        if (options.contains(MMStandardOpenOption.WRITE)) {
            _os = new FileOutputStream(_file);
        } else {
            _os = null;
        }
        if (options.contains(MMStandardOpenOption.READ)) {
            _is = new FileInputStream(_file);
        } else {
            _is = null;
        }
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        ensureOpen();
        synchronized (this.positionLock) {
            try {
                begin();
                int remaining = dst.remaining();
                byte[] buffer = new byte[remaining];
                int readBytes = _is.read(buffer);
                if (readBytes > 0) {
                    dst.put(buffer, 0, readBytes);
                    dst.flip();
                }
                return readBytes;
            } finally {
                end(true);
            }
        }
    }

    @Override
    public int write(ByteBuffer paramByteBuffer) throws IOException {
        ensureOpen();
        synchronized (this.positionLock) {
            try {
                begin();
                byte[] array = paramByteBuffer.array();
                paramByteBuffer.flip();
                _os.write(array);
                _position += array.length;
                return array.length;
            } finally {
                end(true);
            }
        }
    }

    @Override
    public long position() throws IOException {
        ensureOpen();
        synchronized (this.positionLock) {
            return _position;
        }
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

    private void ensureOpen() throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
    }

    @Override
    protected void implCloseChannel() throws IOException {
        IOUtils.closeQuietly(_is);
        IOUtils.closeQuietly(_os);
        LOG.info("implCloseChannel()");
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
