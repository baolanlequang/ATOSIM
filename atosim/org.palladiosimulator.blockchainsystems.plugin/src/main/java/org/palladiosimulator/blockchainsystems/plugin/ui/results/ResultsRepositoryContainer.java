package org.palladiosimulator.blockchainsystems.plugin.ui.results;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ResultsRepositoryContainer {

    public final List<ResultsRepository> resultRepositories = new ArrayList<>();

    public void addRepository(File repositoryPath) {
        resultRepositories.add(new ResultsRepository(repositoryPath, this));
    }
}
