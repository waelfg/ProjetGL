package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import org.apache.commons.lang.Validate;

/**
 * Full if/else if/else statement.
 *
 * @author gl49
 * @date 01/01/2020
 */
public class IfThenElse extends AbstractInst {

    private static int ind = 0;
    private final AbstractExpr condition;
    private final ListInst thenBranch;
    private ListInst elseBranch;
    private static int numif = 0;
    private static boolean racine = true;

    public IfThenElse(AbstractExpr condition, ListInst thenBranch, ListInst elseBranch) {
        Validate.notNull(condition);
        Validate.notNull(thenBranch);
        Validate.notNull(elseBranch);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }


    /**
     * Vérifie que la condition, et que la partie then et else de la structure sont correctes.
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
        this.condition.verifyCondition(compiler, localEnv, currentClass);
        this.thenBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
        this.elseBranch.verifyListInst(compiler, localEnv, currentClass, returnType);
    }


    /**
     * permet de générer les boucles if then else en assembleur
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        int maxlesif = 0;
        condition.codeGenInst(compiler);
        ind++;
        if (racine) {
            racine = false;
            numif++;
            int prevind = ind;

            int cond = compiler.managerReg.getLastRegused();
            compiler.addInstruction(new CMP(0, Register.getR(cond)));;
            compiler.addInstruction(new BEQ(new Label("else"+ numif + ind)));
            thenBranch.codeGenListInst(compiler);
            compiler.addInstruction(new BRA(new Label("fin_if" + numif + ind)));
            compiler.addLabel(new Label("else" + numif + ind));
            compiler.managerReg.freeReg(cond, compiler);
            elseBranch.codeGenListInst(compiler);
            if (ind >= maxlesif) {
                maxlesif = ind;
            }
            compiler.addLabel(new Label("fin_if" + numif + ind));
            ind--;
            ind = maxlesif + prevind +1;
            racine = true;
        }
        else {

            int cond = compiler.managerReg.getLastRegused();
            compiler.addInstruction(new CMP(0, Register.getR(cond)));

            compiler.addInstruction(new BEQ(new Label("else" + numif + (ind))));
            thenBranch.codeGenListInst(compiler);
            compiler.addInstruction(new BRA(new Label("fin_if" + numif + (ind))));
            compiler.addLabel(new Label("else" + numif + (ind)));
            compiler.managerReg.freeReg(cond, compiler);
            elseBranch.codeGenListInst(compiler);
            if (ind >= maxlesif) {
                maxlesif = ind;
            }
            compiler.addLabel(new Label("fin_if" + numif + (ind)));
            ind--;
        }
    }

    
    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour faire if/then/else
     */
    @Override
    public void decompile(IndentPrintStream s) {
        s.print("if (");
        condition.decompile();
        s.print(") {\n");
        s.indent();
        thenBranch.decompile(s);
        s.print("} \n");
        s.unindent();
        s.print("else {\n");
        s.indent();
        elseBranch.decompile();
        s.print("}\n");
        s.unindent();
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        condition.iter(f);
        thenBranch.iter(f);
        elseBranch.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        condition.prettyPrint(s, prefix, false);
        thenBranch.prettyPrint(s, prefix, false);
        elseBranch.prettyPrint(s, prefix, true);
    }
}
