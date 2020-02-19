package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.LabelOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.util.List;

/**
 * Created by saunievi on 1/23/20.
 */
public class SelectionMethod extends AbstractExpr {

    private AbstractExpr expr;
    private AbstractIdentifier method;
    private ListExpr listeparam;

    public SelectionMethod(AbstractExpr expr, AbstractIdentifier method, ListExpr listeparam) {
        Validate.notNull(expr);
        Validate.notNull(method);
        this.expr = expr;
        this.method = method;
        this.listeparam = listeparam;
    }

    
    /**
     * @param s
     * On decompile l'arbre.
     */
    @Override
    public void decompile(IndentPrintStream s) {
    	this.expr.decompile(s);
    	s.print(".");
    	s.print(this.method.getName().getName());
    	s.print("(");
    	this.listeparam.decompile();
    	s.print(")");
    }

    /**
     * Vérifie que le terme de gauche est bien une classe, que la méthode est bien définie, et que la signature
     * correspond aux paramètres donnés. Fixe le type au type de retour de la méthode.
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
        Type exprType = expr.verifyExpr(compiler, localEnv, currentClass);
        if (!exprType.isClass()){
            throw new ContextualError("On ne peut appeler une méthode d'un objet"
                    +"qui n'est pas une classe", this.getLocation());
        }
        SymbolTable.Symbol key = compiler.sym.create(exprType.getName().toString());
        ClassDefinition exprDef = (ClassDefinition) compiler.predef.get(key);
        if (exprDef == null){
            throw new ContextualError("On ne peut appeler une méthode que d'une classe définie",
                                        this.getLocation());
        }
        Signature sig = new Signature();
        for (AbstractExpr param : listeparam.getList()){
            sig.add(param.verifyExpr(compiler, localEnv, currentClass));
        }
        if (method.getDefinition() == null){
            SymbolTable.Symbol keyMethod = compiler.sym.create(method.getName().toString());
            if (compiler.predef.get(keyMethod) != null){
                method.setDefinition(compiler.predef.get(keyMethod));
            }
            else if (exprDef.getMembers().getEnv().get(keyMethod) != null) {
                method.setDefinition(exprDef.getMembers().getEnv().get(keyMethod));
            }
            else{
                throw new ContextualError ("La méthode n'est pas définie", getLocation());
            }
        }
        MethodDefinition defMethod = (MethodDefinition) method.getDefinition();
        Signature sigTheo = defMethod.getSignature();
        if (sig.size() != sigTheo.size()){
            throw new ContextualError("Le nombre de paramètres de la méthode appelée"
                                        + "ne correspond pas à celui de la méthode déclarée", this.getLocation());
        }
        for (int i = 0; i < sigTheo.size(); i++){
            if(!sigTheo.paramNumber(i).sameType(sig.paramNumber(i))){
                if (sigTheo.paramNumber(i).isFloat() && sig.paramNumber(i).isInt()){
                    int pos = 0;
                    for (AbstractExpr param : listeparam.getList()){
                        if (i == pos){
                            ConvFloat cast = new ConvFloat(param);
                            cast.setType(sigTheo.paramNumber(i));
                            listeparam.set(i, cast);
                        }
                        pos++;
                    }
                }
                else{
                    throw new ContextualError("Le type du paramètre donné ne "
                                                + "correspond pas à celui du paramètre attendu", this.getLocation());
                }
            }
        }
        Type retour = defMethod.getType();
        this.setType(retour);
        return retour;
    }


    /**
     * fonction générant le code assembleur pour l'appel d'une méthode d'une classe
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        compiler.addInstruction(new ADDSP(listeparam.size()+1));
        int ind = compiler.managerReg.affectReg(compiler);
        compiler.addInstruction(new LOAD(expr.getDval(), Register.getR(ind)));
        compiler.addInstruction(new STORE(Register.getR(ind), new RegisterOffset(0, Register.SP)));
        List<AbstractExpr> par = listeparam.getList();
        for (int i = 1; i<=listeparam.size(); i++){
            compiler.addInstruction(new LOAD(par.get(i-1).getDval(), Register.getR(ind)));
            compiler.addInstruction(new STORE(Register.getR(ind), new RegisterOffset(i, Register.SP)));
        }
        compiler.addInstruction(new LOAD(expr.getDval(), Register.getR(ind)));
        compiler.addInstruction(new CMP(null, Register.getR(ind)));
        compiler.addInstruction(new BEQ(new Label("dereferencement.null")));
        compiler.addInstruction(new LOAD(new RegisterOffset(0, Register.getR(ind)), Register.getR(ind)));
        compiler.addInstruction(new BSR(method.getPile_address(compiler)));
        compiler.addInstruction(new SUBSP(listeparam.size()+1));
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        method.prettyPrint(s, prefix, false);
        listeparam.prettyPrint(s, prefix, true);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        method.iter(f);
        listeparam.iter(f);
    }
}
