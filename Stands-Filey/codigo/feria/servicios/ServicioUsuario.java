package feria.servicios;

import feria.modelo.Usuario;
import feria.persistencia.dao.UsuarioDAO;
import java.util.List;
import java.util.Optional;

public class ServicioUsuario {
    private final UsuarioDAO dao;
    public ServicioUsuario(UsuarioDAO dao) { this.dao = dao; }
    public List<Usuario> listarUsuarios() { return dao.listarTodos(); }
    public Optional<Usuario> buscarPorId(int id) { return dao.buscarPorId(id); }
    public Optional<Usuario> buscarPorCorreo(String correo) { return dao.buscarPorCorreo(correo); }
}
