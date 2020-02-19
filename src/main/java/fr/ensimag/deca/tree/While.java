package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.Label;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import org.apache.commons.lang.Validate;

/**
 *
 * @author gl49
 * @date 01/01/2020
 */
public class While extends AbstractInst {
    private AbstractExpr condition;
    private ListInst body;
    private static int ind = 0;

    public AbstractExpr getCondition() {
        return condition;
    }

    public ListInst getBody() {
        return body;
    }

    public While(AbstractExpr condition, ListInst body) {
        Validate.notNull(condition);
        Validate.notNull(body);
        this.condition = condition;
        this.body = body;
    }

    /**
     * fonction générant le code assembleur pour une boucle while
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addLabel(new Label("while"+ind));
        condition.codeGenInst(compiler);
        int cond = compiler.managerReg.getLastRegused();
        compiler.addInstruction(new CMP(1, Register.getR(cond)));
        compiler.addInstruction(new BNE(new Label("Finw"+ind)));
        body.codeGenListInst(compiler);
        compiler.addInstruction(new BRA(new Label("while"+ind)));
        compiler.addLabel(new Label("Finw"+ind));
        //throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     *Vérifie que la condition et le corps sont corrects.
     * @param compiler contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass
     *          corresponds to the "class" attribute (null in the main bloc).
     * @param returnType
     * @throws ContextualError
     */
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
        this.getCondition().verifyCondition(compiler, localEnv, currentClass);
        this.getBody().verifyListInst(compiler, localEnv, currentClass, returnType);
    }

    
    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire un while
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("while (");
        getCondition().decompile(s);
        s.println(") {");
        s.indent();
        getBody().decompile(s);
        s.unindent();
        s.print("}");
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        condition.iter(f);
        body.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        body.prettyPrint(s, prefix, true);
    }

}
