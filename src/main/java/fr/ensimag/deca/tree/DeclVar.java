package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.EnvironmentType;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import fr.ensimag.ima.pseudocode.DAddr;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.Validate;

/**
 * @author gl49p
 * @date 01/01/2020
 */
public class DeclVar extends AbstractDeclVar {

    
    final private AbstractIdentifier type;
    final private AbstractIdentifier varName;
    final private AbstractInitialization initialization;
    private DAddr pile_address;

    public DAddr getPile_address() {
        return pile_address;
    }

    public void setPile_address(DAddr pile_address) {
        this.pile_address = pile_address;
    }

    public DAddr getPile_address(DecacCompiler compiler) {
        SymbolTable.Symbol key = compiler.sym.create(varName.getName().toString());
        DAddr addr = compiler.predef.registres.get(key);
        setPile_address(addr);
        return addr;
    }


    public DeclVar(AbstractIdentifier type, AbstractIdentifier varName, AbstractInitialization initialization) {
        Validate.notNull(type);
        Validate.notNull(varName);
        Validate.notNull(initialization);
        this.type = type;
        this.varName = varName;
        this.initialization = initialization;
    }


    /**
     * fonction générante le code assembleur d'une variable du programme en cours
     * @param compiler
     */
    @Override
    void codeGenDeclVar(DecacCompiler compiler){
        initialization.codeGenInit(compiler);
        Symbol key = compiler.sym.create(varName.getName().toString());
        compiler.predef.registres.put(key , pile_address);
        varName.setDval(pile_address);
        varName.setPile_address(pile_address);

    }

    /**
     * Vérifie que le type est bien défini, et qu'aucune autre variable n'a le même nom.
     * Fixe la définition
     * @param compiler contains "env_types" attribute
     * @param localEnv
     *   its "parentEnvironment" corresponds to the "env_exp_sup" attribute
     *   in precondition, its "current" dictionary corresponds to
     *      the "env_exp" attribute
     *   in postcondition, its "current" dictionary corresponds to
     *      the synthetized attribute
     * @param currentClass
     * @throws ContextualError
     */
    @Override
    protected void verifyDeclVar(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        try {
            Definition testtype = compiler.predef.get(compiler.sym.create(type.getName().toString()));
            if (testtype == null){
                throw new ContextualError("type non défini", getLocation());
            }
            VariableDefinition def = new VariableDefinition(testtype.getType(), varName.getLocation());
            SymbolTable.Symbol key = compiler.sym.create(varName.getName().toString());
            if (localEnv.getEnv().containsKey(key)) {
                throw new EnvironmentExp.DoubleDefException();
            } else {
                localEnv.getEnv().put(key, def);
            }
            Definition deft = compiler.predef.get(compiler.sym.create(type.getName().toString()));
            type.setDefinition(deft);
            initialization.verifyInitialization(compiler, type.getDefinition().getType(), localEnv, currentClass);
            varName.setDefinition(def);
            varName.verifyType(compiler);
        } catch (EnvironmentExp.DoubleDefException ex) {
            //Logger.getLogger(DeclVar.class.getName()).log(Level.SEVERE, null, ex);
            throw new ContextualError("Une autre variable déjà déclarée a le même nom", this.getLocation());
        }
    }
    //protected void codeGenDeclVar() {}
    
    
    /**
     * @param s
     * On decompile l'arbre.
     */
    @Override
    public void decompile(IndentPrintStream s) {
        //s.print(type.getName() + " ");
        //s.print(varName.getName().toString());
        
        //if (this.initialization.getExpression() != null) {
        	//s.print(" = ");
        	//initialization.decompile(s);
        //}
        //s.println();
        //throw new UnsupportedOperationException("not yet implemented");
        this.type.decompile(s);
        s.print(" ");
        this.varName.decompile(s);
        if (this.initialization.getExpression() != null) {
    	s.print(" = ");
    	initialization.decompile(s);
        }
        s.println(";");
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        type.iter(f);
        varName.iter(f);
        initialization.iter(f);
    }
    
    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        type.prettyPrint(s, prefix, false);
        varName.prettyPrint(s, prefix, false);
        initialization.prettyPrint(s, prefix, true);
    }
    
    
    
}
