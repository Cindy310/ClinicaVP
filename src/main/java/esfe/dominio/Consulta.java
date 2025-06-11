package esfe.dominio;

import java.sql.Date;

public class Consulta {
    private int idConsulta;
    private Date fecha;
    private String motivo;
    private float costo;
    private int idMascota;
    private int idVeterinario;

    public Consulta() {
        // Constructor vacío
    }

    public Consulta(int idConsulta, Date fecha, String motivo, float costo, int idMascota, int idVeterinario) {
        this.idConsulta = idConsulta;
        this.fecha = fecha;
        this.motivo = motivo;
        this.costo = costo;
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }
}
