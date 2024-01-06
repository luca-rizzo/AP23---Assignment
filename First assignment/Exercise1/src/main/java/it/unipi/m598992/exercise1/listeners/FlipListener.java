package it.unipi.m598992.exercise1.listeners;

import it.unipi.m598992.exercise1.events.FlipEvent;
import it.unipi.m598992.exercise1.events.FlipStatusEvent;

import java.util.EventListener;

public interface FlipListener extends EventListener  {
    void onFlipUpdate(FlipStatusEvent flipStatusEvent);
    void onFlip(FlipEvent flipEvent);
}
