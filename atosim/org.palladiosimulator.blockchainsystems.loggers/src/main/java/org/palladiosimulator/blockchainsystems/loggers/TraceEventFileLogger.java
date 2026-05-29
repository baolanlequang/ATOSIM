package org.palladiosimulator.blockchainsystems.loggers;

import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEvent;
import org.palladiosimulator.blockchainsystems.core.common.abstractions.TraceEventLogOrigin;
import org.palladiosimulator.blockchainsystems.loggers.abstractions.AbstractJsonLogger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class TraceEventFileLogger extends AbstractJsonLogger {

    private final String _filePath;
    private BufferedWriter _outFileWriter;

    public TraceEventFileLogger(String filePath) {
        _filePath = filePath;
    }

    @Override
    public void initialize() {
        Path p = Path.of(_filePath, UUID.randomUUID() + ".jsonl");
        try {
            _outFileWriter = new BufferedWriter(new FileWriter(p.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTraceEventOccurred(TraceEvent traceEvent, TraceEventLogOrigin logOrigin) {
        try {
            _outFileWriter.write(getEventFormat(traceEvent, logOrigin) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            _outFileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            _outFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanUp() {
        flush();
        close();
    }
}
