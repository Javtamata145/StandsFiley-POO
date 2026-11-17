package feria.persistencia.dao;

import feria.persistencia.ConexionBD;
import java.sql.Connection;
import java.sql.SQLException;

abstract class DAOBase {
    protected Connection conectar() throws SQLException {
        return ConexionBD.obtenerConexion();
    }

    protected RuntimeException errorLectura(SQLException e) {
        return new IllegalStateException("No se pudo consultar la base de datos: " + e.getMessage(), e);
    }
}
