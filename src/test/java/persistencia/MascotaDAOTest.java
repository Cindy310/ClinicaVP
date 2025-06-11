package persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import esfe.dominio.Mascota;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MascotaDAOTest {
    private MascotaDAO mascotaDAO;

    @BeforeEach
    void setUp() {
        mascotaDAO = new MascotaDAO();
    }

    private Mascota create(Mascota mascota) throws SQLException {
        Mascota res = mascotaDAO.create(mascota);

        assertNotNull(res, "La mascota creada no debe ser nula.");
        assertTrue(res.getIdMascota() > 0, "El ID generado debe ser mayor que 0.");
        assertEquals(mascota.getNombre(), res.getNombre(), "El nombre debe coincidir.");
        assertEquals(mascota.getEspecie(), res.getEspecie(), "La especie debe coincidir.");
        assertEquals(mascota.getEdad(), res.getEdad(), "La edad debe coincidir.");
        assertEquals(mascota.getIdDueno(), res.getIdDueno(), "El ID del dueño debe coincidir.");

        return res;
    }

    private void update(Mascota mascota) throws SQLException {
        mascota.setNombre(mascota.getNombre() + "_mod");
        mascota.setEspecie("EspecieModificada");
        mascota.setEdad(mascota.getEdad() + 1);

        boolean actualizado = mascotaDAO.update(mascota);
        assertTrue(actualizado, "La mascota debería actualizarse correctamente.");

        getById(mascota);
    }

    private void getById(Mascota mascota) throws SQLException {
        Mascota res = mascotaDAO.getById(mascota.getIdMascota());

        assertNotNull(res, "La mascota obtenida no debe ser nula.");
        assertEquals(mascota.getIdMascota(), res.getIdMascota(), "El ID debe coincidir.");
        assertEquals(mascota.getNombre(), res.getNombre(), "El nombre debe coincidir.");
        assertEquals(mascota.getEspecie(), res.getEspecie(), "La especie debe coincidir.");
        assertEquals(mascota.getEdad(), res.getEdad(), "La edad debe coincidir.");
        assertEquals(mascota.getIdDueno(), res.getIdDueno(), "El ID del dueño debe coincidir.");
    }

    private void search(Mascota mascota) throws SQLException {
        ArrayList<Mascota> lista = mascotaDAO.search(mascota.getNombre());
        assertFalse(lista.isEmpty(), "La búsqueda no debe devolver una lista vacía.");

        boolean encontrado = false;
        for (Mascota item : lista) {
            if (item.getIdMascota() == mascota.getIdMascota()) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "No se encontró la mascota esperada en los resultados.");
    }

    private void delete(Mascota mascota) throws SQLException {
        boolean eliminado = mascotaDAO.delete(mascota);
        assertTrue(eliminado, "La mascota debe eliminarse correctamente.");

        Mascota res = mascotaDAO.getById(mascota.getIdMascota());
        assertNull(res, "La mascota eliminada ya no debe existir en la base de datos.");
    }

    @Test
    void testMascotaDAO() throws SQLException {
        Random rand = new Random();
        int randomNum = rand.nextInt(1000) + 1;
        String nombre = "MascotaTest" + randomNum;

        // NOTA: Asegúrate de que exista un dueño con id_dueno = 1 en la base de datos.
        Mascota nuevaMascota = new Mascota(0, nombre, "Perro", 3, 1);

        Mascota creada = create(nuevaMascota);
        update(creada);
        search(creada);
        delete(creada);
    }
}
