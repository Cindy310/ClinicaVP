package persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import esfe.dominio.Duenos;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DuenosDAOTest {
    private DuenosDAO duenosDAO;

    @BeforeEach
    void setUp() {
        duenosDAO = new DuenosDAO();
    }

    private Duenos create(Duenos dueno) throws SQLException {
        Duenos res = duenosDAO.create(dueno);

        assertNotNull(res, "El dueño creado no debe ser nulo.");
        assertTrue(res.getIdDueno() > 0, "El ID generado debe ser mayor que 0.");
        assertEquals(dueno.getNombre(), res.getNombre(), "El nombre debe coincidir.");
        assertEquals(dueno.getTelefono(), res.getTelefono(), "El teléfono debe coincidir.");
        assertEquals(dueno.getCorreo(), res.getCorreo(), "El correo debe coincidir.");

        return res;
    }

    private void update(Duenos dueno) throws SQLException {
        dueno.setNombre(dueno.getNombre() + "_mod");
        dueno.setTelefono("8888-8888");
        dueno.setCorreo("mod_" + dueno.getCorreo());

        boolean actualizado = duenosDAO.update(dueno);
        assertTrue(actualizado, "El dueño debería actualizarse correctamente.");

        getById(dueno);
    }

    private void getById(Duenos dueno) throws SQLException {
        Duenos res = duenosDAO.getById(dueno.getIdDueno());

        assertNotNull(res, "El dueño obtenido no debe ser nulo.");
        assertEquals(dueno.getIdDueno(), res.getIdDueno(), "El ID debe coincidir.");
        assertEquals(dueno.getNombre(), res.getNombre(), "El nombre debe coincidir.");
        assertEquals(dueno.getTelefono(), res.getTelefono(), "El teléfono debe coincidir.");
        assertEquals(dueno.getCorreo(), res.getCorreo(), "El correo debe coincidir.");
    }

    private void search(Duenos dueno) throws SQLException {
        ArrayList<Duenos> lista = duenosDAO.search(dueno.getNombre());
        assertFalse(lista.isEmpty(), "La búsqueda no debe devolver una lista vacía.");

        boolean encontrado = false;
        for (Duenos item : lista) {
            if (item.getIdDueno() == dueno.getIdDueno()) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "No se encontró el dueño esperado en los resultados.");
    }

    private void delete(Duenos dueno) throws SQLException {
        boolean eliminado = duenosDAO.delete(dueno);
        assertTrue(eliminado, "El dueño debe eliminarse correctamente.");

        Duenos res = duenosDAO.getById(dueno.getIdDueno());
        assertNull(res, "El dueño eliminado ya no debe existir en la base de datos.");
    }

    @Test
    void testDuenosDAO() throws SQLException {
        Random rand = new Random();
        int randomNum = rand.nextInt(1000) + 1;
        String correo = "dueno" + randomNum + "@test.com";

        Duenos nuevoDueno = new Duenos(0, "Test Dueño", "7777-7777", correo);

        Duenos creado = create(nuevoDueno);
        update(creado);
        search(creado);
        delete(creado);
    }
}