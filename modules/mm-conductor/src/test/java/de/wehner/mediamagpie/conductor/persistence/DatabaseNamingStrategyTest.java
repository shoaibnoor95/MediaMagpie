package de.wehner.mediamagpie.conductor.persistence;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.wehner.mediamagpie.conductor.persistence.DatabaseNamingStrategy;

public class DatabaseNamingStrategyTest {

    private DatabaseNamingStrategy _databaseNamingStrategy;

    @Before
    public void setUp() {
        _databaseNamingStrategy = new DatabaseNamingStrategy();
    }

    @Test
    public void testClassToTableNameString() {
        assertEquals("data_source", _databaseNamingStrategy.classToTableName("DataSource"));
        assertEquals("urlencoder", _databaseNamingStrategy.classToTableName("URLEncoder"));
    }

    @Test
    public void testColumnNameString() {
        assertEquals("data_source", _databaseNamingStrategy.columnName("_dataSource"));
        assertEquals("data_source", _databaseNamingStrategy.columnName("dataSource"));
    }

    @Test
    public void testPropertyToColumnNameString() {
        assertEquals("data_source", _databaseNamingStrategy.propertyToColumnName("_dataSource"));
        assertEquals("data_source", _databaseNamingStrategy.propertyToColumnName("dataSource"));
    }
}
