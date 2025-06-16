package esfe.presentacion;

import esfe.dominio.Consulta;
import esfe.utils.CUD;
import persistencia.ConsultaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ConsultaReadingForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtSearch;
    private JTable tableConsultas;
    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnDelete;

    private ConsultaDAO consultaDAO;
    private MainForm mainForm;

    public ConsultaReadingForm(MainForm mainForm) {
        this.mainForm = mainForm;
        consultaDAO = new ConsultaDAO();
        setContentPane(mainPanel);
        setModal(true);
        setTitle("Buscar Consultas");
        pack();
        setLocationRelativeTo(mainForm);

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!txtSearch.getText().trim().isEmpty()) {
                    search(txtSearch.getText());
                } else {
                    tableConsultas.setModel(new DefaultTableModel());
                }
            }
        });

        btnCreate.addActionListener(e -> {
            ConsultaWriteForm writeForm = new ConsultaWriteForm(mainForm, CUD.CREATE, new Consulta());
            writeForm.setVisible(true);
            tableConsultas.setModel(new DefaultTableModel());
        });

        btnUpdate.addActionListener(e -> {
            Consulta c = getConsultaFromTable();
            if (c != null) {
                ConsultaWriteForm writeForm = new ConsultaWriteForm(mainForm, CUD.UPDATE, c);
                writeForm.setVisible(true);
                tableConsultas.setModel(new DefaultTableModel());
            }
        });

        btnDelete.addActionListener(e -> {
            Consulta c = getConsultaFromTable();
            if (c != null) {
                ConsultaWriteForm writeForm = new ConsultaWriteForm(mainForm, CUD.DELETE, c);
                writeForm.setVisible(true);
                tableConsultas.setModel(new DefaultTableModel());
            }
        });
    }

    private void search(String query) {
        try {
            ArrayList<Consulta> lista = consultaDAO.search(query);
            createTable(lista);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createTable(ArrayList<Consulta> lista) {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        model.addColumn("ID");
        model.addColumn("Fecha");
        model.addColumn("Motivo");
        model.addColumn("Costo");
        model.addColumn("Mascota");
        model.addColumn("Veterinario");

        tableConsultas.setModel(model);
        Object[] row = new Object[6];

        for (int i = 0; i < lista.size(); i++) {
            Consulta c = lista.get(i);
            row[0] = c.getIdConsulta();
            row[1] = c.getFecha();
            row[2] = c.getMotivo();
            row[3] = c.getCosto();
            row[4] = c.getIdMascota();
            row[5] = c.getIdVeterinario();
            model.addRow(row.clone());
        }

        hideCol(0);
    }

    private void hideCol(int colIndex) {
        tableConsultas.getColumnModel().getColumn(colIndex).setMaxWidth(0);
        tableConsultas.getColumnModel().getColumn(colIndex).setMinWidth(0);
        tableConsultas.getTableHeader().getColumnModel().getColumn(colIndex).setMaxWidth(0);
        tableConsultas.getTableHeader().getColumnModel().getColumn(colIndex).setMinWidth(0);
    }

    private Consulta getConsultaFromTable() {
        try {
            int row = tableConsultas.getSelectedRow();
            if (row != -1) {
                int id = (int) tableConsultas.getValueAt(row, 0);
                return consultaDAO.getById(id);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Seleccionar una fila.", "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}
