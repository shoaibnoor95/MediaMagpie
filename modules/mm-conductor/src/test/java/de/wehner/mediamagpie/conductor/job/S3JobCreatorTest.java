package de.wehner.mediamagpie.conductor.job;

import static org.fest.assertions.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.wehner.mediamagpie.persistence.TransactionHandlerMock;
import de.wehner.mediamagpie.persistence.dao.MediaDao;
import de.wehner.mediamagpie.persistence.dao.TransactionHandler;
import de.wehner.mediamagpie.persistence.entity.S3JobExecution;

public class S3JobCreatorTest {

    @Mock
    private MediaDao _mediaDao;
    
    private S3JobCreator _s3JobCreator;
    
    private TransactionHandler _transactionHandlerMock = new TransactionHandlerMock();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        _s3JobCreator = new S3JobCreator(null, _mediaDao, _transactionHandlerMock);
    }

    @Test
    public void test_createInTransaction_ButMediaWasDeletedBefore() {
        S3JobExecution s3JobExecution = new S3JobExecution();
        assertThat(_s3JobCreator.create(s3JobExecution)).isNull();

    }
}
