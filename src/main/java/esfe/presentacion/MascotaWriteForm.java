package esfe.presentacion;

import esfe.dominio.Duenos;
import esfe.dominio.Mascota;
import esfe.utils.CUD;
import persistencia.DuenosDAO;
import persistencia.MascotaDAO;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MascotaWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombre;
    private JTextField txtEspecie;
    private JTextField txtEdad;
    private JComboBox<Duenos> cbDueno;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private MainForm mainForm;
    private CUD modo;
    private Mascota mascota;
    private MascotaDAO mascotaDAO;
    private DuenosDAO duenosDAO;

    public MascotaWriteForm(MainForm mainForm, CUD modo, Mascota mascota) {
        this.mainForm = mainForm;
        this.modo = modo;
        this.mascota = mascota;
        this.mascotaDAO = new MascotaDAO();
        this.duenosDAO = new DuenosDAO();

        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        mainPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Especie:"), gbc);
        txtEspecie = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(txtEspecie, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Edad:"), gbc);
        txtEdad = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(txtEdad, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Dueño:"), gbc);
        cbDueno = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(cbDueno, gbc);

        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        JPanel panelBotones = new JPanel();
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        mainPanel.add(panelBotones, gbc);

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Formulario Mascota");
        pack();
        setLocationRelativeTo(mainForm);

        cargarDuenos();  // Cargar dueños al ComboBox
        cargarDatos();   // Cargar datos de la mascota si aplica

        btnGuardar.addActionListener(e -> {
            if (modo == CUD.CREATE) {
                crearMascota();
            } else if (modo == CUD.UPDATE) {
                actualizarMascota();
            } else if (modo == CUD.DELETE) {
                eliminarMascota();
            }
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarDuenos() {
        try {
            ArrayList<Duenos> lista = duenosDAO.getAll();
            for (Duenos d : lista) {
                cbDueno.addItem(d);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar dueños: " + ex.getMessage());
        }
    }

    private void cargarDatos() {
        if (modo != CUD.CREATE && mascota != null) {
            txtNombre.setText(mascota.getNombre());
            txtEspecie.setText(mascota.getEspecie());
            txtEdad.setText(String.valueOf(mascota.getEdad()));

            // Selecciona el dueño actual
            for (int i = 0; i < cbDueno.getItemCount(); i++) {
                if (cbDueno.getItemAt(i).getIdDueno() == mascota.getIdDueno()) {
                    cbDueno.setSelectedIndex(i);
                    break;
                }
            }

            if (modo == CUD.DELETE) {
                txtNombre.setEditable(false);
                txtEspecie.setEditable(false);
                txtEdad.setEditable(false);
                cbDueno.setEnabled(false);
                btnGuardar.setText("Eliminar");
            } else {
                btnGuardar.setText("Actualizar");
            }
        } else {
            btnGuardar.setText("Guardar");
        }
    }

    private void crearMascota() {
        try {
            Mascota nueva = new Mascota();
            nueva.setNombre(txtNombre.getText());
            nueva.setEspecie(txtEspecie.getText());
            nueva.setEdad(Integer.parseInt(txtEdad.getText()));
            nueva.setIdDueno(((Duenos) cbDueno.getSelectedItem()).getIdDueno());
            mascotaDAO.create(nueva);
            JOptionPane.showMessageDialog(this, "Mascota registrada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarMascota() {
        try {
            mascota.setNombre(txtNombre.getText());
            mascota.setEspecie(txtEspecie.getText());
            mascota.setEdad(Integer.parseInt(txtEdad.getText()));
            mascota.setIdDueno(((Duenos) cbDueno.getSelectedItem()).getIdDueno());
            mascotaDAO.update(mascota);
            JOptionPane.showMessageDialog(this, "Mascota actualizada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMascota() {
        try {
            mascotaDAO.delete(mascota);
            JOptionPane.showMessageDialog(this, "Mascota eliminada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
