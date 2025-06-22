package com.atraparalagato.impl.strategy;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.model.HexPosition;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Estrategia de movimiento del gato basada en el algoritmo BFS (Breadth-First Search).
 * 
 * Design Patterns:
 * - Strategy Pattern: Implementa un algoritmo específico de movimiento
 * 
 * SOLID Principles:
 * - Single Responsibility: Solo maneja la lógica de movimiento del gato usando BFS
 * - Open/Closed: Abierta para nuevos algoritmos, cerrada para modificación
 * - Dependency Inversion: Depende de abstracciones, no implementaciones concretas
 * 
 * Conceptos implementados:
 * - Programación Funcional: Uso de Function, Predicate
 * - Algoritmos: BFS
 * - Modularización: Separación de heurísticas y algoritmos
 */
public class BFSCatMovement extends CatMovementStrategy<HexPosition> {

    public BFSCatMovement(GameBoard<HexPosition> board) {
        super(board);
    }

    /**
     * Obtiene todos los movimientos posibles desde una posición usando BFS.
     * (Este método debe ser implementado completamente por los estudiantes).
     */
    @Override
    protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
        return board.getAdjacentPositions(currentPosition).stream()
            .filter(pos -> !board.isBlocked(pos))
            .toList();
    }


    /**
     * Selecciona el mejor movimiento de una lista de movimientos posibles.
     * En esta implementación, simplemente retorna el primer movimiento posible.
     */
    @Override
    protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, 
                                                   HexPosition currentPosition, 
                                                   HexPosition targetPosition) {
        return possibleMoves.isEmpty() ? Optional.empty() : Optional.of(possibleMoves.get(0));
    }

    /**
     * Función heurística para evaluar qué tan buena es una posición.
     * En esta implementación, todas las posiciones tienen la misma evaluación.
     */
    @Override
    protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
        return pos -> 0.0;
    }

    /**
     * Predicado para determinar si una posición es un objetivo válido.
     * En esta implementación, ninguna posición es considerada un objetivo.
     */
    @Override
    protected Predicate<HexPosition> getGoalPredicate() {
        return pos -> false;
    }

    /**
     * Calcula el costo de moverse de una posición a otra.
     * En esta implementación, todos los movimientos tienen el mismo costo.
     */
    @Override
    protected double getMoveCost(HexPosition from, HexPosition to) {
        return 1.0;
    }

    /**
     * Verifica si existe un camino desde la posición actual hasta el objetivo.
     * (Este método debe ser implementado completamente por los estudiantes).
     */
    @Override
    public boolean hasPathToGoal(HexPosition currentPosition) {
        // TODO: Implementar la lógica para verificar el camino al objetivo usando BFS
        return false;
    }

    /**
     * Obtiene el camino completo desde la posición actual hasta el objetivo.
     * (Este método debe ser implementado completamente por los estudiantes).
     */
    @Override
    public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
        // TODO: Implementar la lógica para obtener el camino completo al objetivo usando BFS
        return new ArrayList<>();
    }
}