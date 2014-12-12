package io.ucoin.client.core;

import io.ucoin.client.core.config.Configuration;
import io.ucoin.client.core.config.ConfigurationOption;
import io.ucoin.client.core.service.ServiceLocator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.nuiton.i18n.I18n;
import org.nuiton.i18n.init.DefaultI18nInitializer;
import org.nuiton.i18n.init.UserI18nInitializer;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class TestResource implements TestRule {

    private static final Log log = LogFactory.getLog(TestResource.class);
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

        boolean defaultDbName = StringUtils.isEmpty(configName);

        if (log.isInfoEnabled()) {
            log.info("Prepare test " + testClass);
        }

        resourceDirectory = getTestSpecificDirectory(testClass, "");

        // check that config file is in classpath (avoid to find out why it does not works...)
        String configFilename = getConfigFilesPrefix();
        
        if (!defaultDbName) {
            configFilename += "-" + configName;
        }
        configFilename += ".properties";

        InputStream resourceAsStream = getClass().getResourceAsStream("/" + configFilename);
        Preconditions.checkNotNull(resourceAsStream, "Could not find " + configFilename + " in test class-path");

        // Initialize configuration
        initConfiguration(configFilename);

        // Init i18n
        initI18n();

        // Initialize service locator    
        ServiceLocator.instance().init();
    }

    protected void after(Description description) throws Throwable {
    }

    
    /**
     * Convenience methods that could be override to initialize other configuration
     * 
     * @param configFilename
     * @param configArgs
     */
    protected void initConfiguration(String configFilename) {
        String[] configArgs = getConfigArgs();
        Configuration config = new Configuration(configFilename, configArgs);
        Configuration.setInstance(config);
    }

    protected void initI18n() throws IOException {
        Configuration config = Configuration.instance();

        // --------------------------------------------------------------------//
        // init i18n
        // --------------------------------------------------------------------//
        File i18nDirectory = new File(config.getDataDirectory(), "i18n");
        if (i18nDirectory.exists()) {
            // clean i18n cache
            FileUtils.cleanDirectory(i18nDirectory);
        }

        FileUtils.forceMkdir(i18nDirectory);

        if (log.isDebugEnabled()) {
            log.debug("I18N directory: " + i18nDirectory);
        }

        Locale i18nLocale = config.getI18nLocale();

        if (log.isInfoEnabled()) {
            log.info(String.format("Starts i18n with locale [%s] at [%s]",
                    i18nLocale, i18nDirectory));
        }
        I18n.init(new UserI18nInitializer(
                i18nDirectory, new DefaultI18nInitializer(getI18nBundleName())),
                i18nLocale);
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
    
    protected String getI18nBundleName() {
        return "ucoinj-core-i18n";
    }

    protected String[] getConfigArgs() {
        List<String> configArgs = Lists.newArrayList();
        configArgs.addAll(Lists.newArrayList(
                "--option", ConfigurationOption.BASEDIR.getKey(), resourceDirectory.getAbsolutePath()));
         return configArgs.toArray(new String[configArgs.size()]);
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
