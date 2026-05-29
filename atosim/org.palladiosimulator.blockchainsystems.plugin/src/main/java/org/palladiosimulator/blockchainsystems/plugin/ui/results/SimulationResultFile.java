package org.palladiosimulator.blockchainsystems.plugin.ui.results;

import org.palladiosimulator.blockchainsystems.plugin.json.JsonValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SimulationResultFile {

    public final File file;
    public final ResultsRepository repository;

    private JsonValue _jsonContent;
    private boolean _jsonContentLoaded = false;

    public SimulationResultFile(File file, ResultsRepository repository) {
        this.file = file;
        this.repository = repository;
    }

    public JsonValue getJsonContent() {
        if (!_jsonContentLoaded) {
            _jsonContentLoaded = true;
            if (file == null) return null;
            try {
                String content = Files.readString(file.toPath());
                _jsonContent = JsonValue.parse(content);
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return _jsonContent;
    }
}
