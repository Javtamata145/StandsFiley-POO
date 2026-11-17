package feria.persistencia.dao;

import feria.modelo.Usuario;
import java.sql.*;
import java.util.*;

public class UsuarioDAO extends DAOBase {
    public List<Usuario> listarTodos() {
        String sql = "SELECT id, nombre_usuario_editorial, correo, contrasena_hash, salt, telefono FROM usuarios";
        List<Usuario> lista = new ArrayList<>();
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) { throw errorLectura(e); }
    }

    public Optional<Usuario> buscarPorId(int id) {
        String sql = "SELECT id, nombre_usuario_editorial, correo, contrasena_hash, salt, telefono FROM usuarios WHERE id = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw errorLectura(e); }
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        String sql = "SELECT id, nombre_usuario_editorial, correo, contrasena_hash, salt, telefono FROM usuarios WHERE correo = ?";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw errorLectura(e); }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario x = new Usuario();
        x.id = rs.getInt("id");
        x.nombreUsuarioEditorial = rs.getString("nombre_usuario_editorial");
        x.correo = rs.getString("correo");
        x.contrasenaHash = rs.getString("contrasena_hash");
        x.salt = rs.getString("salt");
        x.telefono = rs.getString("telefono");
        return x;
    }
}
