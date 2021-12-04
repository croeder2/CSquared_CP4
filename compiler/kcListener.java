/**
 * Description: This class is used to do most of the calculations needed in order to perform actions with the compiler. 
 * 
 * @author Cameron Herbert
 * @author Claire Roeder
 * @version 6.0
 * Programming Project 4
 * CS322 - Compiler Construction
 * Fall 2021
 */
import java.util.HashMap;
import org.antlr.v4.runtime.ParserRuleContext; // need to debug every rule
import lexparse.*; //classes for lexer parser
import org.objectweb.asm.*;  //classes for generating bytecode
import org.objectweb.asm.Opcodes; //Explicit import for ASM bytecode constants

public class kcListener extends KnightCodeBaseListener{

    //HASHMAP named memory to store variable names and values
    private HashMap<String, Value> memory = new HashMap<String, Value>();
    private ClassWriter cw;  //class level ClassWriter 
	private MethodVisitor mainVisitor; //class level MethodVisitor
	private String programName; //name of the class and the output file (used by ASM)
	private boolean debug; //flag to indicate debug status
    /**
    Create the kcListener Object
     */
    public kcListener(String programName){
        this.programName = programName;
    }//end myListener

    //set up class for bytecode use
    public void setUpClass(){
       //ClassWriter 
       ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
       cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC,this.programName, null, "java/lang/Object",null);
	
        //Use local MethodVisitor to create the constructor for the object
		MethodVisitor mv=cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V",false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1,1);
        mv.visitEnd();
       	
        //writes bytecode from tree
        mainVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,  "main", "([Ljava/lang/String;)V", null, null);
        mainVisitor.visitCode();

    }//end setupClass

    public void closeClass(){
		//Use global MethodVisitor to finish writing the bytecode and write the binary file.
		mainVisitor.visitInsn(Opcodes.RETURN);
		mainVisitor.visitMaxs(3, 3);
		mainVisitor.visitEnd();

		cw.visitEnd();

        byte[] b = cw.toByteArray();

        Utilities.writeFile(b,this.programName+".class");
        
        System.out.println("Done!");

	}//end closeClass

    @Override public void enterFile(KnightCodeParser.FileContext ctx) {
        System.out.println("Enter program rule for first time");
        setUpClass();
     }
    
    @Override public void exitFile(KnightCodeParser.FileContext ctx) {
        System.out.println("Leaving program rule...");
		printContext(ctx.getText());
		closeClass();
     }
  
    /**
	 * Prints context string. Used for debugging purposes
	 * @param ctx
	 */
	private void printContext(String ctx){
		System.out.println(ctx);
	}

    //DECLARE
    @Override public void enterDeclare(KnightCodeParser.DeclareContext ctx, Value type) { 
        System.out.println("Enter Declare");
    }

    @Override public void exitDeclare(KnightCodeParser.DeclareContext ctx) { 
        System.out.println("Exit Declare");
    }

    //VARIABLE
    @Override public void enterVariable(KnightCodeParser.VariableContext ctx) {
        System.out.println("Enter Variable");
        Value type = new Value(); //creates new value object for the datatype
        type.setDeclareType(ctx.getChild(0).getText()); //sets declareType to first child, STRING or INTEGER
        String name = ctx.getChild(1).getText(); //sets name of variable type to 2nd child
        memory.put(ctx.getChild(1).getText(), type);//stores in the hashmap the VARIABLE NAME and TYPE ex: (x, INTEGER)
    }//end enterVariable
    @Override public void exitVariable(KnightCodeParser.VariableContext ctx) {
        System.out.println("Exit Variable");
     }

    //IDENTIFIER
    @Override public void enterIdentifier(KnightCodeParser.IdentifierContext ctx) {
        System.out.println("Enter Identifier");
     }
    @Override public void exitIdentifier(KnightCodeParser.IdentifierContext ctx) {
        System.out.println("Exit Identifier");
     }

    //VARTYPE
    @Override public void enterVartype(KnightCodeParser.VartypeContext ctx) { 
        System.out.println("Enter Vartype");
        boolean valid;
        if(ctx.getChild(0).contains("INTEGER") || ctx.getChild(0).contains("STRING")){ //checks that the vartype was string or integer
            valid = true;
        }//end if
        else if{
            valid = false;
            System.out.println("Invalid variable type");
        }//end else if
    }//end enterVarType

    @Override public void exitVartype(KnightCodeParser.VartypeContext ctx) {
        System.out.println("Exit VarType");
     }

    @Override public void enterBody(KnightCodeParser.BodyContext ctx) { }
    @Override public void exitBody(KnightCodeParser.BodyContext ctx) { }
    @Override public void enterStat(KnightCodeParser.StatContext ctx) { }  
    @Override public void exitStat(KnightCodeParser.StatContext ctx) { }

    //SETVAR
    @Override public void enterSetvar(KnightCodeParser.SetvarContext ctx) {
        System.out.println("Enter SetVar");
        /**
         * Since the values currently stored in the HashMap are (name, type), ctx.getChild of enterSetVar will 
         * correspond to values of name and can be referenced this way, because SETVAR assigns values to already declared variables
         */
        Value set = new Value(); //create a new value object to set the value of datatype
        int assign = ctx.getText.indexOf(':=');
        set.setValue(ctx.getChild(3).getText()); //sets the value to the 4th child pos (set x := 8)
        memory.get(ctx.getChild(1).getText()) //calls the name of the variable
        memory.replace(ctx.getChild(1).getText(), set); //the hash map will now store variables with their assigned value (ex: (x, 8))
     }//end setVar
    @Override public void exitSetvar(KnightCodeParser.SetvarContext ctx) {
        System.out.println("Exit Setvar");
     }
 
    @Override public void enterParenthesis(KnightCodeParser.ParenthesisContext ctx) {  }
    @Override public void exitParenthesis(KnightCodeParser.ParenthesisContext ctx) { }

    //MULTIPLICATION
    @Override public void enterMultiplication(KnightCodeParser.MultiplicationContext ctx) { 
        System.out.println("Enter Multiplication");
        Value answer = new Value();
        int operator = ctx.getText.indexOf('*'); //finds the index number of * in the expression
        int operand1 = ctx.getText.indexOf(operator - 1); //finds index of first operand
        int operand2 = ctx.getText.indexOf(operator + 1); //finds index of second operand
        
        mainVisitor.visitVarInsn(Opcodes.ILOAD, operand1);
        mainVisitor.visitVarInsn(Opcodes.ILOAD, operand2);
        mainVisitor.visitInsn(Opcodes.IMUL); //performs multiplication
        answer.setValue(mainVisitor.visitInsn(Opcodes.ISTORE));//sets operation into answer's value

        memory.add(ctx.getChild(0).getText(), answer); //hashmap now stores variable name with the answer as its value (ex:(z,12));
    }//end entermultiplication
    @Override public void exitMultiplication(KnightCodeParser.MultiplicationContext ctx) { 
        System.out.println("Exit Multiplication");
    }
    
    //DIVISION
    @Override public void enterDivision(KnightCodeParser.DivisionContext ctx) { 

        System.out.println("Enter Division");
        Value answer = new Value(); //value object to store answer
        int operator = ctx.getText.indexOf('/'); //finds index of /
        int div1 = ctx.getText.indexOf(operator - 1); //finds index of first operand
        int div2 = ctx.getText.indexOf(operator + 1); //second operand
        
        mainVisitor.visitVarInsn(Opcodes.ILOAD, div1);
        mainVisitor.visitVarInsn(Opcodes.ILOAD, div2);
        mainVisitor.visitInsn(Opcodes.IDIV);//performs divison

        memory.add(ctx.getChild(.getText()), answer); //hashmap now stores variable name with the answer as its value (ex:(z,12));
    }
    @Override public void exitDivision(KnightCodeParser.DivisionContext ctx) {
        System.out.println("Exit Divison");
     }
     
    //ADDITION
    @Override public void enterAddition(KnightCodeParser.AdditionContext ctx) { 
        System.out.println("Enter Addition");
        Value answer = new Value(); //value object to store answer
        int operator = ctx.getText.indexOf('+');
        int add1 = ctx.getText.indexOf(operator - 1);
        int add2 = ctx.getText.indexOf(operator + 1);
        
        
        mainVisitor.visitVarInsn(Opcodes.ILOAD, add1);
        mainVisitor.visitVarInsn(Opcodes.ILOAD, add2);
        mainVisitor.visitInsn(Opcodes.IADD);
        answer.setValue(mainVisitor.visitVarInsn(Opcodes.ISTORE, 3));

        memory.add(ctx.getChild(0).getText(), answer); //hashmap now stores variable name with the answer as its value (ex:(z,12));
    }//end enterAddition

    @Override public void exitAddition(KnightCodeParser.AdditionContext ctx) { 
        System.out.println("Exit Addition");
    }

    //SUBTRACTION
    @Override public void enterSubtraction(KnightCodeParser.SubtractionContext ctx) {
        System.out.println("Enter Subtraction");
        Value answer = new Value();
        int operator = ctx.getText.indexOf('+');
        int sub1 = ctx.getText.indexOf(operator - 1);
        int sub2 = ctx.getText.indexOf(operator + 1);
        
        mainVisitor.visitInsn(Opcodes.ILOAD, sub1);
        mainVisitor.visitInsn(Opcodes.ILOAD, sub2);
        mainVisitor.visitVarInsn(Opcodes.ISUB);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, 3);

        memory.add(ctx.getChild(0).getText(), answer); //hashmap now stores variable name with the answer as its value (ex:(z,12));
     }

    @Override public void exitSubtraction(KnightCodeParser.SubtractionContext ctx) {
        System.out.println("Exit Subtraction");
     }

    @Override public void enterNumber(KnightCodeParser.NumberContext ctx) { }

    @Override public void exitNumber(KnightCodeParser.NumberContext ctx) { }

    @Override public void enterComparison(KnightCodeParser.ComparisonContext ctx) { }

    @Override public void exitComparison(KnightCodeParser.ComparisonContext ctx) { }
     
    @Override public void enterId(KnightCodeParser.IdContext ctx) { }
    @Override public void exitId(KnightCodeParser.IdContext ctx) { }
    @Override public void enterComp(KnightCodeParser.CompContext ctx) { }
    @Override public void exitComp(KnightCodeParser.CompContext ctx) { }   

    //PRINT
    @Override public void enterPrint(KnightCodeParser.PrintContext ctx) {

        Value print = new Value();
        print.setValue(ctx.getChild(1).getText()); //sets value to 2nd child
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
     }
 
    @Override public void exitPrint(KnightCodeParser.PrintContext ctx) { }

    //READ
    @Override public void enterRead(KnightCodeParser.ReadContext ctx) { 
        
        Value read = new Value(); //new value object to store user input
        Scanner scan = new Scanner(System.in); //scanner
        read.setValue = scan.nextLine(); //sets the value of read to the input of user
        memory.add(ctx.getChild(1).getText(), read); //stores read in hashmap, corresponds with variable name that was called with READ
        
    }

    @Override public void exitRead(KnightCodeParser.ReadContext ctx) {

        System.out.println("Exit Read");
     }
    
    @Override public void enterDecision(KnightCodeParser.DecisionContext ctx) { }
    @Override public void exitDecision(KnightCodeParser.DecisionContext ctx) { }
    @Override public void enterLoop(KnightCodeParser.LoopContext ctx) { }
    @Override public void exitLoop(KnightCodeParser.LoopContext ctx) { }
    @Override public void enterEveryRule(ParserRuleContext ctx) { }
    @Override public void exitEveryRule(ParserRuleContext ctx) { }
    @Override public void visitTerminal(TerminalNode node) { }
    @Override public void visitErrorNode(ErrorNode node) { }
}
}