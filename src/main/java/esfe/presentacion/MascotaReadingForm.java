package esfe.presentacion;

import persistencia.MascotaDAO;
import esfe.dominio.Mascota;
import esfe.utils.CUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class MascotaReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombre;
    private JButton btnCreate;
    private JTable tableMascotas;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JLabel lbNombre;
    private JScrollPane scrollMascotas;

    private MascotaDAO mascotaDAO;
    private MainForm mainForm;

    public MascotaReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        mascotaDAO = new MascotaDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Mascota");
        pack();
        setLocationRelativeTo(mainForm);

        txtNombre.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtNombre.getText().trim().isEmpty()) {
                    search(txtNombre.getText());
                } else {
                    tableMascotas.setModel(new DefaultTableModel());
                }
            }
        });

        btnCreate.addActionListener(s -> {
            MascotaWriteForm form = new MascotaWriteForm(this.mainForm, CUD.CREATE, new Mascota());
            form.setVisible(true);
            tableMascotas.setModel(new DefaultTableModel());
        });

        btnUpdate.addActionListener(s -> {
            Mascota m = getMascotaFromTableRow();
            if (m != null) {
                MascotaWriteForm form = new MascotaWriteForm(this.mainForm, CUD.UPDATE, m);
                form.setVisible(true);
                tableMascotas.setModel(new DefaultTableModel());
            }
        });

        btnDelete.addActionListener(s -> {
            Mascota m = getMascotaFromTableRow();
            if (m != null) {
                MascotaWriteForm form = new MascotaWriteForm(this.mainForm, CUD.DELETE, m);
                form.setVisible(true);
                tableMascotas.setModel(new DefaultTableModel());
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Mascota> lista = mascotaDAO.search(query);
            createTable(lista);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createTable(ArrayList<Mascota> lista) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Especie");
        model.addColumn("Edad");
        model.addColumn("ID Dueño");

        tableMascotas.setModel(model);
        Object[] row = null;

        for (int i = 0; i < lista.size(); i++) {
            Mascota m = lista.get(i);
            model.addRow(row);
            model.setValueAt(m.getIdMascota(), i, 0);
            model.setValueAt(m.getNombre(), i, 1);
            model.setValueAt(m.getEspecie(), i, 2);
            model.setValueAt(m.getEdad(), i, 3);
            model.setValueAt(m.getIdDueno(), i, 4);
        }

        hideCol(0);
    }

    private void hideCol(int index) {
        tableMascotas.getColumnModel().getColumn(index).setMaxWidth(0);
        tableMascotas.getColumnModel().getColumn(index).setMinWidth(0);
        tableMascotas.getTableHeader().getColumnModel().getColumn(index).setMaxWidth(0);
        tableMascotas.getTableHeader().getColumnModel().getColumn(index).setMinWidth(0);
    }

    private Mascota getMascotaFromTableRow() {
        try {
            int fila = tableMascotas.getSelectedRow();
            if (fila != -1) {
                int id = (int) tableMascotas.getValueAt(fila, 0);
                Mascota m = mascotaDAO.getById(id);
                if (m.getIdMascota() == 0) {
                    JOptionPane.showMessageDialog(null, "No se encontró ninguna mascota.", "Validación", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                return m;
            } else {
                JOptionPane.showMessageDialog(null, "Seleccionar una fila.", "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
