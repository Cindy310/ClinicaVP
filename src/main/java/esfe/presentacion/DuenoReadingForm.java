package esfe.presentacion;

import persistencia.DuenosDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import esfe.dominio.Duenos;
import esfe.utils.CUD;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class DuenoReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtName;
    private JButton btnCreate;
    private JTable tableDuenos;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JScrollPane scrollDueno;

    private DuenosDAO duenosDAO;
    private MainForm mainForm;

    public DuenoReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        duenosDAO = new DuenosDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Dueño");
        pack();
        setLocationRelativeTo(mainForm);

        txtName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtName.getText().trim().isEmpty()) {
                    search(txtName.getText());
                } else {
                    tableDuenos.setModel(new DefaultTableModel());
                }
            }
        });

        btnCreate.addActionListener(s -> {
            DuenoWriteForm duenoWriteForm = new DuenoWriteForm(this.mainForm, CUD.CREATE, new Duenos());
            duenoWriteForm.setVisible(true);
            tableDuenos.setModel(new DefaultTableModel());
        });

        btnUpdate.addActionListener(s -> {
            Duenos dueno = getDuenoFromTableRow();
            if (dueno != null) {
                DuenoWriteForm duenoWriteForm = new DuenoWriteForm(this.mainForm, CUD.UPDATE, dueno);
                duenoWriteForm.setVisible(true);
                tableDuenos.setModel(new DefaultTableModel());
            }
        });

        btnDelete.addActionListener(s -> {
            Duenos dueno = getDuenoFromTableRow();
            if (dueno != null) {
                DuenoWriteForm duenoWriteForm = new DuenoWriteForm(this.mainForm, CUD.DELETE, dueno);
                duenoWriteForm.setVisible(true);
                tableDuenos.setModel(new DefaultTableModel());
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Duenos> lista = duenosDAO.search(query);
            createTable(lista);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createTable(ArrayList<Duenos> lista) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Teléfono");
        model.addColumn("Correo");

        tableDuenos.setModel(model);

        Object[] row = null;

        for (int i = 0; i < lista.size(); i++) {
            Duenos d = lista.get(i);
            model.addRow(row);
            model.setValueAt(d.getIdDueno(), i, 0);
            model.setValueAt(d.getNombre(), i, 1);
            model.setValueAt(d.getTelefono(), i, 2);
            model.setValueAt(d.getCorreo(), i, 3);
        }

        hideCol(0);
    }

    private void hideCol(int colIndex) {
        tableDuenos.getColumnModel().getColumn(colIndex).setMaxWidth(0);
        tableDuenos.getColumnModel().getColumn(colIndex).setMinWidth(0);
        tableDuenos.getTableHeader().getColumnModel().getColumn(colIndex).setMaxWidth(0);
        tableDuenos.getTableHeader().getColumnModel().getColumn(colIndex).setMinWidth(0);
    }

    private Duenos getDuenoFromTableRow() {
        Duenos dueno = null;
        try {
            int selectedRow = tableDuenos.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableDuenos.getValueAt(selectedRow, 0);
                dueno = duenosDAO.getById(id);
                if (dueno.getIdDueno() == 0) {
                    JOptionPane.showMessageDialog(null,
                            "No se encontró ningún dueño.",
                            "Validación", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                return dueno;
            } else {
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila de la tabla.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
