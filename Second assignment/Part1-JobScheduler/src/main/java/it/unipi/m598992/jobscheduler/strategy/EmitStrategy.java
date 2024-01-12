package it.unipi.m598992.jobscheduler.strategy;

import it.unipi.m598992.auxfile.AJob;

import java.util.stream.Stream;

public interface EmitStrategy<K, V> {
    Stream<AJob<K, V>> emit();
}
