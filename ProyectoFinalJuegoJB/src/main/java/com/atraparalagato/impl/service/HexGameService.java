package com.atraparalagato.impl.service;

import com.atraparalagato.base.model.GameBoard;
import com.atraparalagato.base.model.GameState;
import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.base.service.GameService;
import com.atraparalagato.impl.model.HexGameBoard;
import com.atraparalagato.impl.model.HexGameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.impl.repository.H2GameRepository;
import com.atraparalagato.base.strategy.CatMovementStrategy;
import com.atraparalagato.impl.strategy.BFSCatMovement;

import java.util.Optional;
import java.util.UUID;

public class HexGameService extends GameService<HexPosition> {

    private static int lastBoardSize = 5;
    
    public HexGameService() {
        super(
            new HexGameBoard(5),
            new BFSCatMovement(new HexGameBoard(5)),
            new H2GameRepository(),
            () -> UUID.randomUUID().toString(),
            HexGameBoard::new,
            id -> new HexGameState(id, HexGameService.lastBoardSize) // acceso estático correcto
        );
    }

    // Método para actualizar el tamaño antes de crear el juego
    public void setLastBoardSize(int boardSize) {
        HexGameService.lastBoardSize = boardSize;
    }

    @Override
    protected void initializeGame(GameState<HexPosition> gameState, GameBoard<HexPosition> board) {
        // Inicializa el estado del juego como en el ejemplo
        if (gameState instanceof HexGameState hexGameState && board instanceof HexGameBoard hexBoard) {
            
            hexGameState.setCatPosition(new HexPosition(0, 0));
        }
        // Puedes agregar callbacks si lo deseas
        gameState.setOnStateChanged(gs -> {});
        gameState.setOnGameEnded(gs -> {});
    }

    @Override
    protected HexPosition getTargetPosition(GameState<HexPosition> gameState) {
        if (!(gameState instanceof HexGameState hexGameState)) return null;
        HexGameBoard board = hexGameState.getGameBoard();
        if (board == null) return null;

        HexPosition catPos = gameState.getCatPosition();
        int boardSize = board.getSize();
        int radius = (boardSize - 1) / 2;

        // Buscar el borde más cercano al gato
        int minDist = Integer.MAX_VALUE;
        HexPosition target = null;
        for (int q = -radius; q <= radius; q++) {
            for (int r = -radius; r <= radius; r++) {
                HexPosition pos = new HexPosition(q, r);
                if (!pos.isWithinBounds(boardSize)) continue;
                // Si está en el borde
                if (Math.abs(q) == radius || Math.abs(r) == radius || Math.abs(-q - r) == radius) {
                    int dist = (int) catPos.distanceTo(pos);
                    if (dist < minDist) {
                        minDist = dist;
                        target = pos;
                    }
                }
            }
        }
        return target;
    }

    @Override
    public Object getGameStatistics(String gameId) {
        // Ejemplo: retorna estadísticas básicas del juego
        Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);
        if (gameStateOpt.isEmpty()) return null;
        GameState<HexPosition> gameState = gameStateOpt.get();

        return new java.util.HashMap<String, Object>() {{
            put("gameId", gameState.getGameId());
            put("status", gameState.getStatus());
            put("moveCount", gameState.getMoveCount());
            put("catPosition", gameState.getCatPosition());
            put("createdAt", gameState.getCreatedAt());
        }};
    }

    @Override
    public boolean isValidMove(String gameId, HexPosition position) {
        // 1. El juego debe existir
        Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);
        if (gameStateOpt.isEmpty()) return false;
        GameState<HexPosition> gameState = gameStateOpt.get();

        // 2. El juego no debe estar terminado 
        if ("PLAYER_LOSS".equalsIgnoreCase(String.valueOf(gameState.getStatus()))) return false;


        // 3. Obtener el tablero real y su tamaño desde el estado del juego
        if (!(gameState instanceof HexGameState hexGameState)) return false;
        HexGameBoard board = hexGameState.getGameBoard();
        if (board == null) return false;
        int boardSize = board.getSize();

        // 4. La posición debe estar dentro de los límites
        if (!position.isWithinBounds(boardSize)) return false;

        // 5. No debe estar bloqueada ni ocupada por el gato
        if (board.isBlocked(position)) return false;
        if (position.equals(gameState.getCatPosition())) return false;

        return true;
    }

    @Override
    public Optional<HexPosition> getSuggestedMove(String gameId) {
        // Sugerir una posición adyacente libre al gato
        Optional<GameState<HexPosition>> gameStateOpt = super.loadGameState(gameId);
        if (gameStateOpt.isEmpty()) return Optional.empty();
        GameState<HexPosition> gameState = gameStateOpt.get();

        if (!(gameState instanceof HexGameState hexGameState)) return Optional.empty();
        HexGameBoard board = hexGameState.getGameBoard();
        if (board == null) return Optional.empty();

        HexPosition catPos = gameState.getCatPosition();
        int boardSize = board.getSize();

        return board.getAdjacentPositions(catPos).stream()
                .filter(pos -> !board.isBlocked(pos) && !pos.equals(catPos) && pos.isWithinBounds(boardSize))
                .findFirst();
    }

    public Optional<GameState<HexPosition>> getGameState(String gameId) {
        return super.loadGameState(gameId);
    }
}