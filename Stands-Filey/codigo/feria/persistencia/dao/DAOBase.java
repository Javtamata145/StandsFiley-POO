package feria.persistencia.dao;

import feria.persistencia.ConexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase padre de todos los DAO.
 *
 * Contiene la lógica repetida de JDBC (abrir conexión, preparar la sentencia,
 * ejecutarla, recorrer el ResultSet y cerrar todo). Los DAO hijos solo indican
 * QUÉ consulta ejecutar (tomada de ConsultasSQL), CÓMO asignar sus parámetros
 * y CÓMO convertir cada fila en un objeto del modelo.
 */
abstract class DAOBase {

    /** Convierte una fila del ResultSet en un objeto del modelo. */
    @FunctionalInterface
    protected interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    /** Asigna los parámetros (?) de una sentencia preparada. */
    @FunctionalInterface
    protected interface Parametros {
        void asignar(PreparedStatement ps) throws SQLException;
    }

    /** Para consultas sin parámetros. */
    protected static final Parametros SIN_PARAMETROS = ps -> { };

    protected Connection conectar() throws SQLException {
        return ConexionBD.obtenerConexion();
    }

    // ------------------------------------------------------------------
    // READ
    // ------------------------------------------------------------------

    /** Ejecuta un SELECT y devuelve todas las filas como lista. */
    protected <T> List<T> consultarLista(String sql, Parametros parametros, Mapeador<T> mapeador) {
        List<T> lista = new ArrayList<>();
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            parametros.asignar(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw error("consultar", e);
        }
    }

    /** Ejecuta un SELECT que devuelve como máximo una fila. */
    protected <T> Optional<T> consultarUno(String sql, Parametros parametros, Mapeador<T> mapeador) {
        List<T> lista = consultarLista(sql, parametros, mapeador);
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    // ------------------------------------------------------------------
    // CREATE
    // ------------------------------------------------------------------

    /** Ejecuta un INSERT y devuelve el id AUTO_INCREMENT generado. */
    protected int insertarYObtenerId(String sql, Parametros parametros) {
        try (Connection c = conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            parametros.asignar(ps);
            ps.executeUpdate();
            try (ResultSet llaves = ps.getGeneratedKeys()) {
                if (llaves.next()) {
                    return llaves.getInt(1);
                }
            }
            throw new IllegalStateException("La base de datos no devolvió el id generado.");
        } catch (SQLException e) {
            throw error("insertar", e);
        }
    }

    // ------------------------------------------------------------------
    // CREATE (sin id generado) / UPDATE / DELETE
    // ------------------------------------------------------------------

    /** Ejecuta un INSERT, UPDATE o DELETE y devuelve cuántas filas se afectaron. */
    protected int ejecutar(String sql, Parametros parametros) {
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            parametros.asignar(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw error("guardar cambios", e);
        }
    }

    // ------------------------------------------------------------------
    // Utilidades de conversión Java <-> SQL
    // ------------------------------------------------------------------

    protected static void setEnteroONulo(PreparedStatement ps, int indice, Integer valor) throws SQLException {
        if (valor == null) ps.setNull(indice, Types.INTEGER);
        else ps.setInt(indice, valor);
    }

    protected static void setFecha(PreparedStatement ps, int indice, LocalDate fecha) throws SQLException {
        if (fecha == null) ps.setNull(indice, Types.DATE);
        else ps.setDate(indice, Date.valueOf(fecha));
    }

    protected static void setFechaHora(PreparedStatement ps, int indice, LocalDateTime fecha) throws SQLException {
        if (fecha == null) ps.setNull(indice, Types.TIMESTAMP);
        else ps.setTimestamp(indice, Timestamp.valueOf(fecha));
    }

    protected static Integer getEnteroONulo(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : valor;
    }

    protected static LocalDate getFecha(ResultSet rs, String columna) throws SQLException {
        Date fecha = rs.getDate(columna);
        return fecha == null ? null : fecha.toLocalDate();
    }

    protected static LocalDateTime getFechaHora(ResultSet rs, String columna) throws SQLException {
        Timestamp fecha = rs.getTimestamp(columna);
        return fecha == null ? null : fecha.toLocalDateTime();
    }

    // ------------------------------------------------------------------
    // Manejo de errores
    // ------------------------------------------------------------------

    protected RuntimeException error(String accion, SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException || "23000".equals(e.getSQLState())) {
            return new IllegalStateException(
                    "Operación rechazada por la base de datos (registro duplicado o con datos relacionados): "
                    + e.getMessage(), e);
        }
        return new IllegalStateException("No se pudo " + accion + " en la base de datos: " + e.getMessage(), e);
    }
}
