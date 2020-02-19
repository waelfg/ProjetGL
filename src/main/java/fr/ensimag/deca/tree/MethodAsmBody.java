package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.InlinePortion;

import java.io.PrintStream;
import java.util.IllegalFormatException;

/**
 * Created by saunievi on 1/21/20.
 */
public class MethodAsmBody extends AbstractMethodBody {

    private StringLiteral string;

    public MethodAsmBody(StringLiteral string) {
        this.string = string;
    }

    /**
     * Vérifie que le contenu de la méthode est bien un string
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @param retour
     * @throws ContextualError
     */
    @Override
    public void verifyMethodBody(DecacCompiler compiler,
                                 EnvironmentExp localEnv,
                                 ClassDefinition currentClass,
                                 Type retour) throws ContextualError {
        string.verifyExpr(compiler, localEnv, currentClass);
    }


    /**
     * fonction générant recopiant une chaine de caractere en assembleur dans le fichier assembleur d'un programme
     * @param compiler
     */
    @Override
    public void codeGenBody(DecacCompiler compiler) {
        compiler.add(new InlinePortion(string.getValue()));
    }

    @Override
    public void decompile(IndentPrintStream s) {
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	string.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    	string.iterChildren(f);
    }



}
