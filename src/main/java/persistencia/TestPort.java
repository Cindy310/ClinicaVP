package persistencia;

import java.net.Socket;

public class TestPort {
    public static void main(String[] args) {
        try (Socket socket = new Socket("DESKTOP-DMMK5UK", 1433)) {
            System.out.println("✅ Conexión exitosa al puerto 1433.");
        } catch (Exception e) {
            System.out.println("❌ No se pudo conectar al puerto 1433: " + e.getMessage());
        }
    }
}
