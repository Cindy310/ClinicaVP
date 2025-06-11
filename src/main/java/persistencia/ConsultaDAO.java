package persistencia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

import esfe.dominio.Consulta;

public class ConsultaDAO {
    private ConnectionManager conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public ConsultaDAO() {
        conn = ConnectionManager.getInstance();
    }

    /**
     * Crea una nueva consulta veterinaria en la base de datos.
     */
    public Consulta create(Consulta consulta) throws SQLException {
        Consulta res = null;
        PreparedStatement localPs = null;
        try {
            localPs = conn.connect().prepareStatement(
                    "INSERT INTO Consultas (fecha, motivo, costo, id_mascota, id_veterinario) VALUES (?, ?, ?, ?, ?)",
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );
            localPs.setDate(1, consulta.getFecha());
            localPs.setString(2, consulta.getMotivo());
            localPs.setFloat(3, consulta.getCosto());
            localPs.setInt(4, consulta.getIdMascota());
            localPs.setInt(5, consulta.getIdVeterinario());

            int filas = localPs.executeUpdate();

            if (filas != 0) {
                ResultSet generatedKeys = localPs.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    res = getById(idGenerado);
                } else {
                    throw new SQLException("Creating consulta failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al crear la consulta: " + ex.getMessage(), ex);
        } finally {
            if (localPs != null) localPs.close();
            conn.disconnect();
        }
        return res;
    }

    /**
     * Actualiza la información de una consulta existente.
     */
    public boolean update(Consulta consulta) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "UPDATE Consultas SET fecha = ?, motivo = ?, costo = ?, id_mascota = ?, id_veterinario = ? WHERE id_consulta = ?"
            );
            ps.setDate(1, consulta.getFecha());
            ps.setString(2, consulta.getMotivo());
            ps.setFloat(3, consulta.getCosto());
            ps.setInt(4, consulta.getIdMascota());
            ps.setInt(5, consulta.getIdVeterinario());
            ps.setInt(6, consulta.getIdConsulta());

            int filas = ps.executeUpdate();
            res = (filas > 0);
        } catch (SQLException ex) {
            throw new SQLException("Error al actualizar la consulta: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return res;
    }

    /**
     * Elimina una consulta de la base de datos.
     */
    public boolean delete(Consulta consulta) throws SQLException {
        boolean res = false;
        try {
            ps = conn.connect().prepareStatement(
                    "DELETE FROM Consultas WHERE id_consulta = ?"
            );
            ps.setInt(1, consulta.getIdConsulta());

            res = ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new SQLException("Error al eliminar la consulta: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) ps.close();
            conn.disconnect();
        }
        return res;
    }

    /**
     * Busca consultas por motivo (LIKE).
     */
    public ArrayList<Consulta> search(String motivo) throws SQLException {
        ArrayList<Consulta> records = new ArrayList<>();
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_consulta, fecha, motivo, costo, id_mascota, id_veterinario FROM Consultas WHERE motivo LIKE ?"
            );
            ps.setString(1, "%" + motivo + "%");

            rs = ps.executeQuery();
            while (rs.next()) {
                Consulta consulta = new Consulta();
                consulta.setIdConsulta(rs.getInt("id_consulta"));
                consulta.setFecha(rs.getDate("fecha"));
                consulta.setMotivo(rs.getString("motivo"));
                consulta.setCosto(rs.getFloat("costo"));
                consulta.setIdMascota(rs.getInt("id_mascota"));
                consulta.setIdVeterinario(rs.getInt("id_veterinario"));
                records.add(consulta);
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al buscar consultas: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
            conn.disconnect();
        }
        return records;
    }

    /**
     * Obtiene una consulta por su ID.
     */
    public Consulta getById(int id) throws SQLException {
        Consulta consulta = null;
        try {
            ps = conn.connect().prepareStatement(
                    "SELECT id_consulta, fecha, motivo, costo, id_mascota, id_veterinario FROM Consultas WHERE id_consulta = ?"
            );
            ps.setInt(1, id);

            rs = ps.executeQuery();
            if (rs.next()) {
                consulta = new Consulta();
                consulta.setIdConsulta(rs.getInt("id_consulta"));
                consulta.setFecha(rs.getDate("fecha"));
                consulta.setMotivo(rs.getString("motivo"));
                consulta.setCosto(rs.getFloat("costo"));
                consulta.setIdMascota(rs.getInt("id_mascota"));
                consulta.setIdVeterinario(rs.getInt("id_veterinario"));
            }
        } catch (SQLException ex) {
            throw new SQLException("Error al obtener una consulta por ID: " + ex.getMessage(), ex);
        } finally {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
            conn.disconnect();
        }
        return consulta;
    }
}
