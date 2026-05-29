package org.palladiosimulator.blockchainsystems.core.simulation.abstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class MonteCarloSimulation<R extends SimulationRoundResult> implements Simulation {

    protected final int numberOfRounds;
    protected final MonteCarloSimulationProgressMonitor progressMonitor;

    public MonteCarloSimulation(int numberOfRounds, MonteCarloSimulationProgressMonitor progressMonitor) {
        this.numberOfRounds = numberOfRounds;
        this.progressMonitor = progressMonitor;
    }

    @Override
    public MonteCarloSimulationResult run() {
        progressMonitor.onSimulationStarted(numberOfRounds);

        ExecutorService executor = Executors.newWorkStealingPool();
        List<Callable<R>> tasks = new ArrayList<>();
        for (int i = 0; i < numberOfRounds; i++) {
            tasks.add(() -> {
                R result = performSimulationRound();
                progressMonitor.onSimulationRoundFinished();
                return result;
            });
        }

        List<R> results = new ArrayList<>();
        try {
            List<Future<R>> futures = executor.invokeAll(tasks);
            for (Future<R> future : futures) {
                results.add(future.get());
            }
        } catch (Exception e) {
            throw new RuntimeException("Monte Carlo simulation failed", e);
        } finally {
            executor.shutdown();
        }

        progressMonitor.onSimulationFinished();
        return createSimulationResultFromRoundResults(results);
    }

    public abstract R performSimulationRound();
    public abstract MonteCarloSimulationResult createSimulationResultFromRoundResults(List<R> results);
}
