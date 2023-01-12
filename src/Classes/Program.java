package Classes;

import java.util.ArrayList;
import java.util.List;

public class Program {
    public List<Class> classes;
    public List<Method> methods;

    public Program(){
        classes = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public void addMethod(Method m){
        methods.add(m);
    }

    public void addClass(Class c){
        classes.add(c);
    }

    @Override
    public String toString() {
        return "Program{\n" +
                "\n---------- classes ----------" + classes +
                "\n"+
                "\n---------- methods outside class ----------" + methods +
                "\n    ";
    }
}
