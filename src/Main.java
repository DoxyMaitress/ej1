import java.io.*;
import java.util.*;

public class Main {

    private static List<Persona> personas = new ArrayList<>();
    private static Stack<Persona> historialEliminados = new Stack<>();
    private static PipedInputStream pipedInputStream = new PipedInputStream();
    private static PipedOutputStream pipedOutputStream = new PipedOutputStream();

    public static void main(String[] args) {
        try {
            pipedInputStream.connect(pipedOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner scanner = new Scanner(System.in);
        int opcion;
        do {
            System.out.println("==== Menú ====");
            System.out.println("1. Agregar persona");
            System.out.println("2. Modificar persona");
            System.out.println("3. Eliminar persona");
            System.out.println("4. Buscar persona por nombre");
            System.out.println("5. Recuperar último elemento borrado");
            System.out.println("6. Eliminar todos los datos");
            System.out.println("0. Salir");
            System.out.print("Elija una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la línea en blanco

            switch (opcion) {
                case 1:
                    agregarPersona(scanner);
                    break;
                case 2:
                    modificarPersona(scanner);
                    break;
                case 3:
                    eliminarPersona(scanner);
                    break;
                case 4:
                    buscarPersona(scanner);
                    break;
                case 5:
                    recuperarUltimoElementoBorrado();
                    break;
                case 6:
                    eliminarTodosLosDatos();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } while (opcion != 0);

        scanner.close();
    }

    private static void agregarPersona(Scanner scanner) {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Edad: ");
        int edad = scanner.nextInt();
        scanner.nextLine(); // Consumir la línea en blanco
        Persona persona = new Persona(nombre, apellido, edad);
        personas.add(persona);
        try {
            pipedOutputStream.write(("Persona agregada: " + persona + "\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void modificarPersona(Scanner scanner) {
        listarPersonas();
        System.out.print("Nombre de la persona a modificar: ");
        String nombreBuscado = scanner.nextLine();
        boolean personaEncontrada = false;

        for (Persona persona : personas) {
            if (persona.getNombre().equalsIgnoreCase(nombreBuscado)) {
                System.out.println("Persona encontrada:");
                System.out.println(persona);

                System.out.print("Nuevo nombre: ");
                String nuevoNombre = scanner.nextLine();
                System.out.print("Nuevo apellido: ");
                String nuevoApellido = scanner.nextLine();
                System.out.print("Nueva edad: ");
                int nuevaEdad = scanner.nextInt();
                scanner.nextLine(); // Consumir la línea en blanco

                persona.setNombre(nuevoNombre);
                persona.setApellido(nuevoApellido);
                persona.setEdad(nuevaEdad);

                System.out.println("Persona modificada correctamente.");
                try {
                    pipedOutputStream.write(("Persona modificada: " + persona + "\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                personaEncontrada = true;
                break;
            }
        }

        if (!personaEncontrada) {
            System.out.println("Persona no encontrada.");
        }
    }

    private static void eliminarPersona(Scanner scanner) {
        listarPersonas();
        System.out.print("Nombre de la persona a eliminar: ");
        String nombreBuscado = scanner.nextLine();
        boolean personaEliminada = false;

        Iterator<Persona> iterator = personas.iterator();
        while (iterator.hasNext()) {
            Persona persona = iterator.next();
            if (persona.getNombre().equalsIgnoreCase(nombreBuscado)) {
                iterator.remove();
                historialEliminados.push(persona);
                System.out.println("Persona eliminada correctamente.");
                try {
                    pipedOutputStream.write(("Persona eliminada: " + persona + "\n").getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                personaEliminada = true;
                break;
            }
        }

        if (!personaEliminada) {
            System.out.println("Persona no encontrada.");
        }
    }

    private static void buscarPersona(Scanner scanner) {
        System.out.print("Ingrese el nombre de la persona a buscar: ");
        String nombreBuscado = scanner.nextLine();
        boolean personaEncontrada = false;

        for (Persona persona : personas) {
            if (persona.getNombre().equalsIgnoreCase(nombreBuscado)) {
                System.out.println("Persona encontrada:");
                System.out.println(persona);
                personaEncontrada = true;
                break;
            }
        }

        if (!personaEncontrada) {
            System.out.println("Persona no encontrada.");
        }
    }

    private static void recuperarUltimoElementoBorrado() {
        if (!historialEliminados.isEmpty()) {
            Persona personaRecuperada = historialEliminados.pop();
            personas.add(personaRecuperada);
            System.out.println("Persona recuperada: " + personaRecuperada);
            try {
                pipedOutputStream.write(("Persona recuperada: " + personaRecuperada + "\n").getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No hay personas eliminadas para recuperar.");
        }
    }

    private static void eliminarTodosLosDatos() {
        personas.clear();
        historialEliminados.clear();
        System.out.println("Todos los datos de las personas han sido eliminados.");
        try {
            pipedOutputStream.write("Todos los datos de las personas han sido eliminados.\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void listarPersonas() {
        System.out.println("Lista de personas:");
        for (int i = 0; i < personas.size(); i++) {
            System.out.println(i + ". " + personas.get(i));
        }
    }
}

class Persona {
    private String nombre;
    private String apellido;
    private int edad;

    public Persona(String nombre, String apellido, int edad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", edad=" + edad +
                '}';
    }
}
