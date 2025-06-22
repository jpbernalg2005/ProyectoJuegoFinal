package com.atraparalagato.impl.repository;

import com.atraparalagato.base.model.GameState;
import com.atraparalagato.impl.model.HexPosition;
import com.atraparalagato.base.repository.DataRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class H2GameRepository extends DataRepository<GameState<HexPosition>, String> {
    private final Map<String, GameState<HexPosition>> storage = new ConcurrentHashMap<>();

    @Override
    public GameState<HexPosition> save(GameState<HexPosition> entity) {
        storage.put(entity.getGameId(), entity);
        return entity;
    }

    @Override
    public Optional<GameState<HexPosition>> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<GameState<HexPosition>> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<GameState<HexPosition>> findWhere(Predicate<GameState<HexPosition>> condition) {
        List<GameState<HexPosition>> result = new ArrayList<>();
        for (GameState<HexPosition> state : storage.values()) {
            if (condition.test(state)) result.add(state);
        }
        return result;
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<GameState<HexPosition>> condition, Function<GameState<HexPosition>, R> transformer) {
        List<R> result = new ArrayList<>();
        for (GameState<HexPosition> state : storage.values()) {
            if (condition.test(state)) result.add(transformer.apply(state));
        }
        return result;
    }

    @Override
    public long countWhere(Predicate<GameState<HexPosition>> condition) {
        return storage.values().stream().filter(condition).count();
    }

    @Override
    public boolean deleteById(String id) {
        return storage.remove(id) != null;
    }

    @Override
    public long deleteWhere(Predicate<GameState<HexPosition>> condition) {
        long count = 0;
        Iterator<Map.Entry<String, GameState<HexPosition>>> it = storage.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, GameState<HexPosition>> entry = it.next();
            if (condition.test(entry.getValue())) {
                it.remove();
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    @Override
    public <R> R executeInTransaction(Function<DataRepository<GameState<HexPosition>, String>, R> operation) {
        return operation.apply(this);
    }

    @Override
    public List<GameState<HexPosition>> findWithPagination(int page, int size) {
        List<GameState<HexPosition>> all = findAll();
        int from = Math.min(page * size, all.size());
        int to = Math.min(from + size, all.size());
        return all.subList(from, to);
    }

    @Override
    public List<GameState<HexPosition>> findAllSorted(Function<GameState<HexPosition>, ? extends Comparable<?>> sortKeyExtractor, boolean ascending) {
        List<GameState<HexPosition>> all = findAll();
        all.sort(Comparator.comparing(game -> (Comparable) sortKeyExtractor.apply(game)));
        if (!ascending) Collections.reverse(all);
        return all;
    }

    @Override
    public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    protected void initialize() {
        // Implementación mínima
    }

    @Override
    protected void cleanup() {
        // Implementación mínima
    }
}
/* no funciono la implementacion de h2 
package com.atraparalagato.impl.repository;

import com.atraparalagato.base.repository.DataRepository;
import com.atraparalagato.impl.model.HexGameState;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.time.ZoneId;

public class H2GameRepository extends DataRepository<HexGameState, String> {

    private static final String DB_URL = "jdbc:h2:./data/atraparalagato";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Connection connection;

    public H2GameRepository() {
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            createSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos H2", e);
        }
    }

    @Override
    public HexGameState save(HexGameState entity) {
        String sqlInsert = "MERGE INTO games (id, state) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sqlInsert)) {
            stmt.setString(1, entity.getGameId());
            stmt.setString(2, serializeGameState(entity));
            stmt.executeUpdate();
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el juego", e);
        }
    }

    @Override
    public Optional<HexGameState> findById(String id) {
        String sql = "SELECT state FROM games WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String stateJson = rs.getString("state");
                return Optional.of(deserializeGameState(stateJson, id));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar el juego", e);
        }
    }

    @Override
    public List<HexGameState> findAll() {
        String sql = "SELECT id, state FROM games";
        List<HexGameState> result = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String stateJson = rs.getString("state");
                result.add(deserializeGameState(stateJson, id));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los juegos", e);
        }
        return result;
    }

    @Override
    public List<HexGameState> findWhere(Predicate<HexGameState> condition) {
        // Simple: cargar todos y filtrar en memoria
        return findAll().stream().filter(condition).collect(Collectors.toList());
    }

    @Override
    public <R> List<R> findAndTransform(Predicate<HexGameState> condition, Function<HexGameState, R> transformer) {
        return findAll().stream().filter(condition).map(transformer).collect(Collectors.toList());
    }

    @Override
    public long countWhere(Predicate<HexGameState> condition) {
        return findWhere(condition).size();
    }

    @Override
    public boolean deleteById(String id) {
        String sql = "DELETE FROM games WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el juego", e);
        }
    }

    @Override
    public long deleteWhere(Predicate<HexGameState> condition) {
        List<HexGameState> toDelete = findWhere(condition);
        long count = 0;
        for (HexGameState state : toDelete) {
            if (deleteById(state.getGameId())) count++;
        }
        return count;
    }

    @Override
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM games WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia", e);
        }
    }

    @Override
    public <R> R executeInTransaction(Function<DataRepository<HexGameState, String>, R> operation) {
        try {
            connection.setAutoCommit(false);
            R result = operation.apply(this);
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (Exception e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("Error en transacción", e);
        }
    }

    @Override
    public List<HexGameState> findWithPagination(int page, int size) {
        String sql = "SELECT id, state FROM games LIMIT ? OFFSET ?";
        List<HexGameState> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, size);
            stmt.setInt(2, page * size);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString("id");
                String stateJson = rs.getString("state");
                result.add(deserializeGameState(stateJson, id));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en paginación", e);
        }
        return result;
    }

   @Override
    public List<HexGameState> findAllSorted(Function<HexGameState, ? extends Comparable<?>> sortKeyExtractor, boolean ascending) {
        List<HexGameState> all = findAll();
        all.sort((a, b) -> {
            Comparable<?> keyA = sortKeyExtractor.apply(a);
            Comparable<?> keyB = sortKeyExtractor.apply(b);

            if (keyA == null && keyB == null) return 0;
            if (keyA == null) return ascending ? 1 : -1;
            if (keyB == null) return ascending ? -1 : 1;

            @SuppressWarnings("unchecked")
            int cmp = ((Comparable<Object>) keyA).compareTo(keyB);
            return ascending ? cmp : -cmp;
        });
        return all;
    }





    @Override
    public <R> List<R> executeCustomQuery(String query, Function<Object, R> resultMapper) {
        List<R> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                results.add(resultMapper.apply(rs.getObject(1)));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error en consulta personalizada", e);
        }
        return results;
    }

    @Override
    protected void initialize() {
        createSchema();
    }

    @Override
    protected void cleanup() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cerrar la conexión", e);
        }
    }

    // --- Métodos auxiliares ---

    private void createSchema() {
        String sql = "CREATE TABLE IF NOT EXISTS games (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "state CLOB NOT NULL" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear el esquema", e);
        }
    }

    private String serializeGameState(HexGameState gameState) {
        try {
            return objectMapper.writeValueAsString(gameState.getSerializableState());
        } catch (Exception e) {
            throw new RuntimeException("Error al serializar el estado del juego", e);
        }
    }

    private HexGameState deserializeGameState(String serializedData, String gameId) {
        try {
            Map<String, Object> state = objectMapper.readValue(serializedData, Map.class);
            HexGameState gameState = new HexGameState(gameId, (int) state.get("boardSize"));
            gameState.restoreFromSerializable(state);
            return gameState;
        } catch (Exception e) {
            throw new RuntimeException("Error al deserializar el estado del juego", e);
        }
    }


    public Map<String, Object> getRepositoryStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalGames = countWhere(game -> true);
        long finishedGames = countWhere(HexGameState::isGameFinished);
        long wonGames = countWhere(HexGameState::hasPlayerWon);

        stats.put("totalGames", totalGames);
        stats.put("finishedGames", finishedGames);
        stats.put("wonGames", wonGames);
        stats.put("inProgressGames", totalGames - finishedGames);
        stats.put("winRate", totalGames > 0 ? (double) wonGames / totalGames * 100 : 0);

        return stats;
    }

    
    public long cleanupOldGames(long maxAgeMillis) {
        long currentTime = System.currentTimeMillis();

        return deleteWhere(game -> {
            long gameTime = game.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return currentTime - gameTime > maxAgeMillis;
        });
    }
} */
