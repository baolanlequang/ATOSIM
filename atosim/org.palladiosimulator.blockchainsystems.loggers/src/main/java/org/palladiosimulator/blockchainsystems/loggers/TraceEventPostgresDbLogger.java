package org.palladiosimulator.blockchainsystems.loggers;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.core.tracing.TraceEventLogOutput;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

public class TraceEventPostgresDbLogger implements TraceEventLogOutput {

    private static final String TEMP_LOG_FILE_HEADER = "Id,Node,Timestamp,EventType" + System.lineSeparator();

    private final String _server;
    private final String _port;
    private final String _database;
    private final String _databaseUser;
    private final String _databasePassword;

    private BufferedWriter _tempLogFileWriter;
    private Path _logFilePath;

    public TraceEventPostgresDbLogger(
            String server,
            String port,
            String database,
            String databaseUser,
            String databasePassword
    ) {
        _server = server;
        _port = port;
        _database = database;
        _databaseUser = databaseUser;
        _databasePassword = databasePassword;
    }

    @Override
    public void initialize() {
        _logFilePath = getTempLogFullPath(UUID.randomUUID());

        try {
            _tempLogFileWriter = new BufferedWriter(new FileWriter(_logFilePath.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            _tempLogFileWriter.append(TEMP_LOG_FILE_HEADER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanUp() {
        try {
            _tempLogFileWriter.flush();
            _tempLogFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        copyToDatabase();

        try {
            Files.deleteIfExists(_logFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyToDatabase() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://" + _server + ":" + _port + "/" + _database,
                    _databaseUser,
                    _databasePassword
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        CopyManager copyManager = null;
        try {
            copyManager = new CopyManager((BaseConnection) conn);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(_logFilePath.toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (copyManager != null && reader != null) {
            try {
                copyManager.copyIn(
                        "COPY public.\"TraceEvents\" FROM STDIN DELIMITER ',' CSV HEADER;",
                        reader
                );
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (reader != null) reader.close();
            conn.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTraceEventOccurred(TraceEvent event, TraceEventLogOrigin logOrigin) {
        try {
            _tempLogFileWriter.append(UUID.randomUUID().toString());
            _tempLogFileWriter.append(",");
            _tempLogFileWriter.append(logOrigin.getName());
            _tempLogFileWriter.append(",");
            _tempLogFileWriter.append(Long.toString(event.getOccurrenceTime()));
            _tempLogFileWriter.append(",");
            _tempLogFileWriter.append(event.getEventType());
            _tempLogFileWriter.append(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getTempLogFullPath(UUID simulationId) {
        Path currentFileDirectory = FileSystems.getDefault().getPath("").toAbsolutePath();
        return Paths.get(currentFileDirectory.toString(), "temp-simulation-log-" + simulationId + ".csv");
    }
}
