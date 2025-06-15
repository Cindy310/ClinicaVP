package esfe.presentacion;

import javax.swing.*;
import esfe.dominio.User;

public class MainForm extends JFrame {

    private User userAutenticate;
    private JPanel mainPanel;
    private JButton btnConsultas;
    private JButton btnMascotas;
    private JButton btnVeterinarios;
    private JButton btnDuenos;

    public User getUserAutenticate() {
        return userAutenticate;
    }

    public void setUserAutenticate(User userAutenticate) {
        this.userAutenticate = userAutenticate;
    }

    public MainForm() {
        setTitle("Sistema en java de escritorio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(mainPanel);
        createMenu();

        btnDuenos.addActionListener(e -> {
            DuenoReadingForm form = new DuenoReadingForm(this);
            form.setVisible(true);
        });

        btnMascotas.addActionListener(e -> {
            MascotaReadingForm mascotaForm = new MascotaReadingForm(this);
            mascotaForm.setVisible(true);
        });

    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuPerfil = new JMenu("Perfil");
        menuBar.add(menuPerfil);

        JMenuItem itemChangePassword = new JMenuItem("Cambiar contraseña");
        menuPerfil.add(itemChangePassword);
        itemChangePassword.addActionListener(e -> {
            ChangePasswordForm changePassword = new ChangePasswordForm(this);
            changePassword.setVisible(true);
        });

        JMenuItem itemChangeUser = new JMenuItem("Cambiar de usuario");
        menuPerfil.add(itemChangeUser);
        itemChangeUser.addActionListener(e -> {
            LoginForm loginForm = new LoginForm(this);
            loginForm.setVisible(true);
        });

        JMenuItem itemSalir = new JMenuItem("Salir");
        menuPerfil.add(itemSalir);
        itemSalir.addActionListener(e -> System.exit(0));

        JMenu menuMantenimiento = new JMenu("Mantenimientos");
        menuBar.add(menuMantenimiento);

        JMenuItem itemUsers = new JMenuItem("Usuarios");
        menuMantenimiento.add(itemUsers);
        itemUsers.addActionListener(e -> {
            UserReadingForm userReadingForm = new UserReadingForm(this);
            userReadingForm.setVisible(true);
        });
    }
}
