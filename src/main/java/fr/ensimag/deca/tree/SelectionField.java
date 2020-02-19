package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.syntax.DecaParser;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.DAddr;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/23/20.
 */
public class SelectionField extends AbstractLValue {

    private AbstractExpr expr;
    private AbstractIdentifier ident;
    private DAddr pile_address;


    public DAddr getPile_address() {
        return pile_address;
    }

    public void setPile_address(DAddr pile_address) {
        this.pile_address = pile_address;
    }

    public SelectionField(AbstractExpr expr, AbstractIdentifier ident) {
        Validate.notNull(expr);
        Validate.notNull(ident);
        this.expr = expr;
        this.ident = ident;
    }

    @Override
    public DAddr getPile_address(DecacCompiler compiler) {
        return ident.addr;
    }
    
    /**
     * @param s
     * On decompile l'arbre.
     */

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
    	s.print("."+ident.getName().toString());
    }

    /**
     * Vérifie que le terme de gauche est bien une classe, que le champ est bien défini, et qu'on a le droit de l'appeler
     * dans la classe actuelle. Fixe le type à celui du champ.
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
            throw new ContextualError("On ne peut appeler un champ d'un objet"
                                        +"qui n'est pas une classe", this.getLocation());
        }
        SymbolTable.Symbol key = compiler.sym.create(exprType.getName().toString());
        ClassDefinition exprDef = (ClassDefinition) compiler.predef.get(key);
        if (ident.getDefinition() == null){
            SymbolTable.Symbol keyIdent = compiler.sym.create(ident.getName().toString());
            if (compiler.predef.get(keyIdent) != null){
                ident.setDefinition(compiler.predef.get(keyIdent));
            }
            else if (exprDef.getMembers().getEnv().get(keyIdent) != null) {
                ident.setDefinition(exprDef.getMembers().getEnv().get(keyIdent));
            }
            else{
                throw new ContextualError ("Le champ n'est pas défini", getLocation());
            }
        }
        Type idenType = ident.getDefinition().getType();
        FieldDefinition identDef = ident.getFieldDefinition();
        Visibility visib = identDef.getVisibility();
        if ((visib == Visibility.PROTECTED) && ((currentClass == null)
                || (!currentClass.getType().isSubClassOf(exprDef.getType())))) {
            throw new ContextualError("On ne peut appeler le champ"
                    + " en dehors de sa classe ou "
                    + "d'une sous-classe", this.getLocation());

        }
        this.expr.setType(exprType);
        this.setType(idenType);
        return idenType;
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        ident.prettyPrint(s, prefix, true);
    }


    /**
     * fonction générant le code assembleur pour l'appel d'un champ d'une classe
     * @param compiler
     */
    @Override
    protected void codeGenInst(DecacCompiler compiler) {
        expr.codeGenInst(compiler);
        SymbolTable.Symbol key = compiler.sym.create(expr.getType().toString());
        DVal reg = compiler.predef.registres.get(key);
        int indr = compiler.managerReg.affectReg(compiler);
        compiler.addInstruction(new LOAD(reg, Register.getR(indr)));
        key = compiler.sym.create(ident.getName().getName());
        System.out.println();
        SymbolTable.Symbol classe = compiler.sym.create(expr.getType().toString());
        ClassDefinition classd = (ClassDefinition) compiler.predef.get(classe);
        int nf = classd.nfield.get(key);
        int nev = compiler.managerReg.affectReg(compiler);
        ident.getPile_address(compiler);
        setPile_address(new RegisterOffset(nf, Register.getR(indr)));
        compiler.addInstruction(new LOAD(new RegisterOffset(nf, Register.getR(indr)), Register.getR(nev)));
        compiler.luse = new RegisterOffset(nf, Register.getR(indr));

    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        ident.iter(f);
    }
}
