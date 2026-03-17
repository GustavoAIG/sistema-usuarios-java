import java.io.*;
import java.util.*;

public class UsuarioApp {

    private static final String FILE = "usuarios.csv";
    private static HashMap<Integer, ArrayList<Usuario>> edades = new HashMap<>();
    private static int nextId = 1;

    public static void main(String[] args) {
        leerCSV();

        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("=== MENÚ USUARIOS ===");
            System.out.println("1. Leer usuarios por edad");
            System.out.println("2. Buscar usuarios por edad");
            System.out.println("3. Agregar usuario");
            System.out.println("4. Salir");
            System.out.print("Opción: ");
            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    listarUsuarios();
                    break;
                case 2:
                    buscarPorEdad(sc);
                    break;
                case 3:
                    agregarUsuario(sc);
                    break;
                case 4:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        } while (opcion != 4);
    }

    private static void leerCSV() {
        try(BufferedReader lector = new BufferedReader(new FileReader(FILE))) {
            String linea;

            while ((linea = lector.readLine()) != null) {
                String[] datos = linea.split(",");
                int id = Integer.parseInt(datos[0]);
                String nombre = datos[1];
                int edad = Integer.parseInt(datos[2]);

                edades.computeIfAbsent(edad, k -> new ArrayList<>()).add(new Usuario(id, nombre, edad));

                if(id >= nextId) nextId = id + 1;
            }

        }catch (IOException e) {
            System.out.println("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private static void listarUsuarios() {
        System.out.println("=== Usuarios agrupados por edad ===");
        for (var entry : new TreeMap<>(edades).entrySet()) {
            System.out.println("Edad " + entry.getKey() + ":");
            for (Usuario u : entry.getValue()) {
                System.out.println(u);
            }
            System.out.println();
        }
    }

    private static void buscarPorEdad(Scanner sc) {
        System.out.print("Ingrese edad a buscar: ");
        int edad = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        if(edades.containsKey(edad)) {
            System.out.println("Usuarios de " + edad + " años:");
            for (Usuario u : edades.get(edad)) {
                System.out.println(u);
            }
        } else {
            System.out.println("No hay usuarios con esa edad");
        }
    }

    private static void agregarUsuario(Scanner sc) {
        System.out.print("Ingrese nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Ingrese edad: ");
        int edad = sc.nextInt();
        sc.nextLine(); // limpiar buffer

        if (existeUsuario(nombre, edad)) {
            System.out.println("El usuario ya existe.\n");
            return;
        }

        Usuario u = new Usuario(nextId++, nombre, edad);
        edades.computeIfAbsent(edad, k -> new ArrayList<>()).add(u);

        // Guardar en CSV
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(FILE, true))) {
            bw.write(u.getId() + "," + u.getEdad() + "," + u.getNombre()); // id,nombre,edad
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al guardar usuario: " + e.getMessage());
        }

        System.out.println("Usuario agregado!\n");
    }

    private static boolean existeUsuario(String nombre, int edad) {
        if(edades.containsKey(edad)) {
            for (Usuario u : edades.get(edad)) {
                if (u.getNombre().equalsIgnoreCase(nombre)) {
                    return true;
                }
            }
        }
        return false;
    }
}