package persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import esfe.dominio.Veterinario;

public class VeterinarioDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public VeterinarioDAO() {
        conn = ConnectionManager.getInstance();
    }

    public Veterinario create(Veterinario vet) throws SQLException {
        Veterinario res = null;
        PreparedStatement localPs = null;
        try {
            localPs = conn.connect().prepareStatement(
                    "INSERT INTO Veterinarios (nombre, especialidad, correo) VALUES (?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            localPs.setString(1, vet.getNombre());
            localPs.setString(2, vet.getEspecialidad());
            localPs.setString(3, vet.getCorreo());

            int filas = localPs.executeUpdate();

            if (filas != 0) {
                ResultSet generatedKeys = localPs.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("Creating veterinario failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el veterinario: " + ex.getMessage(), ex);
        } finally {
            if (localPs != null) {
                try {
                    localPs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en create (VeterinarioDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    public boolean update(Veterinario vet) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Veterinarios SET nombre = ?, especialidad = ?, correo = ? WHERE id_veterinario = ?"
            );
            ps.setString(1, vet.getNombre());
            ps.setString(2, vet.getEspecialidad());
            ps.setString(3, vet.getCorreo());
            ps.setInt(4, vet.getIdVeterinario());

            int filas = ps.executeUpdate();
            res = (filas > 0);
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar el veterinario: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en update (VeterinarioDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    public boolean delete(Veterinario vet) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Veterinarios WHERE id_veterinario = ?"
            );
            ps.setInt(1, vet.getIdVeterinario());

            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el veterinario: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en delete (VeterinarioDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    public ArrayList<Veterinario> search(String nombre) throws SQLException {
        ArrayList<Veterinario> records = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_veterinario, nombre, especialidad, correo FROM Veterinarios WHERE nombre LIKE ?"
            );
            ps.setString(1, "%" + nombre + "%");

            rs = ps.executeQuery();
            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("id_veterinario"));
                vet.setNombre(rs.getString("nombre"));
                vet.setEspecialidad(rs.getString("especialidad"));
                vet.setCorreo(rs.getString("correo"));
                records.add(vet);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar veterinarios: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en search: " + e.getMessage());
            }
            if (rs != null) try { rs.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en search: " + e.getMessage());
            }
            conn.disconnect();
        }
        return records;
    }

    public Veterinario getById(int id) throws SQLException {
        Veterinario vet = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_veterinario, nombre, especialidad, correo FROM Veterinarios WHERE id_veterinario = ?"
            );
            ps.setInt(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("id_veterinario"));
                vet.setNombre(rs.getString("nombre"));
                vet.setEspecialidad(rs.getString("especialidad"));
                vet.setCorreo(rs.getString("correo"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener un veterinario por id: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en getById: " + e.getMessage());
            }
            if (rs != null) try { rs.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en getById: " + e.getMessage());
            }
            conn.disconnect();
        }
        return vet;
    }

    /**
     * Retorna todos los veterinarios en la base de datos.
     *
     * @return Lista de objetos Veterinario.
     */
    public ArrayList<Veterinario> getAll() {
        ArrayList<Veterinario> lista = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_veterinario, nombre, especialidad, correo FROM Veterinarios"
            );
            rs = ps.executeQuery();

            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("id_veterinario"));
                vet.setNombre(rs.getString("nombre"));
                vet.setEspecialidad(rs.getString("especialidad"));
                vet.setCorreo(rs.getString("correo"));
                lista.add(vet);
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener todos los veterinarios: " + ex.getMessage());
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en getAll: " + e.getMessage());
            }
            if (rs != null) try { rs.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en getAll: " + e.getMessage());
            }
            try {
                conn.disconnect();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return lista;
    }
}
