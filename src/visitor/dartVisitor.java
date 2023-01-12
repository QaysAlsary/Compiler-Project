package visitor;

import Classes.*;
import Classes.Class;
import antlr.*;
import org.antlr.v4.gui.TestRig;
import org.antlr.v4.runtime.Token;

import javax.swing.plaf.multi.MultiInternalFrameUI;
import java.lang.Float;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dartVisitor extends dartBaseVisitor{

    public List<String> semanticErrors ;
    public HashMap<String , Object> assignedVars ;//store all variables that have a value.
    public List<Method> methods;//store all the methods in the program

    public dartVisitor(){
        semanticErrors = new ArrayList<>();
        assignedVars = new HashMap<>();
        methods = new ArrayList<>();
    }

    @Override
    public Program visitProgram(dartParser.ProgramContext ctx) {
        Program prog = new Program();
        int m = 0,c=0;
        for(int i=0 ; i< ctx.getChildCount() ; i++) {
            if(visit(ctx.getChild(i)) instanceof Class){
                prog.addClass((Class) visit(ctx.getChild(i)));
                c++;
            }
            else{
                prog.addMethod((Method) visit(ctx.getChild(i)));
                m++;
            }
        }
        //System.out.println("visiting program with " + c + " Class and " + m + " Method");
        return prog;
    }

    //--------------------------------------------------------------------

    @Override
    public Class visitClasses(dartParser.ClassesContext ctx) {
        Class class1 = new Class();
        String id = ctx.getChild(1).getText();
        class1.ID = id;
        int i = 3;
        while(!ctx.getChild(i).getText().equals("}")){
            if(visit(ctx.getChild(i)) instanceof Declaration)
                class1.addDeclaration((Declaration) visit(ctx.getChild(i)));
            else
                class1.addMethod((Method) visit(ctx.getChild(i)));
            i++;
        }
        //System.out.println("    visiting class ' " + id + " ' with " + (ctx.getChildCount()-4) + " method");
        return class1;
    }

//    @Override
//    public Declaration visitDeclareStat(dartParser.DeclContext ctx) {
////        String type = ctx.getChild(0).getText();
////        String id = ctx.getChild(1).getText();
////        if(ctx.getChildCount()>3){
////            Expression value = (Expression) visit(ctx.getChild(3));
////
////            assignedVars.put(id,value);
////
////            //System.out.println("            visiting decl : " + type + " " + id + " = " + value.toString());
////            return new Declaration(id, type , value);
////        }
////        else{
////            //System.out.println("            visiting decl : " + type + " " + id + ";");
////            return new Declaration(id,type);
////        }
//        return (Declaration)visit(ctx.getChild(0));
//    }


    @Override
    public Method visitMethod(dartParser.MethodContext ctx) {
        Method method = new Method();
        String type = ctx.getChild(0).getText();
        String id = ctx.getChild(1).getText();
        method.ID = id;
        method.returnType = type;

        int i = 3 ,p=0,s=0;
        while(!ctx.getChild(i).getText().equals(")")){
            p++;

            String paramType = ctx.getChild(i).getText();
            String paramName = ctx.getChild(i+1).getText();

            method.addParameter(paramType,paramName);

            if(ctx.getChild(i+2).getText().equals(","))
                i+=3;
            else
                i+=2;
        }
        i+=2;
        while(!ctx.getChild(i).getText().equals("return")){
            method.addStatment((Statment) visit(ctx.getChild(i)));
            i++;
            s++;
        }
       // System.out.println(method.ID + "-------------------------" + methods.size());

        method.returnValue = (Expression) visit(ctx.getChild(i+1));
        //System.out.println("    visiting method ' " + id + " ' with " + p + " Parameter and " + s + " Statment and return value is : " + method.returnValue.toString());

        if(!methods.contains(method)){
            methods.add(method);
        }

        return method;
    }

    @Override
    public If visitIfCondition(dartParser.IfConditionContext ctx) {
        If ifCond = new If();

        int i = 2,s=0;
        while(!ctx.getChild(i).getText().equals(")")){
            ifCond.addCondition((Condition) visit(ctx.getChild(i)));
            if(ctx.getChild(i+1).getText().equals("&&") || ctx.getChild(i+1).getText().equals("||"))
                i+=2;
            else
                i++;
        }
        i+=2;

        while(!ctx.getChild(i).getText().equals("}")){
                ifCond.addStatment((Statment) visit(ctx.getChild(i)));
                i++;
                s++;
        }

        return ifCond;
        //System.out.println("        visiting if : if( " + ctx.getChild(2).getText() + " ) {  " + s + " Statment }" );
    }

    @Override
    public While visitWhileLoop(dartParser.WhileLoopContext ctx) {
        While whileLoop = new While();

        int i = 2,s=0;
        while(!ctx.getChild(i).getText().equals(")")){
            whileLoop.addCondition((Condition) visit(ctx.getChild(i)));
            if(ctx.getChild(i+1).getText().equals("&&") || ctx.getChild(i+1).getText().equals("||"))
                i+=2;
            else
                i++;
        }
        i+=2;

        while(!ctx.getChild(i).getText().equals("}")){
            whileLoop.addStatment((Statment) visit(ctx.getChild(i)));
            i++;
            s++;
        }
        //System.out.println("        visiting while : while( " + ctx.getChild(2).getText() + " ) {  " + s + " Statment }" );
        return whileLoop;
    }

    @Override
    public For visitForLoop(dartParser.ForLoopContext ctx) {
        Expression startValue = (Expression) visit(ctx.getChild(4));
        Expression endValue = (Expression) visit(ctx.getChild(8));
        Expression increment = (Expression) visit(ctx.getChild(12));

        For forLoop = new For(startValue, endValue, increment);

        int i = 15,s=0;
        while(!ctx.getChild(i).getText().equals("}")){
            forLoop.addStatment((Statment) visit(ctx.getChild(i)));
            i++;
            s++;
        }
        //System.out.println("        visiting for : for( " + startValue.toString() + " ; " + endValue.toString() + " ; " + increment.toString() + " ) { " + s + " Statment }");
        return forLoop;
    }

    @Override
    public ExpressionPar visitParenExpression(dartParser.ParenExpressionContext ctx) {
        Expression exp = (Expression) visit(ctx.getChild(1));
        //System.out.println("            visiting Expression " +  exp.toString());
        return new ExpressionPar(exp);
    }

    @Override
    public Condition visitCondition(dartParser.ConditionContext ctx) {
        Expression left = (Expression) visit(ctx.getChild(0));
        Expression right = (Expression) visit(ctx.getChild(2));
        String str = ctx.getChild(1).getText();

        //System.out.println("            visiting condition : " + left.toString() + " " + ctx.getChild(1).getText() + " " + right.toString());
        return new Condition(left, right, str);
    }

    @Override
    public Assignment visitAssignment(dartParser.AssignmentContext ctx) {
        String id = ctx.getChild(0).getText();
        Expression value = (Expression) visit(ctx.getChild(2));

        if(semanticErrors.contains(id)) {
            semanticErrors.remove(id);
        }
        if(!assignedVars.keySet().contains(id)) {
            assignedVars.put(id , value);
        }
//        else{
//            assignedVars.put(id + " new " , value);
//        }

        //System.out.println("        visiting assignment : " + id + " = " + value.toString());
        return new Assignment(id, value);
    }

    @Override
    public Subtract visitSubtract(dartParser.SubtractContext ctx) {
        Expression left = (Expression) visit(ctx.getChild(0));
        Expression right = (Expression) visit(ctx.getChild(2));

        //System.out.println("            visiting sub : " + left .toString() + " - " + right.toString());
        return new Subtract(left, right);
    }

    @Override
    public Divide visitDivide(dartParser.DivideContext ctx) {
        Expression left = (Expression) visit(ctx.getChild(0));
        Expression right = (Expression) visit(ctx.getChild(2));

        //System.out.println("            visiting divide : " + left .toString() + " / " + right.toString());
        return new Divide(left, right);
    }

    @Override
    public Multiplication visitMultiplication(dartParser.MultiplicationContext ctx) {
        Expression left = (Expression) visit(ctx.getChild(0));
        Expression right = (Expression) visit(ctx.getChild(2));

        //System.out.println("            visiting mult : " + left .toString() + " * " + right.toString());
        return new Multiplication(left, right);
    }

    @Override
    public Addition visitAddition(dartParser.AdditionContext ctx) {
        Expression left = (Expression) visit(ctx.getChild(0));
        Expression right = (Expression) visit(ctx.getChild(2));

        //System.out.println("            visiting add : " + left .toString() + " + " + right.toString());
        return new Addition(left, right);
    }

    @Override
    public Variable visitIdentifier(dartParser.IdentifierContext ctx) {
        Token idToken = ctx.ID().getSymbol();
        int line = idToken.getLine();
        int column = idToken.getCharPositionInLine() + 1;

        String id = ctx.getChild(0).getText();
        if(!assignedVars.keySet().contains(id)) {
            semanticErrors.add("Error : variable " + id + " does not have value on line " + line + " , column " + column);
        }
        //System.out.println("            visiting variable : " + id);
        return new Variable(id);
    }

    @Override
    public StringExp visitString(dartParser.StringContext ctx) {
        String str = ctx.getChild(0).getText();

        //System.out.println("            visiting string : " + str);
        return new StringExp(str);
    }

    @Override
    public Bool visitBool(dartParser.BoolContext ctx) {
        String boolText = ctx.getChild(0).getText();
        boolean bool = Boolean.parseBoolean(boolText);

        //System.out.println("            visiting bool : " + boolText);
        return new Bool(bool);
    }

    @Override
    public Int visitInteger(dartParser.IntegerContext ctx) {
        String numText = ctx.getChild(0).getText();
        int num = Integer.parseInt(numText);

        //System.out.println("            visiting int : " + numText);
        return new Int(num);
    }

    @Override
    public Classes.Float visitFloat(dartParser.FloatContext ctx) {
        String numText = ctx.getChild(0).getText();
        float num = Float.parseFloat(numText);

        //System.out.println("            visiting float + " + numText);
        return new Classes.Float(num);
    }

    @Override
    public Declaration visitDeclaration(dartParser.DeclarationContext ctx) {
        String type = ctx.getChild(0).getText();
        String id = ctx.getChild(1).getText();
        if(ctx.getChildCount()>3){
            Expression value = (Expression) visit(ctx.getChild(3));

            assignedVars.put(id,value);

            //System.out.println("visiting declaration : " + type + " " + id + " = " + value.toString());
            return new Declaration(id, type , value);
        }
        else{
            //System.out.println("visiting declaration : " + type + " " + id + " ;");
            return new Declaration(id,type);
        }
    }

    @Override
    public Assignment visitAssignStat(dartParser.AssignStatContext ctx) {
        return (Assignment)visit(ctx.getChild(0));
    }

    @Override
    public Declaration visitDeclareStat(dartParser.DeclareStatContext ctx) {
        return (Declaration) visit(ctx.getChild(0));
    }

    @Override
    public If visitIfStat(dartParser.IfStatContext ctx) {
        return (If) visit(ctx.getChild(0));
    }

    @Override
    public For visitForStat(dartParser.ForStatContext ctx) {
        return (For)visit(ctx.getChild(0));
    }

    @Override
    public While visitWhileStat(dartParser.WhileStatContext ctx) {
        return (While)visit(ctx.getChild(0));
    }

    @Override
    public CallFunction visitCallingFunctionStat(dartParser.CallingFunctionStatContext ctx) {
        return (CallFunction) visit(ctx.getChild(0));
    }

    @Override
    public CallFunction visitCallingFunction(dartParser.CallingFunctionContext ctx) {
        CallFunction call = new CallFunction();
        call.ID = ctx.getChild(0).getText();
        int i = 2;
        while(!ctx.getChild(i).getText().equals(")")){
            if(!ctx.getChild(i).getText().equals(",")){
                if(visit(ctx.getChild(i)) instanceof Expression){
                    call.addArgument((Expression) visit(ctx.getChild(i)));
                }
                else{
                    call.addWidget((Widget) visit(ctx.getChild(i)));
                }
            }
            i++;
        }
        return call;
    }

    @Override
    public ObjectCallFunction visitCallingFuncitonFromObjectStat(dartParser.CallingFuncitonFromObjectStatContext ctx) {
        return (ObjectCallFunction) visit(ctx.getChild(0));
    }

    @Override
    public ObjectCallFunction visitCallingFuncitonFromObject(dartParser.CallingFuncitonFromObjectContext ctx) {
        ObjectCallFunction objectCall = new ObjectCallFunction();
        objectCall.object = ctx.getChild(0).getText();
        objectCall.callFunction = (CallFunction)visit(ctx.getChild(2));
        return objectCall;
    }

    @Override
    public Object visitPrintOutput(dartParser.PrintOutputContext ctx) {
        return visit(ctx.getChild(2));
    }

    @Override
    public DeclareObject visitDeclaringObjectStat(dartParser.DeclaringObjectStatContext ctx) {
        return (DeclareObject) visit(ctx.getChild(0));
    }

    @Override
    public InstantiatObject visitInstantiatingObjectStat(dartParser.InstantiatingObjectStatContext ctx) {
        return (InstantiatObject) visit(ctx.getChild(0));
    }

    @Override
    public DeclareObject visitDeclaringObject(dartParser.DeclaringObjectContext ctx) {
        DeclareObject declareObject = new DeclareObject();
        declareObject.className = ctx.getChild(0).getText();
        declareObject.objectName = ctx.getChild(1).getText();

        if(ctx.getChildCount() == 3)
            return declareObject;
        else{
            declareObject.instanObject =(InstantiatObject) visit(ctx.getChild(3));
            return declareObject;
        }
    }

    @Override
    public InstantiatObject visitInstantiatingObject(dartParser.InstantiatingObjectContext ctx) {
        InstantiatObject instantiatObject = new InstantiatObject();
        instantiatObject.className = ctx.getChild(1).getText();
        int i = 3;
        while(!ctx.getChild(i).getText().equals(")")){
            instantiatObject.addArgument((Expression)visit(ctx.getChild(i)));
            i++;
        }
        return instantiatObject;
    }

    // flutter visitor


    @Override
    public Widget visitWidget(dartParser.WidgetContext ctx) {
        return (Widget) visit(ctx.getChild(0));
    }

    @Override
    public Scaffold visitMaterialWidget(dartParser.MaterialWidgetContext ctx) {
        return (Scaffold) visit(ctx.getChild(4));
    }

    @Override
    public Scaffold visitScaffoldWidget(dartParser.ScaffoldWidgetContext ctx) {
        Scaffold scaffold = new Scaffold();
        scaffold.bar = (AppBar)visit(ctx.getChild(4));
        scaffold.body = (Body) visit(ctx.getChild(8));

        return scaffold;
    }

    @Override
    public Body visitBody(dartParser.BodyContext ctx) {
        return new Body((Widget)visit(ctx.getChild(0)));
    }

    @Override
    public AppBar visitAppBarWidget(dartParser.AppBarWidgetContext ctx) {
        AppBar bar = new AppBar();
        bar.title = (Text)visit(ctx.getChild(4));

        return bar;
    }

    @Override
    public Object visitTextWidget(dartParser.TextWidgetContext ctx) {
        Text text = new Text();
        text.text = ctx.getChild(2).getText();

        return text;
    }

    @Override
    public Row visitRowWidget(dartParser.RowWidgetContext ctx) {
        Row row = new Row();
        int i = 5;

        while(!ctx.getChild(i).getText().equals("]")){
            if(!ctx.getChild(i).getText().equals(",")) {
                row.addChild((Widget) visit(ctx.getChild(i)));
            }
            i++;
        }

        return row;
    }

    @Override
    public Column visitColumnWidget(dartParser.ColumnWidgetContext ctx) {
        Column column = new Column();
        int i = 5;

        while(!ctx.getChild(i).getText().equals("]")){
            if(!ctx.getChild(i).getText().equals(",")){
                column.addChild((Widget) visit(ctx.getChild(i)));
            }
            i++;
        }

        return column;
    }

    @Override
    public Image visitImageWidget(dartParser.ImageWidgetContext ctx) {
        return new Image(ctx.getChild(6).getText());
    }
}
