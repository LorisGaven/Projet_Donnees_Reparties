import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.rmi.RemoteException;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class StubGenerator {
    
    public StubGenerator() {

    }

    /**
     * Génère un fichier stub et le compile
     * Pour l'objet o
     */
    public static void generateStub(Object o) {
        String name = o.getClass().getSimpleName();

        /* On vérifie que le stub n'existe pas déjà */
        String currentPath = System.getProperty("user.dir");
        File currentFolder = new File(currentPath);
        File[] files = currentFolder.listFiles();
        boolean containsClass = false;
        boolean containsJava = false;
        for (File file : files) {
            if (file.getName().equals(name+"_stub.class")) {
                containsClass = true;
            }
            if (file.getName().equals(name+"_stub.java")) {
                containsJava = true;
            }
        }
        /* Si le fichier n'existe pas on le créé */
        if (!containsJava) {
            File stubFile = new File(name+"_stub.java");
            try (BufferedWriter buff = new BufferedWriter(new FileWriter(stubFile))) {
                String contenu = "";
                contenu += "public class " + name + "_stub extends SharedObject implements ";
                contenu += name + "_itf, java.io.Serializable {\n\n";
                contenu += "\tpublic " + name + "_stub(Object o, int id) {\n";
                contenu += "\t\tsuper(o, id);\n\t}\n";

                Method[] methods = o.getClass().getDeclaredMethods();
                for (Method m : methods) {
                    if (Modifier.isPrivate(m.getModifiers())) {
                        contenu += "\t" + "public ";
                    } else if (Modifier.isPublic(m.getModifiers())) {
                        contenu += "\t" + "public ";
                    } else if (Modifier.isProtected(m.getModifiers())) {
                        contenu += "\t" + "public ";
                    } // On pourrait rajouter : static, synchronized, ...

                    contenu += m.getReturnType().getSimpleName() + " " + m.getName() + "(";

                    int nbArg = 0;
                    Class<?>[] parameters = m.getParameterTypes();
                    for (Class<?> p : parameters) {
                        contenu += p.getSimpleName() + " arg" + nbArg;
                        if (nbArg != parameters.length - 1) {
                            contenu += ", ";
                        }
                        nbArg++;
                    }
                    contenu += ")";

                    Class<?>[] exceptions = m.getExceptionTypes();
                    if (exceptions.length != 0) {
                        contenu += " throws ";
                    }
                    int nbExc = 0;
                    for (Class<?> e : exceptions) {
                        contenu += e.getName();
                        if (nbExc != exceptions.length - 1) {
                            contenu += ", ";
                        }
                        nbExc++;
                    }
                    contenu += " {\n";

                    char varName = Character.toLowerCase(name.charAt(0));
                    contenu += "\t\t" + name + " " + varName + " = (" + name + ") obj;\n";
                    String param = "";
                    for (int i=0; i < nbArg; i++) {
                        param += "arg" + i;
                        if (i != nbArg-1) {
                            param += ", ";
                        }
                    }
                    
                    if (!m.getReturnType().getSimpleName().equals("void")) {
                        contenu += "\t\treturn ";
                    } else {
                        contenu += "\t\t";
                    }

                    contenu += varName + "." + m.getName() +"(" + param +");\n"; 

                    contenu += "\t}\n\n";
                }

                contenu += "}";

                buff.write(contenu);
                buff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!containsClass) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            //DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            //StandardJavaFileManager sjfm = compiler.getStandardFileManager(diagnostics, null, null);
            int result = compiler.run(null, null, null, "-d", ".", name + "_stub.java");
            if (result == 0) {
                System.out.println("Compilation réussie");
            } else {
                System.out.println("La compilation a échoué");
            }
        }
    }




    public static void main(String[] args) {
        generateStub(new Sentence1());
    }
}
