package org.palladiosimulator.blockchainsystems.plugin.ui.results;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ResultsRepository {

    public final File directory;
    public final ResultsRepositoryContainer container;

    private final FileFilter _textFileFilter =
            f -> f.isFile() && f.getName().endsWith(".dssimresult");

    public ResultsRepository(File directory, ResultsRepositoryContainer container) {
        this.directory = directory;
        this.container = container;
    }

    public List<SimulationResultFile> getSimulationResultFiles() {
        if (directory == null) return new ArrayList<>();
        File[] files = directory.listFiles(_textFileFilter);
        if (files == null) return new ArrayList<>();
        List<SimulationResultFile> result = new ArrayList<>();
        for (File f : files) result.add(new SimulationResultFile(f, this));
        return result;
    }
}
