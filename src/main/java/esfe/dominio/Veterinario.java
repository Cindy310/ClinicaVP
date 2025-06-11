package esfe.dominio;

public class Veterinario {
    private int idVeterinario;
    private String nombre;
    private String especialidad;
    private String correo;

    public Veterinario() {
        // Constructor vacío
    }

    public Veterinario(int idVeterinario, String nombre, String especialidad, String correo) {
        this.idVeterinario = idVeterinario;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.correo = correo;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
