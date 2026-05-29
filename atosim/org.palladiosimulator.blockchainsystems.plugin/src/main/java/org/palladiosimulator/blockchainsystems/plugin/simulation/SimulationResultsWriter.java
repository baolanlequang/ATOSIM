package org.palladiosimulator.blockchainsystems.plugin.simulation;

import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResult;
import org.palladiosimulator.blockchainsystems.core.simulation.abstractions.SimulationResultSerializer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SimulationResultsWriter {

    private final SimulationResultSerializer _simulationResultSerializer;

    public SimulationResultsWriter(SimulationResultSerializer simulationResultSerializer) {
        _simulationResultSerializer = simulationResultSerializer;
    }

    public void saveResultFile(SimulationResult result, String outDir) {
        String timestamp = getCurrentTimeFormatted();
        String fileName = timestamp + ".tsr.json";
        String fullFilePath = Path.of(outDir, fileName).toString();

        String serializedResult = _simulationResultSerializer.serialize(result);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fullFilePath))) {
            writer.write(serializedResult);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file " + fullFilePath + ".");
            e.printStackTrace();
        }
    }

    private String getCurrentTimeFormatted() {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        return DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss").format(zonedDateTime);
    }
}
