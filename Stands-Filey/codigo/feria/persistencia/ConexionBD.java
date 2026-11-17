package feria.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexiones JDBC. Configuración mediante variables de entorno:
 * FERIA_DB_URL, FERIA_DB_USER y FERIA_DB_PASSWORD.
 */
public final class ConexionBD {
    private static final String URL_DEFAULT =
            "jdbc:mysql://localhost:3306/stands_filey?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Merida";
    private static final String USER_DEFAULT = "root";
    private static final String PASSWORD_DEFAULT = "";

    private ConexionBD() {}

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "No se encontró el driver MySQL Connector/J en el classpath. Agrega mysql-connector-j.jar a la carpeta lib/ y al Build Path.",
                    e);
        }
        String url = System.getenv().getOrDefault("FERIA_DB_URL", URL_DEFAULT);
        String usuario = System.getenv().getOrDefault("FERIA_DB_USER", USER_DEFAULT);
        String contrasena = System.getenv().getOrDefault("FERIA_DB_PASSWORD", PASSWORD_DEFAULT);
        return DriverManager.getConnection(url, usuario, contrasena);
    }
}
