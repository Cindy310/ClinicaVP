package esfe.presentacion;

import esfe.dominio.Consulta;
import esfe.dominio.Mascota;
import esfe.dominio.Veterinario;
import persistencia.ConsultaDAO;
import persistencia.MascotaDAO;
import persistencia.VeterinarioDAO;
import esfe.utils.CUD;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class ConsultaWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtFecha;
    private JTextField txtMotivo;
    private JTextField txtCosto;
    private JComboBox<String> cbMascota;
    private JComboBox<String> cbVeterinario;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private MainForm mainForm;
    private CUD modo;
    private Consulta consulta;
    private ConsultaDAO consultaDAO;
    private MascotaDAO mascotaDAO = new MascotaDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();

    public ConsultaWriteForm(MainForm mainForm, CUD modo, Consulta consulta) {
        this.mainForm = mainForm;
        this.modo = modo;
        this.consulta = consulta;
        this.consultaDAO = new ConsultaDAO();

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Formulario de Consulta");
        pack();
        setLocationRelativeTo(mainForm);

        cargarCombos();  // 🔄 cargar datos en los ComboBox
        cargarDatos();   // 📝 cargar los datos si es modo UPDATE o DELETE

        btnGuardar.addActionListener(e -> {
            switch (modo) {
                case CREATE -> crearConsulta();
                case UPDATE -> actualizarConsulta();
                case DELETE -> eliminarConsulta();
            }
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarCombos() {
        cbMascota.removeAllItems();
        for (Mascota m : mascotaDAO.getAll()) {
            cbMascota.addItem(m.getId() + " - " + m.getNombre());
        }

        cbVeterinario.removeAllItems();
        for (Veterinario v : veterinarioDAO.getAll()) {
            cbVeterinario.addItem(v.getId() + " - " + v.getNombre());
        }
    }

    private void cargarDatos() {
        if (modo != CUD.CREATE && consulta != null) {
            if (consulta.getFecha() != null) {
                txtFecha.setText(consulta.getFecha().toString());
            } else {
                txtFecha.setText("");
            }

            txtMotivo.setText(consulta.getMotivo() != null ? consulta.getMotivo() : "");
            txtCosto.setText(String.valueOf(consulta.getCosto()));

            seleccionarItemCombo(cbMascota, consulta.getIdMascota());
            seleccionarItemCombo(cbVeterinario, consulta.getIdVeterinario());

            if (modo == CUD.DELETE) {
                txtFecha.setEditable(false);
                txtMotivo.setEditable(false);
                txtCosto.setEditable(false);
                cbMascota.setEnabled(false);
                cbVeterinario.setEnabled(false);
                btnGuardar.setText("Eliminar");
            } else {
                btnGuardar.setText("Actualizar");
            }
        } else {
            btnGuardar.setText("Guardar");
        }
    }

    private void seleccionarItemCombo(JComboBox<String> combo, int idBuscado) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.startsWith(idBuscado + " -")) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void crearConsulta() {
        try {
            Consulta nueva = new Consulta();
            try {
                String fechaTexto = txtFecha.getText().trim(); // formato YYYY-MM-DD
                Date fecha = Date.valueOf(fechaTexto);
                nueva.setFecha(fecha);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha inválido. Usa Año-Mes-Día (ej: 2025-06-10)",
                        "Error de fecha",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            nueva.setMotivo(txtMotivo.getText());
            nueva.setCosto(Float.parseFloat(txtCosto.getText()));
            nueva.setIdMascota(obtenerIdDesdeCombo(cbMascota));
            nueva.setIdVeterinario(obtenerIdDesdeCombo(cbVeterinario));

            consultaDAO.create(nueva); // llamado al DAO

            JOptionPane.showMessageDialog(this, "Consulta registrada correctamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarConsulta() {
        try {
            consulta.setFecha(Date.valueOf(txtFecha.getText()));
            consulta.setMotivo(txtMotivo.getText());
            consulta.setCosto(Float.parseFloat(txtCosto.getText()));
            consulta.setIdMascota(obtenerIdDesdeCombo(cbMascota));
            consulta.setIdVeterinario(obtenerIdDesdeCombo(cbVeterinario));
            consultaDAO.update(consulta);
            JOptionPane.showMessageDialog(this, "Consulta actualizada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarConsulta() {
        try {
            consultaDAO.delete(consulta);
            JOptionPane.showMessageDialog(this, "Consulta eliminada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int obtenerIdDesdeCombo(JComboBox<String> combo) {
        String item = (String) combo.getSelectedItem();
        return Integer.parseInt(item.split(" - ")[0].trim());
    }
}
