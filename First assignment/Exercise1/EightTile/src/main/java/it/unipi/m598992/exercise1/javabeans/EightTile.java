
package it.unipi.m598992.exercise1.javabeans;

import it.unipi.m598992.exercise1.events.PosLabelValue;
import it.unipi.m598992.exercise1.events.RestartEvent;
import it.unipi.m598992.exercise1.listeners.RestartListener;

import javax.swing.*;
import java.awt.*;
import java.beans.*;

import static it.unipi.m598992.exercise1.Constant.HOLE_LABEL;

/**
 * EightTile class represents a tile in the Eight Puzzle game.
 * It manages the visual representation of the tile, handles changes during restart events, and synchronizes with
 * other tiles when their values change.
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
        //auto-register to the click of the button
        addActionListener(e -> setLabel(HOLE_LABEL));
    }

    /**
     * Handles a restart event by updating the tile's label based on the new permutation.
     *
     * @param restartEvent The RestartEvent containing the new permutation.
     */
    @Override
    public void onRestart(RestartEvent restartEvent) {
        Integer[] permutation = restartEvent.getPermutation();
        setLabelWithoutVeto(permutation[position - 1]);
    }

    /**
     * Handles a property change event of another tile.
     * If the tile is the hole and another tile is changing its value to the hole, then the tile should capture the old value and update its label with it.
     *
     * @param evt PropertyChangeEvent representing the change in the position and label of a tile.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        PosLabelValue oldValue = (PosLabelValue) evt.getOldValue();
        PosLabelValue newValue = (PosLabelValue) evt.getNewValue();
        //if the tile's label is the hole and another tile is changing its value to the hole
        if (newValue.label() == HOLE_LABEL && this.label == HOLE_LABEL) {
            //update label with oldValue label
            setLabelWithoutVeto(oldValue.label());
        }
    }

    /**
     * Adds a vetoable change listener for the specified property. Register to this event if you want to prevent
     * changes to a property.
     *
     * @param propertyName The name of the property to listen for changes.
     * @param l            The VetoableChangeListener to be added.
     */
    public synchronized void addVetoableChangeListener(String propertyName, VetoableChangeListener l) {
        vetoChangeSupport.addVetoableChangeListener(propertyName, l);
    }

    /**
     * Removes a vetoable change listener for the specified property.
     *
     * @param propertyName The name of the property for which to remove the listener.
     * @param l            The VetoableChangeListener to be removed.
     */
    public synchronized void removeVetoableChangeListener(String propertyName, VetoableChangeListener l) {
        vetoChangeSupport.removeVetoableChangeListener(propertyName, l);
    }

    /**
     * Adds a property change listener for the specified property. Register to this event if you only want
     * to be notified when a property changes
     *
     * @param propertyName The name of the property to listen for changes.
     * @param l            The PropertyChangeListener to be added.
     */
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
        changeSupport.addPropertyChangeListener(propertyName, l);
    }

    /**
     * Removes a property change listener for the specified property.
     *
     * @param propertyName The name of the property for which to remove the listener.
     * @param l            The PropertyChangeListener to be removed.
     */
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
        changeSupport.removePropertyChangeListener(propertyName, l);
    }

    /**
     * Sets the label of the tile, allowing registered vetoableChangeListener to prevent the changes.
     *
     * @param newLabel The new label to set.
     */
    public void setLabel(int newLabel) {
        setLabelWithVeto(newLabel);
    }

    private void setLabelWithVeto(int newLabel) {
        try {
            PosLabelValue previousPosLabelValue = new PosLabelValue(position, label);
            PosLabelValue newPosLabelValue = new PosLabelValue(position, newLabel);
            // first, notify all listeners that could prevent changes
            vetoChangeSupport.fireVetoableChange("label", previousPosLabelValue, newPosLabelValue);
            // if no exception is thrown, change the value and notify all PropertyChangeListeners
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
        // only notify propertyChangeListeners because the vetoableChangeListener could prevent
        // this "special case" change erroneously.
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
        // save the original background color
        Color originalColor = getBackground();
        // set the background to red
        setBackground(FLASH_COLOR);
        // after FLASH_DURATION milliseconds restore original color
        Timer timer = new Timer(FLASH_DURATION, event -> setBackground(originalColor));
        timer.setRepeats(false);
        timer.start();
    }

}