/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package it.unipi.m598992.exercise1.listeners;

import it.unipi.m598992.exercise1.events.RestartEvent;
import java.util.EventListener;

/**
 * @author rizze
 */
public interface RestartListener extends EventListener {
    void onRestart(RestartEvent restartEvent);
}
