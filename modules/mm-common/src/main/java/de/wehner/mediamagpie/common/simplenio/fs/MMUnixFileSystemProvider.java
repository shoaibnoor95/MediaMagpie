package de.wehner.mediamagpie.common.simplenio.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.FileUtils;

import de.wehner.mediamagpie.common.simplenio.file.MMDirectoryNotEmptyException;
import de.wehner.mediamagpie.common.simplenio.file.MMFileSystem;
import de.wehner.mediamagpie.common.simplenio.file.MMPath;
import de.wehner.mediamagpie.common.simplenio.file.attribute.MMBasicFileAttributes;
import de.wehner.mediamagpie.common.simplenio.file.attribute.MMBasicFileAttributesImpl;

public class MMUnixFileSystemProvider extends MMAbstractFileSystemProvider {

    @Override
    boolean implDelete(MMPath paramPath, boolean paramBoolean) throws IOException {
        MMUnixPath localUnixPath = MMUnixPath.toUnixPath(paramPath);
        localUnixPath.checkDelete();
        File file = new File(paramPath.toString());
        if (file.isDirectory()) {
            File[] filesInDir = file.listFiles();
            if (filesInDir != null && filesInDir.length > 0) {
                throw new MMDirectoryNotEmptyException(localUnixPath.getPathForExecptionMessage());
            }
            return file.delete();
        } else {
            return file.delete();
        }
    }

    @Override
    public MMFileSystem getFileSystem(URI uri) {
        checkUri(uri);
        return this.theFileSystem;
    }

    @Override
    public InputStream newInputStream(MMPath path) throws IOException {
        File file = new File(path.toString());
        return new FileInputStream(file);
    }

    @Override
    public OutputStream newOutputStream(MMPath path) throws IOException {
        File file = new File(path.toString());
        return new FileOutputStream(file);
    }

    @Override
    public void createDirectory(MMPath dir) throws IOException {
        File file = new File(dir.toString());
        file.mkdir();
    }

    @Override
    public void copy(MMPath source, MMPath target) throws IOException {
        File fileSrc = new File(source.toString());
        File fileTarget = new File(target.toString());
        FileUtils.copyFile(fileSrc, fileTarget);
    }

    @Override
    public void move(MMPath source, MMPath target) throws IOException {
        File fileSrc = new File(source.toString());
        File fileTarget = new File(target.toString());
        fileSrc.renameTo(fileTarget);
    }

    // private static final String USER_DIR = "user.dir";
    private final MMUnixFileSystem theFileSystem;

    public MMUnixFileSystemProvider() {
        String str = System.getProperty("user.dir");
        this.theFileSystem = newFileSystem(str);
    }

    MMUnixFileSystem newFileSystem(String paramString) {
        return new MMUnixFileSystem(this, paramString);
    }

    public final String getScheme() {
        return "file";
    }

    private void checkUri(URI paramURI) {
        if (!paramURI.getScheme().equalsIgnoreCase(getScheme()))
            throw new IllegalArgumentException("URI does not match this provider");
        if (paramURI.getAuthority() != null)
            throw new IllegalArgumentException("Authority component present");
        if (paramURI.getPath() == null)
            throw new IllegalArgumentException("Path component is undefined");
        if (!paramURI.getPath().equals("/"))
            throw new IllegalArgumentException("Path component should be '/'");
        if (paramURI.getQuery() != null)
            throw new IllegalArgumentException("Query component present");
        if (paramURI.getFragment() != null)
            throw new IllegalArgumentException("Fragment component present");
    }

    //
    // public final MMFileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap) {
    // checkUri(paramURI);
    // throw new FileSystemAlreadyExistsException();
    // }
    //
    // public final MMFileSystem getFileSystem(URI paramURI) {
    // checkUri(paramURI);
    // return this.theFileSystem;
    // }
    //
    @Override
    public MMPath getPath(URI paramURI) {
        return UnixUriUtils.fromUri(this.theFileSystem, paramURI);
    }

    //
    // MMUnixPath checkPath(MMPath paramPath) {
    // if (paramPath == null)
    // throw new NullPointerException();
    // if (!(paramPath instanceof MMUnixPath))
    // throw new MMProviderMismatchException();
    // return (MMUnixPath) paramPath;
    // }
    //
    // // public <V extends FileAttributeView> V getFileAttributeView(Path paramPath, Class<V> paramClass, LinkOption[]
    // paramArrayOfLinkOption)
    // // {
    // // UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
    // // boolean bool = Util.followLinks(paramArrayOfLinkOption);
    // // if (paramClass == BasicFileAttributeView.class)
    // // return UnixFileAttributeViews.createBasicView(localUnixPath, bool);
    // // if (paramClass == PosixFileAttributeView.class)
    // // return UnixFileAttributeViews.createPosixView(localUnixPath, bool);
    // // if (paramClass == FileOwnerAttributeView.class)
    // // return UnixFileAttributeViews.createOwnerView(localUnixPath, bool);
    // // if (paramClass == null)
    // // throw new NullPointerException();
    // // return (FileAttributeView)null;
    // // }
    //
    // public <A extends MMBasicFileAttributes> A readAttributes(MMPath paramPath, Class<A> paramClass) throws IOException {
    // Object localObject;
    // if (paramClass == MMBasicFileAttributes.class) {
    // localObject = MMBasicFileAttributeView.class;
    // } else if (paramClass == PosixFileAttributes.class) {
    // localObject = PosixFileAttributeView.class;
    // } else {
    // if (paramClass == null) {
    // throw new NullPointerException();
    // }
    // throw new UnsupportedOperationException();
    // }
    // return (TA) ((BasicFileAttributeView) getFileAttributeView(paramPath, (Class) localObject, paramArrayOfLinkOption)).readAttributes();
    // }

    public <A extends MMBasicFileAttributes> A readAttributes(MMPath paramPath, Class<A> paramClass) throws IOException {
        Object localObject;
        if (paramClass == MMBasicFileAttributes.class) {
            File file = new File(paramPath.toString());
            return (A) new MMBasicFileAttributesImpl(file.lastModified(), file.isDirectory(), file.length());
        } else {
            if (paramClass == null) {
                throw new NullPointerException();
            }
            throw new UnsupportedOperationException();
        }
    }

    // // protected DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption[] paramArrayOfLinkOption)
    // // {
    // // UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
    // // boolean bool = Util.followLinks(paramArrayOfLinkOption);
    // // if (paramString.equals("basic"))
    // // return UnixFileAttributeViews.createBasicView(localUnixPath, bool);
    // // if (paramString.equals("posix"))
    // // return UnixFileAttributeViews.createPosixView(localUnixPath, bool);
    // // if (paramString.equals("unix"))
    // // return UnixFileAttributeViews.createUnixView(localUnixPath, bool);
    // // if (paramString.equals("owner"))
    // // return UnixFileAttributeViews.createOwnerView(localUnixPath, bool);
    // // return null;
    // // }
    //
    // // public FileChannel newFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, FileAttribute<?>[]
    // paramArrayOfFileAttribute)
    // // throws IOException
    // // {
    // // UnixPath localUnixPath = checkPath(paramPath);
    // // int i = UnixFileModeAttribute.toUnixMode(438, paramArrayOfFileAttribute);
    // // try
    // // {
    // // return UnixChannelFactory.newFileChannel(localUnixPath, paramSet, i);
    // // } catch (UnixException localUnixException) {
    // // localUnixException.rethrowAsIOException(localUnixPath);
    // // }return null;
    // // }
    // //
    // // public AsynchronousFileChannel newAsynchronousFileChannel(Path paramPath, Set<? extends OpenOption> paramSet, ExecutorService
    // // paramExecutorService, FileAttribute<?>[] paramArrayOfFileAttribute)
    // // throws IOException
    // // {
    // // UnixPath localUnixPath = checkPath(paramPath);
    // // int i = UnixFileModeAttribute.toUnixMode(438, paramArrayOfFileAttribute);
    // //
    // // ThreadPool localThreadPool = paramExecutorService == null ? null : ThreadPool.wrap(paramExecutorService, 0);
    // // try {
    // // return UnixChannelFactory.newAsynchronousFileChannel(localUnixPath, paramSet, i, localThreadPool);
    // // }
    // // catch (UnixException localUnixException) {
    // // localUnixException.rethrowAsIOException(localUnixPath);
    // // }return null;
    // // }
    // //
    // // public MMSeekableByteChannel newByteChannel(MMPath paramPath)
    // // throws IOException
    // // {
    // // MMUnixPath localUnixPath = MMUnixPath.toUnixPath(paramPath);
    // // int i = UnixFileModeAttribute.toUnixMode(438, paramArrayOfFileAttribute);
    // // try
    // // {
    // // return UnixChannelFactory.newFileChannel(localUnixPath, paramSet, i);
    // // } catch (UnixException localUnixException) {
    // // localUnixException.rethrowAsIOException(localUnixPath);
    // // }return null;
    // // }
    // //
    // boolean implDelete(MMPath paramPath, boolean paramBoolean)
    // throws IOException
    // {
    // MMUnixPath localUnixPath = MMUnixPath.toUnixPath(paramPath);
    // localUnixPath.checkDelete();
    //
    // // MMUnixFileAttributes localUnixFileAttributes = null;
    // // try {
    // // localUnixFileAttributes = UnixFileAttributes.get(localUnixPath, false);
    // // if (localUnixFileAttributes.isDirectory())
    // // UnixNativeDispatcher.rmdir(localUnixPath);
    // // else {
    // // UnixNativeDispatcher.unlink(localUnixPath);
    // // }
    // // return true;
    // // }
    // // catch (UnixException localUnixException) {
    // // if ((!paramBoolean) && (localUnixException.errno() == 2)) {
    // // return false;
    // // }
    // //
    // // if ((localUnixFileAttributes != null) && (localUnixFileAttributes.isDirectory()) && ((localUnixException.errno() == 17) ||
    // (localUnixException.errno() == 66)))
    // // {
    // // throw new DirectoryNotEmptyException(localUnixPath.getPathForExecptionMessage());
    // // }
    // // localUnixException.rethrowAsIOException(localUnixPath);
    // // }return false;
    // File file = new File(paramPath.toString());
    // if(file.isDirectory()){
    // File[] filesInDir = file.listFiles();
    // if(filesInDir != null && filesInDir.length > 0){
    // throw new MMDirectoryNotEmptyException(localUnixPath.getPathForExecptionMessage());
    // }
    // file.delete();
    // }else{
    // file.delete();
    // }
    // }
    //
    // public void copy(MMPath paramPath1, MMPath paramPath2)
    // throws IOException
    // {
    // MMUnixCopyFile.copy(UnixPath.toUnixPath(paramPath1), UnixPath.toUnixPath(paramPath2), paramArrayOfCopyOption);
    // }
    //
    // public void move(Path paramPath1, Path paramPath2, CopyOption[] paramArrayOfCopyOption)
    // throws IOException
    // {
    // UnixCopyFile.move(UnixPath.toUnixPath(paramPath1), UnixPath.toUnixPath(paramPath2), paramArrayOfCopyOption);
    // }
    //
    // public void checkAccess(Path paramPath, AccessMode[] paramArrayOfAccessMode)
    // throws IOException
    // {
    // UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
    // int i = 0;
    // int j = 0;
    // int k = 0;
    // int m = 0;
    //
    // if (paramArrayOfAccessMode.length == 0)
    // i = 1;
    // else {
    // for (AccessMode localAccessMode : paramArrayOfAccessMode) {
    // switch (1.$SwitchMap$java$nio$file$AccessMode[localAccessMode.ordinal()]) { case 1:
    // j = 1; break;
    // case 2:
    // k = 1; break;
    // case 3:
    // m = 1; break;
    // default:
    // throw new AssertionError("Should not get here");
    // }
    // }
    // }
    //
    // int n = 0;
    // if ((i != 0) || (j != 0)) {
    // localUnixPath.checkRead();
    // n |= (j != 0 ? 4 : 0);
    // }
    // if (k != 0) {
    // localUnixPath.checkWrite();
    // n |= 2;
    // }
    // if (m != 0) {
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // if (localSecurityManager != null)
    // {
    // localSecurityManager.checkExec(localUnixPath.getPathForPermissionCheck());
    // }
    // n |= 1;
    // }
    // try {
    // UnixNativeDispatcher.access(localUnixPath, n);
    // } catch (UnixException localUnixException) {
    // localUnixException.rethrowAsIOException(localUnixPath);
    // }
    // }
    //
    // public boolean isSameFile(Path paramPath1, Path paramPath2) throws IOException
    // {
    // UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath1);
    // if (localUnixPath1.equals(paramPath2))
    // return true;
    // if (paramPath2 == null)
    // throw new NullPointerException();
    // if (!(paramPath2 instanceof UnixPath))
    // return false; UnixPath localUnixPath2 = (UnixPath)paramPath2;
    //
    // localUnixPath1.checkRead();
    // localUnixPath2.checkRead();
    // UnixFileAttributes localUnixFileAttributes1;
    // try {
    // localUnixFileAttributes1 = UnixFileAttributes.get(localUnixPath1, true);
    // } catch (UnixException localUnixException1) {
    // localUnixException1.rethrowAsIOException(localUnixPath1);
    // return false;
    // }UnixFileAttributes localUnixFileAttributes2;
    // try { localUnixFileAttributes2 = UnixFileAttributes.get(localUnixPath2, true);
    // } catch (UnixException localUnixException2) {
    // localUnixException2.rethrowAsIOException(localUnixPath2);
    // return false;
    // }
    // return localUnixFileAttributes1.isSameFile(localUnixFileAttributes2);
    // }
    //
    // public boolean isHidden(Path paramPath)
    // {
    // UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath);
    // localUnixPath1.checkRead();
    // UnixPath localUnixPath2 = localUnixPath1.getFileName();
    // if (localUnixPath2 == null)
    // return false;
    // return localUnixPath2.asByteArray()[0] == 46;
    // }
    //
    // abstract FileStore getFileStore(UnixPath paramUnixPath)
    // throws IOException;
    //
    // public FileStore getFileStore(Path paramPath)
    // throws IOException
    // {
    // UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // if (localSecurityManager != null) {
    // localSecurityManager.checkPermission(new RuntimePermission("getFileStoreAttributes"));
    // localUnixPath.checkRead();
    // }
    // return getFileStore(localUnixPath);
    // }
    //
    // public void createDirectory(Path paramPath, FileAttribute<?>[] paramArrayOfFileAttribute)
    // throws IOException
    // {
    // UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
    // localUnixPath.checkWrite();
    //
    // int i = UnixFileModeAttribute.toUnixMode(511, paramArrayOfFileAttribute);
    // try
    // {
    // UnixNativeDispatcher.mkdir(localUnixPath, i);
    // } catch (UnixException localUnixException) {
    // localUnixException.rethrowAsIOException(localUnixPath);
    // }
    // }
    //
    // public DirectoryStream<Path> newDirectoryStream(Path paramPath, DirectoryStream.Filter<? super Path> paramFilter)
    // throws IOException
    // {
    // UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
    // localUnixPath.checkRead();
    // if (paramFilter == null) {
    // throw new NullPointerException();
    // }
    //
    // if (!UnixNativeDispatcher.supportsAtSysCalls()) {
    // try {
    // long l1 = UnixNativeDispatcher.opendir(localUnixPath);
    // return new UnixDirectoryStream(localUnixPath, l1, paramFilter);
    // } catch (UnixException localUnixException1) {
    // if (localUnixException1.errno() == 20)
    // throw new NotDirectoryException(localUnixPath.getPathForExecptionMessage());
    // localUnixException1.rethrowAsIOException(localUnixPath);
    // }
    //
    // }
    //
    // int i = -1;
    // int j = -1;
    // long l2 = 0L;
    // try {
    // i = UnixNativeDispatcher.open(localUnixPath, 0, 0);
    // j = UnixNativeDispatcher.dup(i);
    // l2 = UnixNativeDispatcher.fdopendir(i);
    // } catch (UnixException localUnixException2) {
    // if (i != -1)
    // UnixNativeDispatcher.close(i);
    // if (j != -1)
    // UnixNativeDispatcher.close(j);
    // if (localUnixException2.errno() == 20)
    // throw new NotDirectoryException(localUnixPath.getPathForExecptionMessage());
    // localUnixException2.rethrowAsIOException(localUnixPath);
    // }
    // return new UnixSecureDirectoryStream(localUnixPath, l2, j, paramFilter);
    // }
    //
    // public void createSymbolicLink(Path paramPath1, Path paramPath2, FileAttribute<?>[] paramArrayOfFileAttribute)
    // throws IOException
    // {
    // UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath1);
    // UnixPath localUnixPath2 = UnixPath.toUnixPath(paramPath2);
    //
    // if (paramArrayOfFileAttribute.length > 0) {
    // UnixFileModeAttribute.toUnixMode(0, paramArrayOfFileAttribute);
    // throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
    // }
    //
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // if (localSecurityManager != null) {
    // localSecurityManager.checkPermission(new LinkPermission("symbolic"));
    // localUnixPath1.checkWrite();
    // }
    //
    // try
    // {
    // UnixNativeDispatcher.symlink(localUnixPath2.asByteArray(), localUnixPath1);
    // } catch (UnixException localUnixException) {
    // localUnixException.rethrowAsIOException(localUnixPath1);
    // }
    // }
    //
    // public void createLink(Path paramPath1, Path paramPath2) throws IOException
    // {
    // UnixPath localUnixPath1 = UnixPath.toUnixPath(paramPath1);
    // UnixPath localUnixPath2 = UnixPath.toUnixPath(paramPath2);
    //
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // if (localSecurityManager != null) {
    // localSecurityManager.checkPermission(new LinkPermission("hard"));
    // localUnixPath1.checkWrite();
    // localUnixPath2.checkWrite();
    // }
    // try {
    // UnixNativeDispatcher.link(localUnixPath2, localUnixPath1);
    // } catch (UnixException localUnixException) {
    // localUnixException.rethrowAsIOException(localUnixPath1, localUnixPath2);
    // }
    // }
    //
    // public Path readSymbolicLink(Path paramPath) throws IOException
    // {
    // UnixPath localUnixPath = UnixPath.toUnixPath(paramPath);
    //
    // SecurityManager localSecurityManager = System.getSecurityManager();
    // Object localObject;
    // if (localSecurityManager != null) {
    // localObject = new FilePermission(localUnixPath.getPathForPermissionCheck(), "readlink");
    //
    // AccessController.checkPermission((Permission)localObject);
    // }
    // try {
    // localObject = UnixNativeDispatcher.readlink(localUnixPath);
    // return new UnixPath(localUnixPath.getFileSystem(), localObject);
    // } catch (UnixException localUnixException) {
    // if (localUnixException.errno() == 22)
    // throw new NotLinkException(localUnixPath.getPathForExecptionMessage());
    // localUnixException.rethrowAsIOException(localUnixPath);
    // }return (Path)null;
    // }
    // }
}
