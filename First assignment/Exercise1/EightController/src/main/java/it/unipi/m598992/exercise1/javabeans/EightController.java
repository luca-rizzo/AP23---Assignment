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

public class EightController extends JLabel implements VetoableChangeListener, PropertyChangeListener,
        RestartListener, ActionListener {

    private final Map<Integer, List<Integer>> neighborsTilesForTiles;

    private final List<FlipListener> flipListeners;
    private List<TileStatus> tilesStatus;

    public EightController() {
        flipListeners = new ArrayList<>();
        neighborsTilesForTiles = new HashMap<>();
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

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        if (!isChangeAllowed(evt)) {
            vetoChange(evt);
        }
    }

    //a change is allowed if a neighbor is the hole
    private boolean isChangeAllowed(PropertyChangeEvent evt) {
        //I always find a tile that is changing
        TileStatus changingTile = retrieveTileChanging(evt).orElseThrow(IllegalStateException::new);
        List<Integer> changingTileNeighbors = neighborsTilesForTiles.get(changingTile.getIdPos());
        return checkIfNeighborsContainHole(changingTileNeighbors);
    }

    private boolean checkIfNeighborsContainHole(List<Integer> changingTileNeighbors) {
        return changingTileNeighbors.stream()
                .map(neighborPos -> tilesStatus.get(neighborPos - 1))
                .anyMatch(tileStatus -> tileStatus.getLabel() == HOLE_LABEL);
    }

    private Optional<TileStatus> retrieveTileChanging(PropertyChangeEvent evt) {
        PosLabelValue oldValue = (PosLabelValue) evt.getOldValue();
        return tilesStatus.stream().filter(tile -> tile.idPos == oldValue.pos()).findFirst();
    }

    private void vetoChange(PropertyChangeEvent evt) throws PropertyVetoException {
        setText("KO");
        throw new PropertyVetoException("nope", evt);
    }

    @Override
    public void onRestart(RestartEvent restartEvent) {
        Integer[] permutation = restartEvent.getPermutation();
        tilesStatus = mapPermutationToStatus(permutation);
    }

    private List<TileStatus> mapPermutationToStatus(Integer[] permutation) {
        return IntStream.range(0, permutation.length)
                .mapToObj(i -> new TileStatus(i + 1, permutation[i]))
                .toList();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //simply changes status: checks happened earlier
        changeInternalStatusGame(evt);
        notifyAllFlipStatusListener();
        setText("OK");
    }

    private void notifyAllFlipStatusListener() {
        FlipStatusEvent flipStatusEvent = new FlipStatusEvent(isFlipPossible());
        flipListeners.stream().forEach(l -> l.onFlipUpdate(flipStatusEvent));
    }

    private void changeInternalStatusGame(PropertyChangeEvent evt) {
        TileStatus changingTile = retrieveTileChanging(evt).orElseThrow(IllegalStateException::new);
        PosLabelValue newValue = (PosLabelValue) evt.getNewValue();
        changingTile.label = newValue.label();
    }

    public synchronized void addFlipListener(FlipListener l) {
        flipListeners.add(l);
    }

    public synchronized void removeFlipListener(FlipListener l) {
        flipListeners.remove(l);
    }

    private boolean isFlipPossible() {
        return tilesStatus.get(8).label == 9;
    }

    private Integer[] getFlippedPermutation() {
        Integer[] flippedPermutation = new Integer[9];
        this.tilesStatus.stream().map(tile -> tile.label).toList().toArray(flippedPermutation);
        Integer temp = flippedPermutation[0];
        flippedPermutation[0] = flippedPermutation[1];
        flippedPermutation[1] = temp;
        return flippedPermutation;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isFlipPossible()) {
            Integer[] flippedPermutation = getFlippedPermutation();
            FlipEvent flipEvent = new FlipEvent(flippedPermutation);
            flipListeners.stream().forEach(l -> l.onFlip(flipEvent));
        }
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


