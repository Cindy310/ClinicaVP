package persistencia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import esfe.dominio.Veterinario;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class VeterinarioDAOTest {
    private VeterinarioDAO veterinarioDAO;

    @BeforeEach
    void setUp() {
        veterinarioDAO = new VeterinarioDAO();
    }

    private Veterinario create(Veterinario veterinario) throws SQLException {
        Veterinario res = veterinarioDAO.create(veterinario);

        assertNotNull(res, "El veterinario creado no debe ser nulo.");
        assertTrue(res.getIdVeterinario() > 0, "El ID generado debe ser mayor que 0.");
        assertEquals(veterinario.getNombre(), res.getNombre(), "El nombre debe coincidir.");
        assertEquals(veterinario.getEspecialidad(), res.getEspecialidad(), "La especialidad debe coincidir.");
        assertEquals(veterinario.getCorreo(), res.getCorreo(), "El correo debe coincidir.");

        return res;
    }

    private void update(Veterinario veterinario) throws SQLException {
        veterinario.setNombre(veterinario.getNombre() + "_mod");
        veterinario.setEspecialidad("Cirugía General");
        veterinario.setCorreo("mod_" + veterinario.getCorreo());

        boolean actualizado = veterinarioDAO.update(veterinario);
        assertTrue(actualizado, "El veterinario debería actualizarse correctamente.");

        getById(veterinario);
    }

    private void getById(Veterinario veterinario) throws SQLException {
        Veterinario res = veterinarioDAO.getById(veterinario.getIdVeterinario());

        assertNotNull(res, "El veterinario obtenido no debe ser nulo.");
        assertEquals(veterinario.getIdVeterinario(), res.getIdVeterinario(), "El ID debe coincidir.");
        assertEquals(veterinario.getNombre(), res.getNombre(), "El nombre debe coincidir.");
        assertEquals(veterinario.getEspecialidad(), res.getEspecialidad(), "La especialidad debe coincidir.");
        assertEquals(veterinario.getCorreo(), res.getCorreo(), "El correo debe coincidir.");
    }

    private void search(Veterinario veterinario) throws SQLException {
        ArrayList<Veterinario> lista = veterinarioDAO.search(veterinario.getNombre());
        assertFalse(lista.isEmpty(), "La búsqueda no debe devolver una lista vacía.");

        boolean encontrado = false;
        for (Veterinario item : lista) {
            if (item.getIdVeterinario() == veterinario.getIdVeterinario()) {
                encontrado = true;
                break;
            }
        }

        assertTrue(encontrado, "No se encontró el veterinario esperado en los resultados.");
    }

    private void delete(Veterinario veterinario) throws SQLException {
        boolean eliminado = veterinarioDAO.delete(veterinario);
        assertTrue(eliminado, "El veterinario debe eliminarse correctamente.");

        Veterinario res = veterinarioDAO.getById(veterinario.getIdVeterinario());
        assertNull(res, "El veterinario eliminado ya no debe existir en la base de datos.");
    }

    @Test
    void testVeterinarioDAO() throws SQLException {
        Random rand = new Random();
        int randomNum = rand.nextInt(1000) + 1;
        String correo = "vet" + randomNum + "@test.com";

        Veterinario nuevoVeterinario = new Veterinario(0, "Vet Test", "Medicina Interna", correo);

        Veterinario creado = create(nuevoVeterinario);
        update(creado);
        search(creado);
        delete(creado);
    }
}
