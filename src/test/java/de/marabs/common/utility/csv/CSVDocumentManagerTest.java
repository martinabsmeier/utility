/*
 * Test the
 * Copyright (C) 2013 Martin Absmeier, IT Consulting Services
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.marabs.common.utility.csv;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * JUnit test cases of {@link CSVDocumentManagerTest} class.
 *
 * @author Martin Absmeier
 */
public class CSVDocumentManagerTest {

    private String filePath;
    private CSVDocumentManager documentManager;

    @Before
    public void setUp() {
        filePath = System.getProperty("user.dir").concat(File.separator)
            .concat("src").concat(File.separator)
            .concat("test").concat(File.separator)
            .concat("resources").concat(File.separator)
            .concat("postcodes.csv");
        documentManager = CSVDocumentManager.builder().build();
    }

    @After
    public void tearDown() {
        this.filePath = null;
        this.documentManager = null;
    }

    @Test
    public void testReadCSVFile() throws IOException {
        CSVDocument actual = documentManager.readDocument(filePath, true);
        Assert.assertNotNull("We expect an csv document.", actual);
    }
}