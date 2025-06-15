package esfe.presentacion;

import esfe.dominio.Veterinario;
import esfe.utils.CUD;
import persistencia.VeterinarioDAO;

import javax.swing.*;
import java.awt.*;

public class VeterinarioWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombre;
    private JTextField txtEspecialidad;
    private JTextField txtCorreo;
    private JButton btnGuardar;
    private JButton btnCancelar;

    private MainForm mainForm;
    private CUD modo;
    private Veterinario veterinario;
    private VeterinarioDAO veterinarioDAO;

    public VeterinarioWriteForm(MainForm mainForm, CUD modo, Veterinario veterinario) {
        this.mainForm = mainForm;
        this.modo = modo;
        this.veterinario = veterinario;
        this.veterinarioDAO = new VeterinarioDAO();

        // Crear layout principal
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtNombre, gbc);

        // Especialidad
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Especialidad:"), gbc);
        txtEspecialidad = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtEspecialidad, gbc);

        // Correo
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Correo:"), gbc);
        txtCorreo = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtCorreo, gbc);

        // Botones
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnGuardar);
        btnPanel.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        mainPanel.add(btnPanel, gbc);

        // Configuración final
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Formulario Veterinario");
        pack();
        setLocationRelativeTo(mainForm);

        cargarDatos();

        // Eventos
        btnGuardar.addActionListener(e -> {
            if (modo == CUD.CREATE) {
                crearVeterinario();
            } else if (modo == CUD.UPDATE) {
                actualizarVeterinario();
            } else if (modo == CUD.DELETE) {
                eliminarVeterinario();
            }
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    private void cargarDatos() {
        if (modo != CUD.CREATE && veterinario != null) {
            txtNombre.setText(veterinario.getNombre());
            txtEspecialidad.setText(veterinario.getEspecialidad());
            txtCorreo.setText(veterinario.getCorreo());

            if (modo == CUD.DELETE) {
                txtNombre.setEditable(false);
                txtEspecialidad.setEditable(false);
                txtCorreo.setEditable(false);
                btnGuardar.setText("Eliminar");
            } else {
                btnGuardar.setText("Actualizar");
            }
        } else {
            btnGuardar.setText("Guardar");
        }
    }

    private void crearVeterinario() {
        try {
            Veterinario nuevo = new Veterinario();
            nuevo.setNombre(txtNombre.getText());
            nuevo.setEspecialidad(txtEspecialidad.getText());
            nuevo.setCorreo(txtCorreo.getText());

            veterinarioDAO.create(nuevo);
            JOptionPane.showMessageDialog(this, "Veterinario registrado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarVeterinario() {
        try {
            veterinario.setNombre(txtNombre.getText());
            veterinario.setEspecialidad(txtEspecialidad.getText());
            veterinario.setCorreo(txtCorreo.getText());

            veterinarioDAO.update(veterinario);
            JOptionPane.showMessageDialog(this, "Veterinario actualizado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarVeterinario() {
        try {
            veterinarioDAO.delete(veterinario);
            JOptionPane.showMessageDialog(this, "Veterinario eliminado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
