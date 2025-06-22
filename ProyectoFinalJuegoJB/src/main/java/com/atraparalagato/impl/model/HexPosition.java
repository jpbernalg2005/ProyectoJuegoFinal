package com.atraparalagato.impl.model;

import com.atraparalagato.base.model.Position;

/**
 * Clase para representar posiciones en un tablero hexagonal.
 * 
 * SOLID Principles:
 * - Single Responsibility: Representa una posición en coordenadas hexagonales
 * - Open/Closed: Permite calcular distancias, sumas y restas en un sistema de coordenadas hexagonales
 * 
 * Conceptos a implementar:
 * - OOP: Herencia, encapsulación, polimorfismo
 * - Modularización: Abstracción del sistema de coordenadas hexagonales
 * - Programación Funcional: Métodos que pueden ser usados en streams
 */
public class HexPosition extends Position {
    private final int q;
    private final int r;

    /**
     * Constructor para posiciones hexagonales.
     * @param q Coordenada Q (eje horizontal)
     * @param r Coordenada R (eje diagonal)
     */
    public HexPosition(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int getQ() { return q; }
    public int getR() { return r; }
    public int getS() { return -q - r; }

    /**
     * Calcula la distancia entre esta posición y otra en el tablero hexagonal.
     * @param other Otra posición
     * @return Distancia en línea recta entre las dos posiciones
     */
    @Override
    public double distanceTo(Position other) {
        if (!(other instanceof HexPosition)) return Double.POSITIVE_INFINITY;
        HexPosition o = (HexPosition) other;
        return (Math.abs(q - o.q) + Math.abs(r - o.r) + Math.abs(getS() - o.getS())) / 2.0;
    }

    /**
     * Suma esta posición con otra en el tablero hexagonal, retornando una nueva posición.
     * @param other Otra posición a sumar
     * @return Nueva posición resultante de la suma
     */
    @Override
    public Position add(Position other) {
        if (!(other instanceof HexPosition)) throw new IllegalArgumentException();
        HexPosition o = (HexPosition) other;
        return new HexPosition(q + o.q, r + o.r);
    }

    /**
     * Resta otra posición de esta en el tablero hexagonal, retornando una nueva posición.
     * @param other Otra posición a restar
     * @return Nueva posición resultante de la resta
     */
    @Override
    public Position subtract(Position other) {
        if (!(other instanceof HexPosition)) throw new IllegalArgumentException();
        HexPosition o = (HexPosition) other;
        return new HexPosition(q - o.q, r - o.r);
    }

    /**
     * Verifica si esta posición es adyacente a otra en el tablero hexagonal.
     * @param other Otra posición
     * @return Verdadero si las posiciones son adyacentes, falso en caso contrario
     */
    @Override
    public boolean isAdjacentTo(Position other) {
        if (!(other instanceof HexPosition)) return false;
        HexPosition o = (HexPosition) other;
        int dq = Math.abs(q - o.q);
        int dr = Math.abs(r - o.r);
        int ds = Math.abs(getS() - o.getS());
        return (dq + dr + ds) == 2;
    }

    /**
     * Verifica si esta posición está dentro de los límites dados en el tablero hexagonal.
     * @param maxSize Tamaño máximo del tablero
     * @return Verdadero si la posición está dentro de los límites, falso en caso contrario
     */
    @Override
    public boolean isWithinBounds(int maxSize) {
        int radius = (maxSize - 1) / 2;
        return Math.abs(q) <= radius && Math.abs(r) <= radius && Math.abs(getS()) <= radius;
    }

    /**
     * Obtiene una representación única de la posición para hashing.
     * @return Código hash de la posición
     */
    @Override
    public int hashCode() {
        return 31 * q + r;
    }

    /**
     * Compara esta posición con otra para igualdad.
     * @param obj Objeto a comparar
     * @return Verdadero si las posiciones son iguales, falso en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HexPosition)) return false;
        HexPosition o = (HexPosition) obj;
        return q == o.q && r == o.r;
    }

    /**
     * Representación en string de la posición hexagonal.
     * @return String con la representación de la posición
     */
    @Override
    public String toString() {
        return "HexPosition(" + q + "," + r + ")";
    }
}