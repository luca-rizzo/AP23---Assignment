/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package it.unipi.m598992.exercise1.javabeans;

import it.unipi.m598992.exercise1.events.PosLabelValue;
import it.unipi.m598992.exercise1.events.RestartEvent;
import it.unipi.m598992.exercise1.listeners.RestartListener;

import javax.swing.*;
import java.awt.*;
import java.beans.*;

import static it.unipi.m598992.exercise1.Constant.HOLE_LABEL;

/**
 * @author Luca Rizzo
 */
public class EightTile extends JButton implements RestartListener, PropertyChangeListener {

    private static final int FLASH_DURATION = 500;
    private static final Color FLASH_COLOR = Color.RED;
    private static final Color NINE_TILE_COLOR = Color.GRAY;
    private static final Color CORRECT_POSITION_COLOR = Color.GREEN;
    private static final Color INCORRECT_POSITION_COLOR = Color.YELLOW;
    private int position;
    private int label;
    private final VetoableChangeSupport vetoChangeSupport = new VetoableChangeSupport(this);
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public EightTile() {
    }

    public EightTile(int position) {
        this.position = position;
        addActionListener(e -> setLabel(HOLE_LABEL));
    }

    public synchronized void addVetoableChangeListener(String propertyName, VetoableChangeListener l) {
        vetoChangeSupport.addVetoableChangeListener(propertyName, l);
    }

    public synchronized void removeVetoableChangeListener(String propertyName, VetoableChangeListener l) {
        vetoChangeSupport.removeVetoableChangeListener(propertyName, l);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(propertyName, l);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(propertyName, l);
    }

    public void setLabel(int newLabel) {
        setLabelWithVeto(newLabel);
    }

    private void setLabelWithVeto(int newLabel) {
        try {
            PosLabelValue previousPosLabelValue = new PosLabelValue(position, label);
            PosLabelValue newPosLabelValue = new PosLabelValue(position, newLabel);
            vetoChangeSupport.fireVetoableChange("label", previousPosLabelValue, newPosLabelValue);
            this.label = newLabel;
            changeSupport.firePropertyChange("label", previousPosLabelValue, newPosLabelValue);
            updateAppearance();
        } catch (PropertyVetoException e) {
            flashTile();
        }

    }

    private void setLabelWithoutVeto(int newLabel) {
        PosLabelValue previousPosLabelValue = new PosLabelValue(position, label);
        PosLabelValue newPosLabelValue = new PosLabelValue(position, newLabel);
        this.label = newLabel;
        changeSupport.firePropertyChange("label", previousPosLabelValue, newPosLabelValue);
        updateAppearance();
    }

    private void updateAppearance() {
        updateText();
        updateBackground();
    }

    private void updateText() {
        String newText = label == HOLE_LABEL ? "" : String.valueOf(label);
        setText(newText);
    }

    private void updateBackground() {
        if (label == HOLE_LABEL) {
            setBackground(NINE_TILE_COLOR);
        } else if (label == position) {
            setBackground(CORRECT_POSITION_COLOR);
        } else {
            setBackground(INCORRECT_POSITION_COLOR);
        }
    }

    private void flashTile() {
        Color originalColor = getBackground();
        setBackground(FLASH_COLOR);
        Timer timer = new Timer(FLASH_DURATION, event -> setBackground(originalColor));
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void onRestart(RestartEvent restartEvent) {
        Integer[] permutation = restartEvent.getPermutation();
        setLabelWithoutVeto(permutation[position - 1]);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PosLabelValue oldValue = (PosLabelValue) evt.getOldValue();
        PosLabelValue newValue = (PosLabelValue) evt.getNewValue();
        if (newValue.label() == HOLE_LABEL && this.label == HOLE_LABEL) {
            setLabelWithoutVeto(oldValue.label());
        }
    }
}