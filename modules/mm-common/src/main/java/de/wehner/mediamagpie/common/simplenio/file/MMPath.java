package de.wehner.mediamagpie.common.simplenio.file;

import java.net.URI;

public interface MMPath extends Comparable<MMPath>, Iterable<MMPath> {

    /**
     * Returns the file system that created this object.
     * 
     * @return the file system that created this object
     */
    MMFileSystem getFileSystem();

    /**
     * Returns the <em>parent path</em>, or {@code null} if this path does not have a parent.
     * 
     * <p>
     * The parent of this path object consists of this path's root component, if any, and each element in the path except for the
     * <em>farthest</em> from the root in the directory hierarchy. This method does not access the file system; the path or its parent may
     * not exist. Furthermore, this method does not eliminate special names such as "." and ".." that may be used in some implementations.
     * On UNIX for example, the parent of "{@code /a/b/c}" is "{@code /a/b}", and the parent of {@code "x/y/.}" is "{@code x/y}". This
     * method may be used with the {@link #normalize normalize} method, to eliminate redundant names, for cases where <em>shell-like</em>
     * navigation is required.
     * 
     * <p>
     * If this path has one or more elements, and no root component, then this method is equivalent to evaluating the expression:
     * <blockquote>
     * 
     * <pre>
     * subpath(0, getNameCount() - 1);
     * </pre>
     * 
     * </blockquote>
     * 
     * @return a path representing the path's parent
     */
    MMPath getParent();

    /**
     * Resolve the given path against this path.
     * 
     * <p>
     * If the {@code other} parameter is an {@link #isAbsolute() absolute} path then this method trivially returns {@code other}. If
     * {@code other} is an <i>empty path</i> then this method trivially returns this path. Otherwise this method considers this path to be a
     * directory and resolves the given path against this path. In the simplest case, the given path does not have a {@link #getRoot root}
     * component, in which case this method <em>joins</em> the given path to this path and returns a resulting path that {@link #endsWith
     * ends} with the given path. Where the given path has a root component then resolution is highly implementation dependent and therefore
     * unspecified.
     * 
     * @param other
     *            the path to resolve against this path
     * 
     * @return the resulting path
     * 
     * @see #relativize
     */
    MMPath resolve(MMPath other);

    /**
     * Converts a given path string to a {@code Path} and resolves it against this {@code Path} in exactly the manner specified by the
     * {@link #resolve(Path) resolve} method. For example, suppose that the name separator is "{@code /}" and a path represents "
     * {@code foo/bar}", then invoking this method with the path string "{@code gus}" will result in the {@code Path} "{@code foo/bar/gus}".
     * 
     * @param other
     *            the path string to resolve against this path
     * 
     * @return the resulting path
     * 
     * @throws InvalidPathException
     *             if the path string cannot be converted to a Path.
     * 
     * @see FileSystem#getPath
     */
    MMPath resolve(String other);

    /**
     * Returns the number of name elements in the path.
     * 
     * @return the number of elements in the path, or {@code 0} if this path only represents a root component
     */
    int getNameCount();

     /**
     * Returns a name element of this path as a {@code Path} object.
     *
     * <p>
     * The {@code index} parameter is the index of the name element to return. The element that is <em>closest</em> to the root in the
     * directory hierarchy has index {@code 0}. The element that is <em>farthest</em> from the root has index {@link #getNameCount count}
     * {@code -1}.
     *
     * @param index
     * the index of the element
     *
     * @return the name element
     *
     * @throws IllegalArgumentException
     * if {@code index} is negative, {@code index} is greater than or equal to the number of elements, or this path has zero
     * name elements
     */
     MMPath getName(int index);

    /**
     * Returns the name of the file or directory denoted by this path as a {@code Path} object. The file name is the <em>farthest</em>
     * element from the root in the directory hierarchy.
     * 
     * @return a path representing the name of the file or directory, or {@code null} if this path has zero elements
     */
    MMPath getFileName();

    /**
     * Returns a URI to represent this path.
     * 
     * <p>
     * This method constructs an absolute {@link URI} with a {@link URI#getScheme() scheme} equal to the URI scheme that identifies the
     * provider. The exact form of the scheme specific part is highly provider dependent.
     * 
     * <p>
     * In the case of the default provider, the URI is hierarchical with a {@link URI#getPath() path} component that is absolute. The query
     * and fragment components are undefined. Whether the authority component is defined or not is implementation dependent. There is no
     * guarantee that the {@code URI} may be used to construct a {@link java.io.File java.io.File}. In particular, if this path represents a
     * Universal Naming Convention (UNC) path, then the UNC server name may be encoded in the authority component of the resulting URI. In
     * the case of the default provider, and the file exists, and it can be determined that the file is a directory, then the resulting
     * {@code URI} will end with a slash.
     * 
     * <p>
     * The default provider provides a similar <em>round-trip</em> guarantee to the {@link java.io.File} class. For a given {@code Path}
     * <i>p</i> it is guaranteed that <blockquote><tt>
     * {@link Paths#get(URI) Paths.get}(</tt><i>p</i><tt>.toUri()).equals(</tt><i>p</i>
     * <tt>.{@link #toAbsolutePath() toAbsolutePath}())</tt> </blockquote> so long as the original {@code Path}, the {@code URI}, and the
     * new {@code Path} are all created in (possibly different invocations of) the same Java virtual machine. Whether other providers make
     * any guarantees is provider specific and therefore unspecified.
     * 
     * <p>
     * When a file system is constructed to access the contents of a file as a file system then it is highly implementation specific if the
     * returned URI represents the given path in the file system or it represents a <em>compound</em> URI that encodes the URI of the
     * enclosing file system. A format for compound URIs is not defined in this release; such a scheme may be added in a future release.
     * 
     * @return the URI representing this path
     * 
     * @throws java.io.IOError
     *             if an I/O error occurs obtaining the absolute path, or where a file system is constructed to access the contents of a
     *             file as a file system, and the URI of the enclosing file system cannot be obtained
     * 
     * @throws SecurityException
     *             In the case of the default provider, and a security manager is installed, the {@link #toAbsolutePath toAbsolutePath}
     *             method throws a security exception.
     */
    URI toUri();

    /**
     * Returns the string representation of this path.
     * 
     * <p>
     * If this path was created by converting a path string using the {@link FileSystem#getPath getPath} method then the path string
     * returned by this method may differ from the original String used to create the path.
     * 
     * <p>
     * The returned path string uses the default name {@link FileSystem#getSeparator separator} to separate names in the path.
     * 
     * @return the string representation of this path
     */
    String toString();

}
