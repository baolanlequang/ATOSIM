package org.palladiosimulator.blockchainsystems.core.simulation.abstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MonteCarloSimulation<R extends SimulationRoundResult> implements Simulation {

    protected final int numberOfRounds;
    protected final int parallelism;
    protected final MonteCarloSimulationProgressMonitor progressMonitor;

    /**
     * @param parallelism Maximum number of rounds executed concurrently. {@code 0} or less
     *                     falls back to {@link ForkJoinPool#commonPool()}.
     */
    public MonteCarloSimulation(int numberOfRounds, int parallelism, MonteCarloSimulationProgressMonitor progressMonitor) {
        this.numberOfRounds = numberOfRounds;
        this.parallelism = parallelism;
        this.progressMonitor = progressMonitor;
    }

    @Override
    public MonteCarloSimulationResult run() {
        progressMonitor.onSimulationStarted(numberOfRounds);

        AtomicInteger roundCounter = new AtomicInteger(0);
        ForkJoinPool executor = parallelism > 0 ? new ForkJoinPool(parallelism) : ForkJoinPool.commonPool();
        List<Callable<R>> tasks = new ArrayList<>();
        for (int i = 0; i < numberOfRounds; i++) {
            tasks.add(() -> {
            	R result = performSimulationRound();
                int round = roundCounter.incrementAndGet();
                System.out.println("Monte Carlo round " + round + "/" + numberOfRounds + " finished");
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
            if (parallelism > 0) {
                executor.shutdown();
            }
        }

        progressMonitor.onSimulationFinished();
        return createSimulationResultFromRoundResults(results);
    }

    public abstract R performSimulationRound();
    public abstract MonteCarloSimulationResult createSimulationResultFromRoundResults(List<R> results);
}
