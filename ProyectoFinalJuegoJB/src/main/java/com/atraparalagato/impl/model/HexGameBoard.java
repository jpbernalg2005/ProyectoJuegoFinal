package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.GameBoard;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Clase concreta para el tablero del juego Hex.
 */
public class HexGameBoard extends GameBoard<HexPosition> {
    public HexGameBoard(int size) { super(size); }

    @Override protected Set<HexPosition> initializeBlockedPositions() { return new HashSet<>(); }
    @Override
    public boolean isPositionInBounds(HexPosition position) {
        int size = getSize();
        return Math.abs(position.getQ()) <= size &&
            Math.abs(position.getR()) <= size &&
            Math.abs(position.getS()) <= size;
    }
    @Override protected boolean isValidMove(HexPosition position) {
        return isPositionInBounds(position) && 
               !isAtBorder(position) && 
               !isBlocked(position); }
    @Override
    protected void executeMove(HexPosition position) {
        blockedPositions.add(position);
    }
    public boolean isAtBorder(HexPosition position) {
        return Math.abs(position.getQ()) == size ||
               Math.abs(position.getR()) == size ||
               Math.abs(position.getS()) == size;
    }
    private List<HexPosition> getAllPossiblePositions() {
        List<HexPosition> positions = new ArrayList<>();
        
        // Generar todas las posiciones dentro del tablero (excluyendo el borde para jugabilidad)
        for (int q = -size + 1; q < size; q++) {
            for (int r = -size + 1; r < size; r++) {
                HexPosition pos = new HexPosition(q, r);
                // Solo incluir posiciones que no están en el borde (donde el jugador puede jugar)
                if (isPositionInBounds(pos) && !isAtBorder(pos)) {
                    positions.add(pos);
                }
            }
        }
        
        return positions;
    }

    @Override public List<HexPosition> getPositionsWhere(Predicate<HexPosition> condition) { 
        return getAllPossiblePositions().stream()
                .filter(condition)
                .collect(Collectors.toList()); }
    @Override public List<HexPosition> getAdjacentPositions(HexPosition position) {
        int[][] directions = {
            {1, 0}, {1, -1}, {0, -1},
            {-1, 0}, {-1, 1}, {0, 1}
        };
        List<HexPosition> adj = new ArrayList<>();
        for (int[] dir : directions) {
            HexPosition neighbor = new HexPosition(
                position.getQ() + dir[0],
                position.getR() + dir[1]
            );
            if (isPositionInBounds(neighbor)) {
                adj.add(neighbor);
            }
        }
        return adj;
    }
    public boolean isBlocked(HexPosition position) {
        // Verificar si la posición está en el conjunto de bloqueadas
        return blockedPositions.contains(position);
    }
}