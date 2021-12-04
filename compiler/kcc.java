/**
 * This class is kicks off the compiler and creates an output file of the parser
 * 
 * @author Cameron Herbert
 * @author Claire Roeder
 * @version 6.0
 * Programming Project 4
 * CS322 - Compiler Construction
 * Fall 2021
 */
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import lexparse.*;

public class kcc {
    
    public static void main(String[] args) throws Exception{

        CharStream input;
        KnightCodeLexer lexer;
        CommonTokenStream tokens;
        KnightCodeParser parser;

        try{
            input = CharStreams.fromFileName(args[0]);  //charstream input
            lexer = new KnightCodeLexer(input); //lexer builds off input
            tokens = new CommonTokenStream(lexer); //token stream builds off lexer
            parser = new KnightCodeParser(tokens); //parser builds off tokens
       
            ParseTree tree = parser.file();  //set the start location of the parser

            Scanner scan = new Scanner(System.in); //Scanner
	        System.out.print("Enter name for output class file: ");
	        String classFile = scan.next();
             
            Trees.inspect(tree, parser);

            kcListener listener = new kcListener(classFile, false);
	        ParseTreeWalker walker = new ParseTreeWalker();
	        walker.walk(listener, tree);
        
            scan.close();
        } // end try
        catch(IOException e){
            System.out.println(e.getMessage());
        } // end catch

    }//end main
}//end kcc
