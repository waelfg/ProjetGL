package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.util.Iterator;

/**
 * 
 * @author gl49
 * @date 01/01/2020
 */
public class ListInst extends TreeList<AbstractInst> {

    /**
     * Implements non-terminal "list_inst" of [SyntaxeContextuelle] in pass 3
     * @param compiler contains "env_types" attribute
     * @param localEnv corresponds to "env_exp" attribute
     * @param currentClass 
     *          corresponds to "class" attribute (null in the main bloc).
     * @param returnType
     *          corresponds to "return" attribute (void in the main bloc).
     */    
    public void verifyListInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        Iterator<AbstractInst> it = iterator();
        while(it.hasNext()){
            it.next().verifyInst(compiler, localEnv, currentClass, returnType);
        }
    }


    /**
     * fonction générant le code assembleur d'un ensemble d'instructions
     * @param compiler
     */
    public void codeGenListInst(DecacCompiler compiler) {
        for (AbstractInst i : getList()) {
            i.codeGenInst(compiler);
        }
    }

    @Override
    public void decompile(IndentPrintStream s) {
    	s.indent();
        for (AbstractInst i : getList()) {
        	s.indent();
            i.decompileInst(s);
            s.println("");
            s.unindent();
            s.println();
        }
        s.unindent();
    }
}
