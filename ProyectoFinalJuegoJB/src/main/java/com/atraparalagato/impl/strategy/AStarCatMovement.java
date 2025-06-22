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
 * Estrategia de movimiento del gato utilizando el algoritmo A*.
 * 
 * Design Patterns:
 * - Strategy Pattern: Permite diferentes algoritmos de movimiento
 * - Template Method: Define el flujo general de decisión
 * 
 * SOLID Principles:
 * - Single Responsibility: Solo maneja la lógica de movimiento del gato
 * - Open/Closed: Abierta para nuevos algoritmos, cerrada para modificación
 * - Dependency Inversion: Depende de abstracciones, no implementaciones concretas
 * 
 * Conceptos a implementar:
 * - Programación Funcional: Uso de Function, Predicate, Optional
 * - Algoritmos: A*, BFS, DFS, Greedy, etc.
 * - Modularización: Separación de heurísticas y algoritmos
 */
public class AStarCatMovement extends CatMovementStrategy<HexPosition> {

    public AStarCatMovement(GameBoard<HexPosition> board) {
        super(board);
    }

    /**
     * Obtiene todos los movimientos posibles desde una posición.
     * Debe considerar las reglas del tablero y posiciones bloqueadas.
     */
    @Override
    protected List<HexPosition> getPossibleMoves(HexPosition currentPosition) {
        return new ArrayList<>();
    }

    /**
     * Selecciona el mejor movimiento de una lista de movimientos posibles.
     * Aquí es donde los estudiantes implementan su algoritmo específico.
     */
    @Override
    protected Optional<HexPosition> selectBestMove(List<HexPosition> possibleMoves, 
                                                   HexPosition currentPosition, 
                                                   HexPosition targetPosition) {
        return possibleMoves.isEmpty() ? Optional.empty() : Optional.of(possibleMoves.get(0));
    }

    /**
     * Función heurística para evaluar qué tan buena es una posición.
     * Los estudiantes pueden implementar diferentes heurísticas.
     * Ejemplo de programación funcional: Function<T, Double>
     */
    @Override
    protected Function<HexPosition, Double> getHeuristicFunction(HexPosition targetPosition) {
        return pos -> 0.0;
    }

    /**
     * Predicado para determinar si una posición es un objetivo válido.
     * Ejemplo: posiciones en el borde del tablero para escapar.
     */
    @Override
    protected Predicate<HexPosition> getGoalPredicate() {
        return pos -> false;
    }

    /**
     * Calcula el costo de moverse de una posición a otra.
     * Útil para algoritmos como A* que consideran costos.
     */
    @Override
    protected double getMoveCost(HexPosition from, HexPosition to) {
        return 1.0;
    }

    /**
     * Verifica si existe un camino desde la posición actual hasta el objetivo.
     * Los estudiantes pueden implementar diferentes algoritmos de pathfinding.
     */
    @Override
    public boolean hasPathToGoal(HexPosition currentPosition) {
        return false;
    }

    /**
     * Obtiene el camino completo desde la posición actual hasta el objetivo.
     * Útil para debugging y visualización.
     * Retorna una lista vacía si no hay camino.
     */
    @Override
    public List<HexPosition> getFullPath(HexPosition currentPosition, HexPosition targetPosition) {
        return new ArrayList<>();
    }
}