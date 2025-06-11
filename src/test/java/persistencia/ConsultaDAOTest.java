package persistencia;

import esfe.dominio.Consulta;
import esfe.dominio.Mascota;
import esfe.dominio.Veterinario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ConsultaDAOTest {
    private ConsultaDAO consultaDAO;

    @BeforeEach
    void setUp() {
        consultaDAO = new ConsultaDAO();
    }

    private Consulta create(Consulta consulta) throws SQLException {
        Consulta res = consultaDAO.create(consulta);

        assertNotNull(res, "La consulta creada no debe ser nula.");
        assertTrue(res.getIdConsulta() > 0, "El ID generado debe ser mayor que 0.");
        assertEquals(consulta.getFecha().toLowerCase(), res.getFecha().toLowerCase(), "La fecha debe coincidir.");
        assertEquals(consulta.getMotivo(), res.getMotivo(), "El motivo debe coincidir.");
        assertEquals(consulta.getCosto(), res.getCosto(), "El costo debe coincidir.");
        assertEquals(consulta.getIdMascota(), res.getIdMascota(), "El ID de la mascota debe coincidir.");
        assertEquals(consulta.getIdVeterinario(), res.getIdVeterinario(), "El ID del veterinario debe coincidir.");

        return res;
    }

    private void update(Consulta consulta) throws SQLException {
        consulta.setMotivo(consulta.getMotivo() + "_mod");
        consulta.setCosto(consulta.getCosto() + 10.0f);

        boolean actualizado = consultaDAO.update(consulta);
        assertTrue(actualizado, "La consulta debería actualizarse correctamente.");

        getById(consulta);
    }

    private void getById(Consulta consulta) throws SQLException {
        Consulta res = consultaDAO.getById(consulta.getIdConsulta());

        assertNotNull(res, "La consulta obtenida no debe ser nula.");
        assertEquals(consulta.getIdConsulta(), res.getIdConsulta(), "El ID debe coincidir.");
        assertEquals(consulta.getFecha().toLowerCase(), res.getFecha().toLowerCase(), "La fecha debe coincidir.");
        assertEquals(consulta.getMotivo(), res.getMotivo(), "El motivo debe coincidir.");
        assertEquals(consulta.getCosto(), res.getCosto(), "El costo debe coincidir.");
        assertEquals(consulta.getIdMascota(), res.getIdMascota(), "El ID de la mascota debe coincidir.");
        assertEquals(consulta.getIdVeterinario(), res.getIdVeterinario(), "El ID del veterinario debe coincidir.");
    }

    private void search(Consulta consulta) throws SQLException {
        ArrayList<Consulta> lista = consultaDAO.search(consulta.getMotivo());
        assertFalse(lista.isEmpty(), "La búsqueda no debe devolver una lista vacía.");

        boolean encontrado = false;
        for (Consulta item : lista) {
            if (item.getIdConsulta() == consulta.getIdConsulta()) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "No se encontró la consulta esperada en los resultados.");
    }

    private void delete(Consulta consulta) throws SQLException {
        boolean eliminado = consultaDAO.delete(consulta);
        assertTrue(eliminado, "La consulta debe eliminarse correctamente.");

        Consulta res = consultaDAO.getById(consulta.getIdConsulta());
        assertNull(res, "La consulta eliminada ya no debe existir en la base de datos.");
    }

    @Test
    void testConsultaDAO() throws SQLException {
        // Crear dependencias necesarias
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        Veterinario vet = new Veterinario(0, "VetTest", "Medicina General", "vet" + new Random().nextInt(1000) + "@test.com");
        vet = veterinarioDAO.create(vet);

        MascotaDAO mascotaDAO = new MascotaDAO();
        Mascota mascota = new Mascota(0, "TestMascota", "Gato", 2, 1); // Asegúrate que id_dueno = 1 exista
        mascota = mascotaDAO.create(mascota);

        String motivo = "Chequeo de rutina";
        float costo = 20.0f;
        Date fecha = new Date(System.currentTimeMillis());

        Consulta nuevaConsulta = new Consulta(0, fecha, motivo, costo, mascota.getIdMascota(), vet.getIdVeterinario());

        Consulta creada = create(nuevaConsulta);
        update(creada);
        search(creada);
        delete(creada);
    }
}