package it.unipi.m598992.jobscheduler.strategy;

import it.unipi.m598992.auxfile.Pair;

import java.util.List;
import java.util.stream.Stream;

public interface OutputStrategy<K, V> {
    void output(Stream<Pair<K, List<V>>> collectOutput);
}
