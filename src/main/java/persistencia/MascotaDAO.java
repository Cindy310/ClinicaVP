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
            if (localPs != null) try { localPs.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en create: " + e.getMessage());
            }
            conn.disconnect();
        }
        return res;
    }

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

            res = (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar la mascota: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en update: " + e.getMessage());
            }
            conn.disconnect();
        }
        return res;
    }

    public boolean delete(Mascota mascota) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Mascotas WHERE id_mascota = ?"
            );
            ps.setInt(1, mascota.getIdMascota());

            res = (ps.executeUpdate() > 0);
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar la mascota: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en delete: " + e.getMessage());
            }
            conn.disconnect();
        }
        return res;
    }

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
                mascota.setIdMascota(rs.getInt("id_mascota"));
                mascota.setNombre(rs.getString("nombre"));
                mascota.setEspecie(rs.getString("especie"));
                mascota.setEdad(rs.getInt("edad"));
                mascota.setIdDueno(rs.getInt("id_dueno"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener una mascota por ID: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en getById: " + e.getMessage());
            }
            if (rs != null) try { rs.close(); } catch (SQLException e) {
                System.err.println("Error al cerrar ResultSet en getById: " + e.getMessage());
            }
            conn.disconnect();
        }
        return mascota;
    }

    // ✅ Método adicional para cargar todas las mascotas (para ComboBox)
    public ArrayList<Mascota> getAll() {
        ArrayList<Mascota> mascotas = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_mascota, nombre, especie, edad, id_dueno FROM Mascotas"
            );
            rs = ps.executeQuery();

            while (rs.next()) {
                Mascota mascota = new Mascota();
                mascota.setIdMascota(rs.getInt("id_mascota"));
                mascota.setNombre(rs.getString("nombre"));
                mascota.setEspecie(rs.getString("especie"));
                mascota.setEdad(rs.getInt("edad"));
                mascota.setIdDueno(rs.getInt("id_dueno"));
                mascotas.add(mascota);
            }
        } catch (SQLException ex) {
            System.err.println("Error al obtener todas las mascotas: " + ex.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (rs != null) rs.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos en getAll: " + e.getMessage());
            }
            try {
                conn.disconnect();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return mascotas;
    }
}
