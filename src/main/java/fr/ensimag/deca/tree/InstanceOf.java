package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/24/20.
 */
public class InstanceOf extends AbstractExpr {
    AbstractExpr objet;
    AbstractIdentifier type;

    public InstanceOf(AbstractExpr objet, AbstractIdentifier type) {
        Validate.notNull(objet);
        Validate.notNull(type);
        this.objet = objet;
        this.type = type;
    }





    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un instance of
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("(");
        s.print(this.objet.decompile());
        s.print("InstanceOf");
        s.print(this.type.decompile());
        s.print(")");
    }

    /**
     * Vérifie que les opérandes sont corrects, Fixe le type à boolean
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return
     * @throws ContextualError
     */
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv, ClassDefinition currentClass) throws ContextualError {
       objet.verifyExpr(compiler, localEnv, currentClass);
       type.verifyExpr(compiler, localEnv, currentClass);
       Type bool = compiler.predef.get(compiler.sym.create("boolean")).getType();
       this.setType(bool);
       return bool;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        throw new UnsupportedOperationException("Not yet supported");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        throw new UnsupportedOperationException("Not yet supported");
    }
}
