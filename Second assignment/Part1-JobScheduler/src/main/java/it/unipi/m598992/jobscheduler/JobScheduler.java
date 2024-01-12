package it.unipi.m598992.jobscheduler;

import it.unipi.m598992.auxfile.AJob;
import it.unipi.m598992.auxfile.Pair;
import it.unipi.m598992.jobscheduler.strategy.EmitStrategy;
import it.unipi.m598992.jobscheduler.strategy.OutputStrategy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class serves as a generic job scheduler framework, implementing the Strategy design pattern
 * for job emission and stream consumption after the collect phase.
 */
// Using a final class because extending behavior is achieved through composition
// by injecting specific strategies, rather than relying on inheritance
public final class JobScheduler<K, V> {
    private final EmitStrategy<K, V> emitStrategy;
    private final OutputStrategy<K, V> outputStrategy;

    /**
     * This constructor follows the Dependency Injection pattern to provide strategies for the runSteps() method.
     *
     * @param emitStrategy   The strategy for job emission (hot spot).
     * @param outputStrategy The strategy for output handling (hot spot).
     */
    public JobScheduler(EmitStrategy<K, V> emitStrategy, OutputStrategy<K, V> outputStrategy) {
        this.emitStrategy = emitStrategy;
        this.outputStrategy = outputStrategy;
    }


    /**
     * Executes the framework steps, where the output of each step is used as input for the next (composition).
     * Uses the injected strategy as the hot spot.
     */
    public void runSteps() {
        // Use the strategy to retrieve the stream of jobs (Hot spot).
        Stream<AJob<K, V>> emitOutput = emitStrategy.emit();
        // Compute in a uniquely shared way across all instances of the framework (Frozen spot)
        Stream<Pair<K, V>> computeOutput = compute(emitOutput);
        // Collect in a uniquely shared way across all instances of the framework (Frozen spot)
        Stream<Pair<K, List<V>>> collectOutput = collect(computeOutput);
        // Use the strategy to consume the stream of pairs (Hot spot)
        outputStrategy.output(collectOutput);
        System.out.printf("%s: all steps have been performed without error",
                this.getClass().getSimpleName());
    }

    private Stream<Pair<K, V>> compute(Stream<AJob<K, V>> emitOutput) {
        // Executes each job and flattens their streams into a single stream.
        // flatMap is used to transform Stream<Stream<Pair<K, V>>> into a single Stream<Pair<K, V>>.
        return emitOutput.flatMap(AJob::execute);
    }

    private Stream<Pair<K, List<V>>> collect(Stream<Pair<K, V>> computeOutput) {
        // Group key/value pairs by key and collect values grouped into a list
        Map<K, List<V>> groupedByKey = computeOutput.collect(
                Collectors.groupingBy(Pair::getKey,
                        Collectors.mapping(Pair::getValue, Collectors.toList())));
        // Transform the grouped entries back into a stream of Pair objects
        return groupedByKey.entrySet().stream().map(entry ->
                new Pair<>(entry.getKey(), entry.getValue()));
    }
}
