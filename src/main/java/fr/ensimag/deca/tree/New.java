package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/24/20.
 */
public class New extends AbstractExpr {

    AbstractIdentifier nom;

    public New(AbstractIdentifier nom) {
        Validate.notNull(nom);
        this.nom = nom;
    }

    public AbstractIdentifier getNom() {
        return nom;
    }

    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un new
     */
    @Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("Not yet implemented");
    	s.print("new ");
    	nom.decompile(s);
    	s.print("()");
    }

    /**
     * Vérifie que le type est bien une classe définie. Fixe le type à cette classe.
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
    public Type verifyExpr(DecacCompiler compiler,
                           EnvironmentExp localEnv,
                           ClassDefinition currentClass) throws ContextualError {
        Definition def = compiler.predef.get(compiler.sym.create(nom.getName().toString()));
        if (def == null){
            throw new ContextualError("On ne peut pas déclarer avec new un objet"
                                        + " dont le type n'est pas implémenté", this.getLocation());
        }
        if (!def.isClass()){
            throw new ContextualError("On ne peut déclarer que des classes avec new", this.getLocation());
        }
        this.setType(def.getType());
        return def.getType();
    }

    /**
     * fonction générant la création d'un nouvel objet avec la fonction new
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addComment("new");
        SymbolTable.Symbol key = compiler.sym.create(nom.getName().toString());
        ClassDefinition classe = (ClassDefinition) compiler.predef.get(key);
        int reg = compiler.managerReg.affectReg(compiler);
        compiler.addInstruction(new NEW(classe.getNumberOfFields(), Register.getR(reg)));
        compiler.addInstruction(new LEA(new RegisterOffset(classe.getAdresspile(), Register.GB), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(0, Register.getR(reg))));
        compiler.addInstruction(new PUSH(Register.getR(reg)));
        compiler.addInstruction(new BSR(new LabelOperand(new Label("init."+nom.getName().getName()))));
        compiler.addInstruction(new POP(Register.getR(reg)));
        key = compiler.sym.create(nom.getName().toString());
        DAddr addr = compiler.predef.registres.get(key);
        compiler.addInstruction(new STORE(Register.getR(reg), addr));
        compiler.managerReg.freeReg(reg, compiler);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        nom.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        nom.iterChildren(f);
    }
}
