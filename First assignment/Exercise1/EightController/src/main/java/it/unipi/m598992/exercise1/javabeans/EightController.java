package it.unipi.m598992.exercise1.javabeans;


import it.unipi.m598992.exercise1.events.FlipEvent;
import it.unipi.m598992.exercise1.events.FlipStatusEvent;
import it.unipi.m598992.exercise1.events.PosLabelValue;
import it.unipi.m598992.exercise1.events.RestartEvent;
import it.unipi.m598992.exercise1.listeners.FlipListener;
import it.unipi.m598992.exercise1.listeners.RestartListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.*;
import java.util.stream.IntStream;

import static it.unipi.m598992.exercise1.Constant.HOLE_LABEL;

/**
 * EightController class represents the controller for the Eight Puzzle game.
 * This controller manages the game logic, ensuring that only legal moves are allowed and handling flip mechanism.
 */
public class EightController extends JLabel implements VetoableChangeListener, PropertyChangeListener,
        RestartListener, ActionListener {

    private final Map<Integer, List<Integer>> neighborsTilesForTiles;

    private final List<FlipListener> flipListeners;
    private List<TileStatus> tilesStatus;

    public EightController() {
        flipListeners = new ArrayList<>();
        neighborsTilesForTiles = new HashMap<>();
        // registers all neighbors for each tile to facilitate the check for allowed moves
        neighborsTilesForTiles.put(1, Arrays.asList(2, 4));
        neighborsTilesForTiles.put(2, Arrays.asList(1, 3, 5));
        neighborsTilesForTiles.put(3, Arrays.asList(2, 6));
        neighborsTilesForTiles.put(4, Arrays.asList(1, 5, 7));
        neighborsTilesForTiles.put(5, Arrays.asList(2, 4, 6, 8));
        neighborsTilesForTiles.put(6, Arrays.asList(3, 5, 9));
        neighborsTilesForTiles.put(7, Arrays.asList(4, 8));
        neighborsTilesForTiles.put(8, Arrays.asList(5, 7, 9));
        neighborsTilesForTiles.put(9, Arrays.asList(6, 8));
    }

    /**
     * Callback method that implements the logic to prevent a change if it is not allowed following the rules of the game.
     */
    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (!isChangeAllowed(evt)) {
            // updates current UI and throws Exception
            vetoChange(evt);
        }
    }

    /**
     * Callback method that implements the logic to update the internal representation when a restart event occurs
     */
    @Override
    public void onRestart(RestartEvent restartEvent) {
        Integer[] permutation = restartEvent.getPermutation();
        //update tilesStatus with new permutation
        tilesStatus = mapPermutationToStatus(permutation);
    }

    /**
     * Handles a PropertyChangeEvent, updating the internal game state and notifying all flip status listeners.
     *
     * @param evt PropertyChangeEvent representing the change in the position and label of a tile.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        changeInternalStatusGame(evt);
        notifyAllFlipStatusListener();
        setText("OK");
    }

    /**
     * Adds a flip listener to the list of listeners.
     *
     * @param l The flip listener to be added.
     */
    public synchronized void addFlipListener(FlipListener l) {
        flipListeners.add(l);
    }

    /**
     * Removes a flip listener from the list of listeners.
     *
     * @param l The flip listener to be removed.
     */
    public synchronized void removeFlipListener(FlipListener l) {
        flipListeners.remove(l);
    }

    /**
     * Callback for the click of flip button. It checks if it is possible and, if so, notifies all listeners
     * of the flip event with the flipped permutation.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFlipPossible()) {
            Integer[] flippedPermutation = getFlippedPermutation();
            FlipEvent flipEvent = new FlipEvent(flippedPermutation);
            notifyAllFlipListener(flipEvent);
        }
    }

    //Checks whether a change is allowed by examining the neighbors of the tile undergoing the change.
    private boolean isChangeAllowed(PropertyChangeEvent evt) {
        TileStatus changingTile = retrieveTileChanging(evt).orElseThrow(IllegalStateException::new);
        List<Integer> changingTileNeighbors = neighborsTilesForTiles.get(changingTile.getIdPos());
        // A change is allowed if a neighbor is the hole.
        return checkIfNeighborsContainHole(changingTileNeighbors);
    }

    private boolean checkIfNeighborsContainHole(List<Integer> changingTileNeighbors) {
        // Allows the change only if the label of one neighbor is HOLE_LABEL in the current status
        return changingTileNeighbors.stream()
                .map(neighborPos -> tilesStatus.get(neighborPos - 1))
                .anyMatch(tileStatus -> tileStatus.getLabel() == HOLE_LABEL);
    }

    private Optional<TileStatus> retrieveTileChanging(PropertyChangeEvent evt) {
        PosLabelValue oldValue = (PosLabelValue) evt.getOldValue();
        // Find the tile that is attempting to change based on its position and current status
        return tilesStatus.stream().filter(tile -> tile.idPos == oldValue.pos()).findFirst();
    }

    private void vetoChange(PropertyChangeEvent evt) throws PropertyVetoException {
        setText("KO");
        throw new PropertyVetoException("nope", evt);
    }

    private List<TileStatus> mapPermutationToStatus(Integer[] permutation) {
        //create the internal representation
        return IntStream.range(0, permutation.length)
                .mapToObj(i -> new TileStatus(i + 1, permutation[i]))
                .toList();
    }

    private void changeInternalStatusGame(PropertyChangeEvent evt) {
        TileStatus changingTile = retrieveTileChanging(evt).orElseThrow(IllegalStateException::new);
        PosLabelValue newValue = (PosLabelValue) evt.getNewValue();
        //change the label of tile in status with new value
        changingTile.label = newValue.label();
    }

    //notifies all flip status listeners if the hole is in tile 9, indicating that a flip is possible.
    private synchronized void notifyAllFlipStatusListener() {
        FlipStatusEvent flipStatusEvent = new FlipStatusEvent(isFlipPossible());
        flipListeners.stream().forEach(l -> l.onFlipUpdate(flipStatusEvent));
    }

    //Flip is possible only is the hole is in position 9
    private boolean isFlipPossible() {
        return tilesStatus.get(8).label == HOLE_LABEL;
    }

    private synchronized void notifyAllFlipListener(FlipEvent flipEvent) {
        flipListeners.forEach(l -> l.onFlip(flipEvent));
    }

    private Integer[] getFlippedPermutation() {
        //Creates a flipped permutation by swapping the first two elements of the current tile status
        Integer[] flippedPermutation = new Integer[9];
        this.tilesStatus.stream().map(tile -> tile.label).toList().toArray(flippedPermutation);
        Integer temp = flippedPermutation[0];
        flippedPermutation[0] = flippedPermutation[1];
        flippedPermutation[1] = temp;
        return flippedPermutation;
    }

    private static class TileStatus {
        private final int idPos;
        private int label;

        public TileStatus(int idPos, int label) {
            this.idPos = idPos;
            this.label = label;
        }

        public int getIdPos() {
            return idPos;
        }

        public int getLabel() {
            return label;
        }

        public void setLabel(int label) {
            this.label = label;
        }
    }
}


