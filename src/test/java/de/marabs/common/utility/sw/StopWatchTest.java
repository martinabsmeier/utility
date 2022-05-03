/*
 * Simple implementation of a stop watch.
 * Copyright (C) 2013 Martin Absmeier, IT Consulting Services
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.marabs.common.utility.sw;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test cases of {@link StopWatch} class.
 *
 * @author Martin Absmeier
 */
public class StopWatchTest {

    private StopWatch sw;

    @Before
    public void setUp() {
        this.sw = new StopWatch();
    }

    @After
    public void tearDown() {
        this.sw = null;
    }

    @Test(expected = IllegalStateException.class)
    public void testStartException() {
        sw.start("Task-1");
        sw.start("Task-2");
    }

    @Test(expected = IllegalStateException.class)
    public void testStopException() {
        sw.stop();
    }

    @Test
    public void testStopWatch() {
        try {
            sw.start("Task-1");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            sw.stop();
            sw.start("Task-2");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            sw.stop();
            sw.start("Task-3");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            sw.stop();
            sw.start("Task-4");
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            sw.stop();
            System.out.println(sw.prettyPrint());
        } catch (IllegalStateException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
