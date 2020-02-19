package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.log4j.Logger;

import java.util.Iterator;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class ListDeclClass extends TreeList<AbstractDeclClass> {
    private static final Logger LOG = Logger.getLogger(ListDeclClass.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclClass c : getList()) {
            c.decompile(s);
            s.println();
        }
    }


    /**
     * fonction générant tables des méthodes et initialisation des classes en assembleur
     * @param compiler
     */
    void codeGenListDeclClass(DecacCompiler compiler) {
        Identifier name = new Identifier(compiler.sym.create("Object"));
        ListDeclField field = new ListDeclField();
        ListDeclMethod meth = new ListDeclMethod();


        Iterator<AbstractDeclClass> it = this.iterator();
        int i = 1;

        compiler.addInstruction(new LOAD(new NullOperand(), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(1, Register.GB)));
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(2, Register.GB)));
        while (it.hasNext()){
            AbstractDeclClass var = it.next();
            var.setPile_address(new RegisterOffset(i, Register.GB));
            var.codeGenclassTable(compiler);
            int last = compiler.managerReg.getLastRegused();
            compiler.predef.registres.put(compiler.sym.create(var.getName().getName().toString()), new RegisterOffset(i, Register.GB));
            compiler.addInstruction(new STORE(Register.getR(last), new RegisterOffset(i,Register.GB)));
            compiler.managerReg.freeReg(last, compiler);

            i++;
        }

        compiler.addInstruction(new TSTO(new ImmediateInteger(i)));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));
        compiler.addInstruction(new ADDSP(i));
    }

    /**
     * Pass 1 of [SyntaxeContextuelle]
     */
    void verifyListClass(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listClass: start");
        for (AbstractDeclClass classe : this.getList()){
            classe.verifyClass(compiler);
        }

    }

    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListClassMembers(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass classe : this.getList()){
            classe.verifyClassMembers(compiler);
        }
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListClassBody(DecacCompiler compiler) throws ContextualError {
        for (AbstractDeclClass classe : this.getList()){
            classe.verifyClassBody(compiler);
        }
        LOG.debug("verify listClass: end");
    }


    /**
     * fonction générant le code de déclaration de l'ensemble des fields d'une classe
     * @param compiler
     */
    public void codeGenDeclField(DecacCompiler compiler) {
        Iterator<AbstractDeclClass> it = iterator();
        while (it.hasNext()){

            AbstractDeclClass cl = it.next();
            cl.codeGenDeclFields(compiler);
        }
    }
}
