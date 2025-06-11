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

    /**
     * Crea un nuevo veterinario en la base de datos.
     *
     * @param vet El objeto Veterinario que contiene la información del nuevo veterinario.
     *            Se espera que los campos nombre, especialidad y correo estén establecidos.
     *            El campo id_veterinario será generado automáticamente por la base de datos.
     * @return El objeto Veterinario recién creado, incluyendo el id_veterinario generado, o null si ocurre un error.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos durante la creación.
     */
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

    /**
     * Actualiza la información de un veterinario existente.
     *
     * @param vet El objeto Veterinario con la información actualizada.
     *           Se requiere que el campo id_veterinario esté correctamente establecido.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
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

    /**
     * Elimina un veterinario de la base de datos basado en su id_veterinario.
     *
     * @param vet El objeto Veterinario que contiene el id_veterinario del veterinario a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
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

    /**
     * Busca veterinarios por nombre (búsqueda parcial usando LIKE).
     *
     * @param nombre El nombre o parte del nombre a buscar.
     * @return Una lista de veterinarios que coinciden con el nombre proporcionado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
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
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en search (VeterinarioDAO): " + e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar ResultSet en search (VeterinarioDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return records;
    }

    /**
     * Obtiene un veterinario por su id_veterinario.
     *
     * @param id El id_veterinario del veterinario a buscar.
     * @return Un objeto Veterinario si se encuentra, null si no existe.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
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
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en getById (VeterinarioDAO): " + e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar ResultSet en getById (VeterinarioDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return vet;
    }
}
