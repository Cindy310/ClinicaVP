package persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import esfe.dominio.Mascota;

public class MascotaDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;
    public MascotaDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea una nueva mascota en la base de datos.
     *
     * @param mascota El objeto Mascota que contiene la información de la nueva mascota.
     *                Se espera que los campos nombre, especie, edad e idDueno estén establecidos.
     *                El campo idMascota será generado automáticamente por la base de datos.
     * @return El objeto Mascota recién creado, incluyendo el idMascota generado, o null si ocurre un error.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos durante la creación.
     */
    public Mascota create(Mascota mascota) throws SQLException {
        Mascota res = null;
        PreparedStatement localPs = null;
        try {
            localPs = conn.connect().prepareStatement(
                    "INSERT INTO Mascotas (nombre, especie, edad, id_dueno) VALUES (?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            localPs.setString(1, mascota.getNombre());
            localPs.setString(2, mascota.getEspecie());
            localPs.setInt(3, mascota.getEdad());
            localPs.setInt(4, mascota.getIdDueno());

            int affectedRows = localPs.executeUpdate();

            if (affectedRows != 0) {
                ResultSet generatedKeys = localPs.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("Creating mascota failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear la mascota: " + ex.getMessage(), ex);
        } finally {
            if (localPs != null) {
                try {
                    localPs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en create (MascotaDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    /**
     * Actualiza la información de una mascota existente.
     *
     * @param mascota El objeto Mascota con la información actualizada.
     *                Se requiere que el campo idMascota esté correctamente establecido.
     * @return true si la actualización fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public boolean update(Mascota mascota) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Mascotas SET nombre = ?, especie = ?, edad = ?, id_dueno = ? WHERE id_mascota = ?"
            );
            ps.setString(1, mascota.getNombre());
            ps.setString(2, mascota.getEspecie());
            ps.setInt(3, mascota.getEdad());
            ps.setInt(4, mascota.getIdDueno());
            ps.setInt(5, mascota.getIdMascota());

            int filas = ps.executeUpdate();
            res = (filas > 0);
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar la mascota: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en update (MascotaDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    /**
     * Elimina una mascota de la base de datos basado en su idMascota.
     *
     * @param mascota El objeto Mascota que contiene el idMascota de la mascota a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public boolean delete(Mascota mascota) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Mascotas WHERE id_mascota = ?"
            );
            ps.setInt(1, mascota.getIdMascota());

            if (ps.executeUpdate() > 0) {
                res = true;
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar la mascota: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en delete (MascotaDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return res;
    }

    /**
     * Busca mascotas por nombre (búsqueda parcial usando LIKE).
     *
     * @param nombre El nombre o parte del nombre de la mascota a buscar.
     * @return Una lista de mascotas que coinciden con el nombre proporcionado.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public ArrayList<Mascota> search(String nombre) throws SQLException {
        ArrayList<Mascota> records = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_mascota, nombre, especie, edad, id_dueno FROM Mascotas WHERE nombre LIKE ?"
            );
            ps.setString(1, "%" + nombre + "%");

            rs = ps.executeQuery();
            while (rs.next()) {
                Mascota mascota = new Mascota();
                mascota.setIdMascota(rs.getInt("id_mascota"));
                mascota.setNombre(rs.getString("nombre"));
                mascota.setEspecie(rs.getString("especie"));
                mascota.setEdad(rs.getInt("edad"));
                mascota.setIdDueno(rs.getInt("id_dueno"));
                records.add(mascota);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar mascotas: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en search (MascotaDAO): " + e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar ResultSet en search (MascotaDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return records;
    }

    /**
     * Obtiene una mascota por su idMascota.
     *
     * @param id El idMascota de la mascota a buscar.
     * @return Un objeto Mascota si se encuentra, null si no existe.
     * @throws SQLException Si ocurre un error al interactuar con la base de datos.
     */
    public Mascota getById(int id) throws SQLException {
        Mascota mascota = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_mascota, nombre, especie, edad, id_dueno FROM Mascotas WHERE id_mascota = ?"
            );
            ps.setInt(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                mascota = new Mascota();
                mascota.setIdMascota(rs.getInt(1));
                mascota.setNombre(rs.getString(2));
                mascota.setEspecie(rs.getString(3));
                mascota.setEdad(rs.getInt(4));
                mascota.setIdDueno(rs.getInt(5));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener una mascota por ID: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar PreparedStatement en getById (MascotaDAO): " + e.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar ResultSet en getById (MascotaDAO): " + e.getMessage());
                }
            }
            conn.disconnect();
        }
        return mascota;
    }
}
