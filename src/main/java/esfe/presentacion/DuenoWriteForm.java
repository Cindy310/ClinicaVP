package esfe.presentacion;

import persistencia.DuenosDAO;
import esfe.dominio.Duenos;
import esfe.utils.CUD;

import javax.swing.*;

public class DuenoWriteForm extends JDialog {
    private JPanel mainPanel;
    private JTextField txtNombre;
    private JTextField txtTelefono;
    private JTextField txtCorreo;
    private JButton btnOk;
    private JButton btnCancel;

    private DuenosDAO duenosDAO;
    private MainForm mainForm;
    private CUD cud;
    private Duenos en;

    public DuenoWriteForm(MainForm mainForm, CUD cud, Duenos dueno) {
        this.cud = cud;
        this.en = dueno;
        this.mainForm = mainForm;
        duenosDAO = new DuenosDAO();
        setContentPane(mainPanel);
        setModal(true);
        init();
        pack();
        setLocationRelativeTo(mainForm);

        btnCancel.addActionListener(s -> this.dispose());
        btnOk.addActionListener(s -> ok());
    }

    private void init() {
        switch (this.cud) {
            case CREATE:
                setTitle("Crear Dueño");
                btnOk.setText("Guardar");
                break;
            case UPDATE:
                setTitle("Modificar Dueño");
                btnOk.setText("Guardar");
                break;
            case DELETE:
                setTitle("Eliminar Dueño");
                btnOk.setText("Eliminar");
                break;
        }

        setValuesControls(this.en);
    }

    private void setValuesControls(Duenos dueno) {
        txtNombre.setText(dueno.getNombre());
        txtTelefono.setText(dueno.getTelefono());
        txtCorreo.setText(dueno.getCorreo());

        if (this.cud == CUD.DELETE) {
            txtNombre.setEditable(false);
            txtTelefono.setEditable(false);
            txtCorreo.setEditable(false);
        }
    }

    private boolean getValuesControls() {
        boolean res = false;

        if (txtNombre.getText().trim().isEmpty()) {
            return res;
        } else if (txtTelefono.getText().trim().isEmpty()) {
            return res;
        } else if (txtCorreo.getText().trim().isEmpty()) {
            return res;
        } else if (this.cud != CUD.CREATE && this.en.getIdDueno() == 0) {
            return res;
        }

        res = true;

        this.en.setNombre(txtNombre.getText().trim());
        this.en.setTelefono(txtTelefono.getText().trim());
        this.en.setCorreo(txtCorreo.getText().trim());

        return res;
    }

    private void ok() {
        try {
            boolean res = getValuesControls();

            if (res) {
                boolean r = false;
                switch (this.cud) {
                    case CREATE:
                        Duenos creado = duenosDAO.create(this.en);
                        if (creado.getIdDueno() > 0) {
                            r = true;
                        }
                        break;
                    case UPDATE:
                        r = duenosDAO.update(this.en);
                        break;
                    case DELETE:
                        r = duenosDAO.delete(this.en);
                        break;
                }

                if (r) {
                    JOptionPane.showMessageDialog(null,
                            "Transacción realizada exitosamente",
                            "Información", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No se logró realizar ninguna acción",
                            "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Los campos con * son obligatorios",
                        "Validación", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    ex.getMessage(),
                    "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}