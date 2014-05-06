package de.wehner.mediamagpie.persistence.dao;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import de.wehner.mediamagpie.persistence.entity.Media;
import de.wehner.mediamagpie.persistence.entity.S3JobExecution;
import de.wehner.mediamagpie.persistence.testsupport.DbTestEnvironment;

public class S3JobExecutionDaoTest {

    @Rule
    public DbTestEnvironment _dbTestEnvironment = new DbTestEnvironment();

    private MediaDao _mediaDao;

    @Before
    public void setUp() throws IOException {
        _dbTestEnvironment.cleanDb();
        _dbTestEnvironment.beginTransaction();
        _mediaDao = new MediaDao(_dbTestEnvironment.getPersistenceService());
    }

    @Test
    public void test_deleteMediaAlthoughtAJobToThisMediaAlreadyExists() throws Exception {
        try {
            // create Media and associated S3JobExecution
            Media m1 = _dbTestEnvironment.createNewMedia("m1");
            S3JobExecution s3JobExecution = new S3JobExecution(m1, S3JobExecution.Direction.PUT);
            S3JobExecutionDao s3JobExecutionDao = new S3JobExecutionDao(_dbTestEnvironment.getPersistenceService());
            s3JobExecutionDao.makePersistent(s3JobExecution);
            _dbTestEnvironment.flipTransaction();

            // try to delete the media
            m1 = _dbTestEnvironment.reload(m1);
            _mediaDao.makeTransient(m1);
            _dbTestEnvironment.flipTransaction();

            // expected: no exception will be thrown
        } catch (Exception e) {
            _dbTestEnvironment.getPersistenceService().rollbackTransaction();
            System.out.println(e);
            throw e;
        }
    }

}
