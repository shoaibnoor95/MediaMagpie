package de.wehner.mediamagpie.common.test.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A {@link DirectoryWalker} Implementation to collect files and / or directories beginning at a specified folder.
 * <p>
 * Here are some examples how to use this class:
 * </p>
 * <b>a) Search all files with insensitive file name <code>"*.txt"</code> recursively:</b>
 * 
 * <pre>
 * FileUtilDirectoryWalker dw = new FileUtilDirectoryWalker(startFolder, TrueFileFilter.INSTANCE, new WildcardFileFilter(&quot;*.txt&quot;, IOCase.INSENSITIVE), true);
 * List&lt;File&gt; allTxtFiles = dw.getFiles(folder);
 * </pre>
 * 
 * <b>b) Search all subdirectories without files:</b>
 * 
 * <pre>
 * FileUtilDirectoryWalker dw = new FileUtilDirectoryWalker(startFolder, TrueFileFilter.INSTANCE, FalseFileFilter.INSTANCE, false);
 * List&lt;File&gt; allSubDirs = dw.getFiles(folder);
 * </pre>
 * 
 * <b>c) Search all subdirectories with exactly name <code>"out"</code> without files:</b>
 * 
 * <pre>
 * FileUtilDirectoryWalker dw = new FileUtilDirectoryWalker(startFolder, TrueFileFilter.INSTANCE, new NameFileFilter(&quot;out&quot;), false);
 * List&lt;File&gt; allSubDirs = dw.getFiles(folder);
 * </pre>
 * 
 * @author ralfwehner
 * 
 */
public class FileUtilDirectoryWalker extends DirectoryWalker<File> {

    private final boolean _collectOnlyFiles;

    public FileUtilDirectoryWalker(IOFileFilter dirFilter, IOFileFilter fileFilter, boolean collectOnlyFiles) {
        super(dirFilter, fileFilter, -1);
        _collectOnlyFiles = collectOnlyFiles;
    }

    @Override
    protected boolean handleDirectory(File directory, int depth, Collection<File> results) throws IOException {
        if (!_collectOnlyFiles && depth > 0) {
            results.add(directory);
        }
        return true;
    }

    @Override
    protected void handleFile(File file, int depth, Collection<File> results) throws IOException {
        results.add(file);
    }

    /**
     * Starts the search and provides all files and folders that matches to this object.
     * 
     * @param startDirectory
     *            The directory the search will start
     * @return All files and directories that were found
     * @throws IOException
     */
    public List<File> getFiles(File startDirectory) throws IOException {
        List<File> dirs = new ArrayList<File>();
        walk(startDirectory, dirs);
        return dirs;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}