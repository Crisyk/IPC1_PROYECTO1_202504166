import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    static ArrayList<Usuario> usuarios = new ArrayList<>();
    static ArrayList<Libro> libros = new ArrayList<>();
    static ArrayList<Prestamo> prestamos = new ArrayList<>();

    static Usuario usuarioActual;

    public static void main(String[] args) {

        cargarUsuarios();
        cargarPrestamos();
        cargarLibrosDemo();

        new LoginFrame();
    }

    static void cargarLibrosDemo() {

        // Libros originales
        libros.add(new Libro("L1","1234567890","Java Básico","Autor A","Programación",2020,5));
        libros.add(new Libro("L2","1234567891","Estructuras de Datos","Autor B","Programación",2019,3));
        libros.add(new Libro("L3","1234567892","Base de Datos","Autor C","Tecnología",2021,4));

        // Nuevos libros agregados para pruebas
        libros.add(new Libro("L4","1234567893","Clean Code","Robert C. Martin","Ingeniería",2008,5));
        libros.add(new Libro("L5","1234567894","El Señor de los Anillos","J.R.R. Tolkien","Fantasía",1954,2));
        libros.add(new Libro("L6","1234567895","Cien Años de Soledad","Gabriel García Márquez","Novela",1967,3));
        libros.add(new Libro("L7","1234567896","Introducción a los Algoritmos","Thomas H. Cormen","Programación",2009,4));
        libros.add(new Libro("L8","1234567897","Patrones de Diseño","Erich Gamma","Ingeniería",1994,2));
        libros.add(new Libro("L9","1234567898","Inteligencia Artificial","Stuart Russell","Tecnología",2021,3));
        libros.add(new Libro("L10","1234567899","Don Quijote de la Mancha","Miguel de Cervantes","Clásico",1605,1));
    }

    static void cargarUsuarios() {

        try {

            File file = new File("cuentas.txt");
            if(!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;

            while((linea=br.readLine())!=null){

                String[] p = linea.split(";");

                usuarios.add(new Usuario(p[0],p[1],p[2],p[3],p[4]));
            }

            br.close();

        } catch(Exception e){}
    }

    static void guardarUsuarios(){

        try{

            PrintWriter pw = new PrintWriter("cuentas.txt");

            for(Usuario u:usuarios){

                pw.println(u.rol+";"+u.usuario+";"+u.password+";"+u.nombre+";"+u.extra);
            }

            pw.close();

        }catch(Exception e){}
    }

    static void cargarPrestamos(){

        try{

            File file = new File("prestamos.txt");
            if(!file.exists()) return;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String linea;

            while((linea=br.readLine())!=null){

                String[] p=linea.split(";");

                prestamos.add(new Prestamo(
                        p[0],p[1],p[2],
                        LocalDate.parse(p[3]),
                        LocalDate.parse(p[4]),
                        p[5]
                ));
            }

            br.close();

        }catch(Exception e){}
    }

    static void guardarPrestamos(){

        try{

            PrintWriter pw=new PrintWriter("prestamos.txt");

            for(Prestamo pr:prestamos){

                pw.println(pr.codigo+";"+pr.carnet+";"+pr.libro+
                        ";"+pr.fechaPrestamo+";"+pr.fechaLimite+";"+pr.estado);
            }

            pw.close();

        }catch(Exception e){}
    }

    static void bitacora(String operacion,String modulo){

        try{

            FileWriter fw=new FileWriter("bitacora.txt",true);

            DateTimeFormatter f1=DateTimeFormatter.ofPattern("dd/MM/yy");
            DateTimeFormatter f2=DateTimeFormatter.ofPattern("hh:mm a");

            fw.write("["+operacion+"]["+
                    (usuarioActual!=null?usuarioActual.usuario:"NA")+
                    "]["+modulo+"]["+
                    LocalDate.now().format(f1)+"]["+
                    LocalTime.now().format(f2)+"]\n");

            fw.close();

        }catch(Exception e){}
    }

}

class Usuario{

    String rol;
    String usuario;
    String password;
    String nombre;
    String extra;

    Usuario(String r,String u,String p,String n,String e){

        rol=r;
        usuario=u;
        password=p;
        nombre=n;
        extra=e;
    }
}

class Libro{

    String codigo;
    String isbn;
    String titulo;
    String autor;
    String genero;
    int anio;
    int total;
    int disponibles;

    Libro(String c,String i,String t,String a,String g,int an,int tot){

        codigo=c;
        isbn=i;
        titulo=t;
        autor=a;
        genero=g;
        anio=an;
        total=tot;
        disponibles=tot;
    }
}

class Prestamo{

    String codigo;
    String carnet;
    String libro;
    LocalDate fechaPrestamo;
    LocalDate fechaLimite;
    String estado;

    Prestamo(String c,String ca,String l,LocalDate fp,LocalDate fl,String e){

        codigo=c;
        carnet=ca;
        libro=l;
        fechaPrestamo=fp;
        fechaLimite=fl;
        estado=e;
    }
}

class LoginFrame extends JFrame{

    JTextField user;
    JPasswordField pass;

    LoginFrame(){

        setTitle("BiblioSystem Login");

        setSize(300,200);
        setLayout(new GridLayout(4,2));

        add(new JLabel("Usuario"));
        user=new JTextField();
        add(user);

        add(new JLabel("Password"));
        pass=new JPasswordField();
        add(pass);

        JButton login=new JButton("Login");
        JButton crear=new JButton("Crear Estudiante");

        add(login);
        add(crear);

        login.addActionListener(e->login());
        crear.addActionListener(e->registro());

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    void login(){

        String u=user.getText();
        String p=new String(pass.getPassword());

        if(u.equals("admin") && p.equals("admin")){

            Main.usuarioActual=new Usuario("ADMIN","admin","admin","Administrador","");
            new AdminMenu();
            dispose();
            return;
        }

        for(Usuario us:Main.usuarios){

            if(us.usuario.equals(u) && us.password.equals(p)){

                Main.usuarioActual=us;

                if(us.rol.equals("OPERADOR"))
                    new OperadorMenu();
                else
                    new EstudianteMenu();

                dispose();
                return;
            }
        }

        JOptionPane.showMessageDialog(this,"Login incorrecto");
    }

    void registro(){

        String carnet=JOptionPane.showInputDialog("Carnet");
        String nombre=JOptionPane.showInputDialog("Nombre");
        String carrera=JOptionPane.showInputDialog("Carrera");
        String pass=JOptionPane.showInputDialog("Password");

        Main.usuarios.add(new Usuario(
                "ESTUDIANTE",carnet,pass,nombre,carrera));

        Main.guardarUsuarios();

        JOptionPane.showMessageDialog(this,"Cuenta creada");
    }
}

class AdminMenu extends JFrame{

    AdminMenu(){

        setTitle("Administrador");

        setSize(300,300);
        setLayout(new GridLayout(6,1));

        JButton op1=new JButton("Crear Operador");
        JButton op2=new JButton("Eliminar Operador");
        JButton op3=new JButton("Ver Operadores");
        JButton op4=new JButton("Gestion Libros");
        JButton op5=new JButton("Prestamos");

        add(op1);
        add(op2);
        add(op3);
        add(op4);
        add(op5);

        op1.addActionListener(e->crear());
        op2.addActionListener(e->eliminar());
        op3.addActionListener(e->listar());
        op4.addActionListener(e->new LibrosFrame());
        op5.addActionListener(e->new PrestamosFrame());

        setVisible(true);
    }

    void crear(){

        String user=JOptionPane.showInputDialog("Usuario");
        String pass=JOptionPane.showInputDialog("Password");
        String nombre=JOptionPane.showInputDialog("Nombre");

        Main.usuarios.add(new Usuario("OPERADOR",user,pass,nombre,""));

        Main.guardarUsuarios();
    }

    void eliminar(){

        String user=JOptionPane.showInputDialog("Usuario");

        Main.usuarios.removeIf(u->u.usuario.equals(user));

        Main.guardarUsuarios();
    }

    void listar(){

        String s="";

        for(Usuario u:Main.usuarios){

            if(u.rol.equals("OPERADOR"))
                s+=u.usuario+" - "+u.nombre+"\n";
        }

        JOptionPane.showMessageDialog(this,s);
    }
}

class OperadorMenu extends JFrame{

    OperadorMenu(){

        setTitle("Operador");

        setSize(300,200);
        setLayout(new GridLayout(3,1));

        JButton libros=new JButton("Gestion Libros");
        JButton prestamos=new JButton("Prestamos");

        add(libros);
        add(prestamos);

        libros.addActionListener(e->new LibrosFrame());
        prestamos.addActionListener(e->new PrestamosFrame());

        setVisible(true);
    }
}

class EstudianteMenu extends JFrame{

    EstudianteMenu(){

        setTitle("Estudiante");

        setSize(300,200);
        setLayout(new GridLayout(3,1));

        JButton pedir=new JButton("Solicitar Prestamo");
        JButton historial=new JButton("Ver Historial");

        add(pedir);
        add(historial);

        pedir.addActionListener(e->solicitar());
        historial.addActionListener(e->historial());

        setVisible(true);
    }

    void solicitar(){

        String libro=JOptionPane.showInputDialog("Codigo libro");

        for(Libro l:Main.libros){

            if(l.codigo.equals(libro) && l.disponibles>0){

                String cod="P"+(Main.prestamos.size()+1);

                Main.prestamos.add(
                        new Prestamo(
                                cod,
                                Main.usuarioActual.usuario,
                                libro,
                                LocalDate.now(),
                                LocalDate.now().plusDays(15),
                                "ACTIVO"
                        )
                );

                l.disponibles--;

                Main.guardarPrestamos();

                JOptionPane.showMessageDialog(this,"Prestamo registrado");

                return;
            }
        }

        JOptionPane.showMessageDialog(this,"Libro no disponible");
    }

    void historial(){

        String s="";

        for(Prestamo p:Main.prestamos){

            if(p.carnet.equals(Main.usuarioActual.usuario)){

                s+=p.codigo+" "+p.libro+" "+p.estado+"\n";
            }
        }

        JOptionPane.showMessageDialog(this,s);
    }
}

class LibrosFrame extends JFrame{

    LibrosFrame(){

        setTitle("Libros");

        setSize(300,300);
        setLayout(new GridLayout(4,1));

        JButton add=new JButton("Agregar Libro");
        JButton lista=new JButton("Listar Libros");

        add(add);
        add(lista);

        add.addActionListener(e->agregar());
        lista.addActionListener(e->listar());

        setVisible(true);
    }

    void agregar(){

        String c=JOptionPane.showInputDialog("Codigo");
        String isbn=JOptionPane.showInputDialog("ISBN");
        String t=JOptionPane.showInputDialog("Titulo");
        String a=JOptionPane.showInputDialog("Autor");
        int tot=Integer.parseInt(JOptionPane.showInputDialog("Ejemplares"));

            Main.libros.add(new Libro(c,isbn,t,a,"",2024,tot));
    }

    void listar(){

        String s="";

        for(Libro l:Main.libros){

            s+=l.codigo+" "+l.titulo+" disponibles:"+l.disponibles+"\n";
        }

        JOptionPane.showMessageDialog(this,s);
    }
}

class PrestamosFrame extends JFrame{

    PrestamosFrame(){

        setTitle("Prestamos");

        setSize(300,200);
        setLayout(new GridLayout(3,1));

        JButton prestar=new JButton("Registrar Prestamo");
        JButton devolver=new JButton("Registrar Devolucion");

        add(prestar);
        add(devolver);

        prestar.addActionListener(e->prestar());
        devolver.addActionListener(e->devolver());

        setVisible(true);
    }

    void prestar(){

        String carnet=JOptionPane.showInputDialog("Carnet");
        String libro=JOptionPane.showInputDialog("Codigo libro");

        for(Libro l:Main.libros){

            if(l.codigo.equals(libro) && l.disponibles>0){

                String cod="P"+(Main.prestamos.size()+1);

                Main.prestamos.add(
                        new Prestamo(
                                cod,
                                carnet,
                                libro,
                                LocalDate.now(),
                                LocalDate.now().plusDays(15),
                                "ACTIVO"
                        )
                );

                l.disponibles--;

                Main.guardarPrestamos();

                JOptionPane.showMessageDialog(this,"Prestamo registrado");

                return;
            }
        }
    }

    void devolver(){

        String codigo=JOptionPane.showInputDialog("Codigo prestamo");

        for(Prestamo p:Main.prestamos){

            if(p.codigo.equals(codigo) && p.estado.equals("ACTIVO")){

                p.estado="DEVUELTO";

                for(Libro l:Main.libros){

                    if(l.codigo.equals(p.libro))
                        l.disponibles++;
                }

                Main.guardarPrestamos();

                JOptionPane.showMessageDialog(this,"Devuelto");

                return;
            }
        }
    }
}
