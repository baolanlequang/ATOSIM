package org.palladiosimulator.blockchainsystems.plugin.logging;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.palladiosimulator.blockchainsystems.core.simulation.logoutputs.abstractions.LogOutputProvider;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput;
import org.palladiosimulator.blockchainsystems.loggers.TraceEventConsoleLogger;
import org.palladiosimulator.blockchainsystems.loggers.TraceEventFileLogger;
import org.palladiosimulator.blockchainsystems.loggers.TraceEventPostgresDbLogger;
import org.palladiosimulator.blockchainsystems.plugin.Attributes;

import java.util.HashSet;
import java.util.Set;

public class LogOutputProviderImpl implements LogOutputProvider {

    private final boolean _useConsoleLogging;
    private final boolean _useFileLogging;
    private final String _fileLoggingDirectoryPath;
    private final boolean _useDatabaseLogging;
    private final String _dbServer;
    private final String _dbPort;
    private final String _dbName;
    private final String _dbUsername;
    private final String _dbPassword;

    public LogOutputProviderImpl(
            boolean useConsoleLogging,
            boolean useFileLogging,
            String fileLoggingDirectoryPath,
            boolean useDatabaseLogging,
            String dbServer,
            String dbPort,
            String dbName,
            String dbUsername,
            String dbPassword
    ) {
        _useConsoleLogging = useConsoleLogging;
        _useFileLogging = useFileLogging;
        _fileLoggingDirectoryPath = fileLoggingDirectoryPath;
        _useDatabaseLogging = useDatabaseLogging;
        _dbServer = dbServer;
        _dbPort = dbPort;
        _dbName = dbName;
        _dbUsername = dbUsername;
        _dbPassword = dbPassword;
    }

    @Override
    public Set<TraceEventLogOutput> getLogOutputs() {
        Set<TraceEventLogOutput> logOutputs = new HashSet<>();
        if (_useConsoleLogging) logOutputs.add(new TraceEventConsoleLogger());
        if (_useFileLogging) logOutputs.add(new TraceEventFileLogger(_fileLoggingDirectoryPath));
        if (_useDatabaseLogging) logOutputs.add(new TraceEventPostgresDbLogger(
                _dbServer, _dbPort, _dbName, _dbUsername, _dbPassword));
        return logOutputs;
    }

    public static LogOutputProviderImpl fromLaunchConfiguration(ILaunchConfiguration configuration)
            throws CoreException {
    	if (configuration == null) {
    		return new LogOutputProviderImpl(
    				false,
    				false,
    	            "",
    	            false,
    	            "",
    	            "",
    	            "",
    	            "",
    	            ""
            );
    	}
        return new LogOutputProviderImpl(
                configuration.getAttribute(
                        Attributes.Logging.IS_CONSOLE_LOGGING_ENABLED_ATTRIBUTE,
                        Attributes.Logging.IS_CONSOLE_LOGGING_ENABLED_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.IS_FILE_LOGGING_ENABLED_ATTRIBUTE,
                        Attributes.Logging.IS_FILE_LOGGING_ENABLED_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.LOG_FILE_PATH_ATTRIBUTE,
                        Attributes.Logging.LOG_FILE_PATH_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.IS_DATABASE_LOGGING_ENABLED_ATTRIBUTE,
                        Attributes.Logging.IS_DATABASE_LOGGING_ENABLED_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.DATABASE_SERVER_ATTRIBUTE,
                        Attributes.Logging.DATABASE_SERVER_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.DATABASE_PORT_ATTRIBUTE,
                        Attributes.Logging.DATABASE_PORT_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.DATABASE_NAME_ATTRIBUTE,
                        Attributes.Logging.DATABASE_NAME_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.DATABASE_USERNAME_ATTRIBUTE,
                        Attributes.Logging.DATABASE_USERNAME_ATTRIBUTE_DEFAULT),
                configuration.getAttribute(
                        Attributes.Logging.DATABASE_PASSWORD_ATTRIBUTE,
                        Attributes.Logging.DATABASE_PASSWORD_ATTRIBUTE_DEFAULT)
        );
    }
}
