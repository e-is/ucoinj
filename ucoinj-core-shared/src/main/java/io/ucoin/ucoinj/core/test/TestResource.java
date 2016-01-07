package io.ucoin.ucoinj.core.test;

/*
 * #%L
 * UCoin Java Client :: Core API
 * %%
 * Copyright (C) 2014 - 2015 EIS
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.google.common.base.Preconditions;
import io.ucoin.ucoinj.core.util.FileUtils;
import io.ucoin.ucoinj.core.util.StringUtils;
import org.abstractj.kalium.NaCl;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class TestResource implements TestRule {

    private static final Logger log = LoggerFactory.getLogger(TestResource.class);
    public static long BUILD_TIMESTAMP = System.nanoTime();

    public static TestResource create() {
        return new TestResource(null);
    }
    
    public static TestResource create(String configName) {
        return new TestResource(configName);
    }

    private TestFixtures fixtures = new TestFixtures();
    private File resourceDirectory;
    private String configName;
    private String configFileName;
    private boolean witherror = false;
    protected Class<?> testClass;
    
    protected TestResource(String configName) {
        this.configName = configName;
    }
    
    public TestFixtures getFixtures() {
        return fixtures;
    }
    
    public File getResourceDirectory(String name) {
        return new File(resourceDirectory, name);
    }
    
    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before(description);
                try {
                    base.evaluate();
                } catch (Throwable e) {
                    witherror = true;
                } finally {
                    after(description);
                }
            }
        };
    }

    protected void before(Description description) throws Throwable {
        testClass = description.getTestClass();
        
        NaCl.init();

        boolean defaultDbName = StringUtils.isEmpty(configName);

        if (log.isInfoEnabled()) {
            log.info("Prepare test " + testClass);
        }

        resourceDirectory = getTestSpecificDirectory(testClass, "");

        // check that config file is in classpath (avoid to find out why it does not works...)
        this.configFileName = getConfigFilesPrefix();
        
        if (!defaultDbName) {
            this.configFileName += "-" + configName;
        }
        this.configFileName += ".properties";

        InputStream resourceAsStream = getClass().getResourceAsStream("/" + this.configFileName);
        Preconditions.checkNotNull(resourceAsStream, "Could not find " + this.configFileName + " in test class-path");
        resourceAsStream.close();
    }

    protected void after(Description description) throws Throwable {
    }

    protected String getConfigFileName() {
        return this.configFileName;
    }

    protected File getResourceDirectory() {
        return this.resourceDirectory;
    }
    /**
     * Return configuration files prefix (i.e. 'allegro-test')
     * Could be override by external project
     * 
     * @return the prefix to use to retrieve configuration files
     */
    protected String getConfigFilesPrefix() {
        return "ucoinj-test";
    }
        protected File getTestSpecificDirectory(Class<?> testClass,
            String name) throws IOException {
        // Trying to look for the temporary folder to store data for the test
        String tempDirPath = System.getProperty("java.io.tmpdir");
        if (tempDirPath == null) {
            // can this really occur ?
            tempDirPath = "";
            if (log.isWarnEnabled()) {
                log.warn("'\"java.io.tmpdir\" not defined");
            }
        }
        File tempDirFile = new File(tempDirPath);

        // create the directory to store database data
        String dataBasePath = testClass.getName()
                + File.separator // a directory with the test class name
                + name // a sub-directory with the method name
                + '_'
                + BUILD_TIMESTAMP; // and a timestamp
        File databaseFile = new File(tempDirFile, dataBasePath);
        FileUtils.forceMkdir(databaseFile);

        return databaseFile;
    }
}
