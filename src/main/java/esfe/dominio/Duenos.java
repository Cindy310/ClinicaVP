package esfe.dominio;

public class Duenos {
    private int idDueno;
    private String nombre;
    private String telefono;
    private String correo;

    public Duenos() {
        // Constructor vacío
    }

    public Duenos(int idDueno, String nombre, String telefono, String correo) {
        this.idDueno = idDueno;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getIdDueno() {
        return idDueno;
    }

    public void setIdDueno(int idDueno) {
        this.idDueno = idDueno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}