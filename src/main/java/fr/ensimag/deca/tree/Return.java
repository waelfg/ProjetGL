package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/24/20.
 */
public class Return extends AbstractInst {

    private AbstractExpr retourPrat;

    public Return(AbstractExpr retourPrat) {
        this.retourPrat = retourPrat;
    }

    /**
     * Vérifie que l'on est bien dans une méthode mais qui ne renvoie pas void.
     * Vérifie que l'objet retourné est bien du bon type
     * Fixe le type à ce dernier.
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass
     *          corresponds to the "class" attribute (null in the main bloc).
     * @param returnType
     * @throws ContextualError
     */
    @Override
    protected void verifyInst(DecacCompiler compiler,
                              EnvironmentExp localEnv,
                              ClassDefinition currentClass,
                              Type returnType) throws ContextualError {
        Type pratType = this.retourPrat.verifyExpr(compiler, localEnv, currentClass);
        if (returnType.isVoid() || !(currentClass.isClass())) {
            throw new ContextualError("On ne peut pas faire de return dans la main"
                                        +" ou dans une classe qui doit renvoyer void", this.getLocation());
        }
        if (pratType.isInt() && returnType.isFloat()){
            ConvFloat conv = new ConvFloat(retourPrat);
            conv.setType(conv.verifyExpr(compiler, localEnv, currentClass));
            this.retourPrat = conv;
        }
        else if (!pratType.isSubType(returnType)){
            throw new ContextualError("Le type renvoyé en pratique doit correspondre "
                                        + "à un sous-type du type attendu", this.getLocation());
        }
        this.retourPrat.setType(returnType);


    }
    
    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un return
     */

    @Override
    public void decompile(IndentPrintStream s) {
    	s.print("return ");
    	retourPrat.decompile(s);
    	s.print(";");
    }

    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        retourPrat.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        retourPrat.iterChildren(f);
    }
}
