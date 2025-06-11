package persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import esfe.dominio.Duenos;
public class DuenosDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public DuenosDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea un nuevo dueño en la base de datos.
     *
     * @param dueno El objeto Duenos que contiene la información del nuevo dueño.
     *              Se espera que los campos nombre, telefono y correo estén correctamente establecidos.
     *              El campo id_dueno será generado automáticamente por la base de datos.
     * @return El objeto Duenos recién creado, incluyendo el id_dueno generado, o null si ocurre un error.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos durante la creación.
     */
    public Duenos create(Duenos dueno) throws SQLException {
        Duenos res = null;
        PreparedStatement localPs = null;
        try {
            localPs = conn.connect().prepareStatement(
                    "INSERT INTO Duenos (nombre, telefono, correo) VALUES (?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            localPs.setString(1, dueno.getNombre());
            localPs.setString(2, dueno.getTelefono());
            localPs.setString(3, dueno.getCorreo());

            int affectedRows = localPs.executeUpdate();

            if (affectedRows != 0) {
                ResultSet generatedKeys = localPs.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("Creating dueno failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear el dueño: " + ex.getMessage(), ex);
        } finally {
            if (localPs != null) {
                try {
                    localPs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en create (DuenoDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    /**
     * Actualiza la información de un dueño existente.
     *
     * @param dueno El objeto Duenos con la información actualizada.
     *              Se requiere que el campo id_dueno esté correctamente establecido.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public boolean update(Duenos dueno) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Duenos SET nombre = ?, telefono = ?, correo = ? WHERE id_dueno = ?"
            );
            ps.setString(1, dueno.getNombre());
            ps.setString(2, dueno.getTelefono());
            ps.setString(3, dueno.getCorreo());
            ps.setInt(4, dueno.getIdDueno());  // Asegúrate que uses getIdDueno()

            int filas = ps.executeUpdate();
            res = (filas > 0);
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar el dueño: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en update: " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    /**
     * Elimina un dueño de la base de datos basado en su id_dueno.
     *
     * @param dueno El objeto Duenos que contiene el id_dueno del dueño a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public boolean delete(Duenos dueno) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Duenos WHERE id_dueno = ?"
            );
            ps.setInt(1, dueno.getIdDueno()); // ✅ Índice correcto (1)

            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar el dueño: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en delete (DuenoDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }


    /**
     * Busca dueños por nombre (búsqueda parcial usando LIKE).
     *
     * @param nombre El nombre o parte del nombre a buscar.
     * @return Una lista de dueños que coinciden con el nombre proporcionado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public ArrayList<Duenos> search(String nombre) throws SQLException {
        ArrayList<Duenos> records = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_dueno, nombre, telefono, correo FROM Duenos WHERE nombre LIKE ?"
            );
            ps.setString(1, "%" + nombre + "%");

            rs = ps.executeQuery();
            while (rs.next()) {
                Duenos dueno = new Duenos();
                dueno.setIdDueno(rs.getInt("id_dueno"));     // ✅ Usar nombre de columna
                dueno.setNombre(rs.getString("nombre"));
                dueno.setTelefono(rs.getString("telefono"));
                dueno.setCorreo(rs.getString("correo"));
                records.add(dueno);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar dueños: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en search (DuenoDAO): " + e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar ResultSet en search (DuenoDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return records;
    }


    /**
     * Obtiene un dueño por su id_dueno.
     *
     * @param id El id_dueno del dueño a buscar.
     * @return Un objeto Duenos si se encuentra, null si no existe.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public Duenos getById(int id) throws SQLException {
        Duenos dueno = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_dueno, nombre, telefono, correo FROM Duenos WHERE id_dueno = ?"
            );
            ps.setInt(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                dueno = new Duenos();
                dueno.setIdDueno(rs.getInt(1));
                dueno.setNombre(rs.getString(2));
                dueno.setTelefono(rs.getString(3));
                dueno.setCorreo(rs.getString(4));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener un dueño por id: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en getById (DuenoDAO): " + e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar ResultSet en getById (DuenoDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return dueno;
    }
}