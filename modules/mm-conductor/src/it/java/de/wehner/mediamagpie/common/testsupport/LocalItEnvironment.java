package de.wehner.mediamagpie.common.testsupport;


public class LocalItEnvironment extends ItEnvironment {

    public LocalItEnvironment(CleanFolderInstruction cleanFolderInstruction) {
        super(cleanFolderInstruction/*, HadoopUtil.createConf()*/);
    }

//    @Override
//    public boolean isClusteredEnvironment() throws IOException {
//        return false;
//    }
//
//    @Override
//    protected String getDbUrl(DbType dbType) throws Exception {
//        if (dbType.isSame(BuiltInDbType.HSQL_FILE) || dbType.isSame(BuiltInDbType.HSQL_HTTP)) {
//            return HsqlDb.memUrl();
//        }
//        return Ec2Setup.getEc2Setup().getDbUrl(dbType);
//        // throw new UnsupportedOperationException("no db url for type " + dbType.getName() +
//        // " for " + this.getClass().getSimpleName() + " configured");
//    }
//
//    @Override
//    public Path getDfsTmpFile(String name) throws IOException {
//        return new Path(getTmpFile(name).getAbsolutePath());
//    }
//
//    @Override
//    public FileSystem getFileSystem() throws IOException {
//        return FileSystem.getLocal(_hadoopConf);
//    }
//
//    @Override
//    protected String copyFileToImportableFileLocation(File localFile) throws Exception {
//        // already locally existent
//        return localFile.getCanonicalPath();
//    }
//
//    @Override
//    protected void setFileConnectionPassword(FileConnection fileConnection) {
//        // no password to set
//    }
//
//    @Override
//    protected String getImportFileUrlPrefix() throws Exception {
//        return "file:/";
//    }
//
//    @Override
//    protected FileDataAccess getSrcDataAccess() {
//        return new FileDataAccess(FileDataLocation.LOCAL, FileProtocol.FILE);
//    }
//
//    @Override
//    public String getAbsoluteDataPath(String filename) throws Exception {
//        // new File(findFile(filename)).getCanonicalPath();
//        return getSrcDataAccess().getDataLocation().getTestDataFile(filename);
//    }
//
//    @Override
//    protected void initConfiguration(Configuration configuration) {
//        configuration.setInt(HadoopConstants.NUMBER_OF_MAP_TASKS_PER_JOB, 1);
//    }
}
