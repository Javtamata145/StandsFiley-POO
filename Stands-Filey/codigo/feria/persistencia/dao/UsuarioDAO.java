package feria.persistencia.dao;

import feria.modelo.Usuario;
import feria.persistencia.ConsultasSQL.Usuarios;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de la tabla usuarios. CRUD completo. */
public class UsuarioDAO extends DAOBase {

    // ---------------- READ ----------------
    public List<Usuario> listarTodos() {
        return consultarLista(Usuarios.LISTAR_TODOS, SIN_PARAMETROS, this::mapear);
    }

    public Optional<Usuario> buscarPorId(int id) {
        return consultarUno(Usuarios.BUSCAR_POR_ID, ps -> ps.setInt(1, id), this::mapear);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return consultarUno(Usuarios.BUSCAR_POR_CORREO, ps -> ps.setString(1, correo), this::mapear);
    }

    // ---------------- CREATE ----------------
    /** Inserta el usuario y le asigna el id generado. */
    public Usuario insertar(Usuario u) {
        u.id = insertarYObtenerId(Usuarios.INSERTAR, ps -> {
            ps.setString(1, u.nombreUsuarioEditorial);
            ps.setString(2, u.correo);
            ps.setString(3, u.contrasenaHash);
            ps.setString(4, u.salt);
            ps.setString(5, u.telefono);
        });
        return u;
    }

    // ---------------- UPDATE ----------------
    /** Actualiza nombre, correo y teléfono. La contraseña se cambia con actualizarContrasena. */
    public boolean actualizar(Usuario u) {
        return ejecutar(Usuarios.ACTUALIZAR, ps -> {
            ps.setString(1, u.nombreUsuarioEditorial);
            ps.setString(2, u.correo);
            ps.setString(3, u.telefono);
            ps.setInt(4, u.id);
        }) > 0;
    }

    public boolean actualizarContrasena(int id, String hash, String salt) {
        return ejecutar(Usuarios.ACTUALIZAR_CONTRASENA, ps -> {
            ps.setString(1, hash);
            ps.setString(2, salt);
            ps.setInt(3, id);
        }) > 0;
    }

    // ---------------- DELETE ----------------
    public boolean eliminar(int id) {
        return ejecutar(Usuarios.ELIMINAR, ps -> ps.setInt(1, id)) > 0;
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
