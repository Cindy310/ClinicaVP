package esfe.presentacion;

import persistencia.VeterinarioDAO;
import esfe.dominio.Veterinario;
import esfe.utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class VeterinarioReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombre;
    private JTable tableVeterinarios;
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;

    private MainForm mainForm;
    private VeterinarioDAO veterinarioDAO;

    public VeterinarioReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        this.veterinarioDAO = new VeterinarioDAO();

        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Veterinario");
        pack();
        setLocationRelativeTo(mainForm);

        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtNombre.getText().trim().isEmpty()) {
                    buscar(txtNombre.getText());
                } else {
                    tableVeterinarios.setModel(new DefaultTableModel());
                }
            }
        });

        btnCreate.addActionListener(e -> {
            VeterinarioWriteForm writeForm = new VeterinarioWriteForm(mainForm, CUD.CREATE, new Veterinario());
            writeForm.setVisible(true);
            tableVeterinarios.setModel(new DefaultTableModel());
        });

        btnUpdate.addActionListener(e -> {
            Veterinario vet = getVeterinarioFromTable();
            if (vet != null) {
                VeterinarioWriteForm writeForm = new VeterinarioWriteForm(mainForm, CUD.UPDATE, vet);
                writeForm.setVisible(true);
                tableVeterinarios.setModel(new DefaultTableModel());
            }
        });

        btnDelete.addActionListener(e -> {
            Veterinario vet = getVeterinarioFromTable();
            if (vet != null) {
                VeterinarioWriteForm writeForm = new VeterinarioWriteForm(mainForm, CUD.DELETE, vet);
                writeForm.setVisible(true);
                tableVeterinarios.setModel(new DefaultTableModel());
            }
        });
    }

    private void buscar(String nombre) {
        try {
            ArrayList<Veterinario> lista = veterinarioDAO.search(nombre);
            crearTabla(lista);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void crearTabla(ArrayList<Veterinario> lista) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Especialidad");
        model.addColumn("Correo");

        tableVeterinarios.setModel(model);

        for (Veterinario v : lista) {
            model.addRow(new Object[]{
                    v.getIdVeterinario(),
                    v.getNombre(),
                    v.getEspecialidad(),
                    v.getCorreo()
            });
        }

        ocultarColumna(0);
    }

    private void ocultarColumna(int index) {
        tableVeterinarios.getColumnModel().getColumn(index).setMaxWidth(0);
        tableVeterinarios.getColumnModel().getColumn(index).setMinWidth(0);
        tableVeterinarios.getTableHeader().getColumnModel().getColumn(index).setMaxWidth(0);
        tableVeterinarios.getTableHeader().getColumnModel().getColumn(index).setMinWidth(0);
    }

    private Veterinario getVeterinarioFromTable() {
        try {
            int row = tableVeterinarios.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una fila", "Atención", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            int id = (int) tableVeterinarios.getValueAt(row, 0);
            return veterinarioDAO.getById(id);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
