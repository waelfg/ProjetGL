package fr.ensimag.deca.tree;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
/**
 * Created by saunievi on 1/20/20.
 */
public class DeclParam extends AbstractDeclParam {

    private AbstractIdentifier name;
    private AbstractIdentifier type;

    public DeclParam(AbstractIdentifier name, AbstractIdentifier type) {
        Validate.notNull(name);
        Validate.notNull(type);
        this.name = name;
        this.type = type;
    }

    public AbstractIdentifier getType() {
        return type;
    }

    public void setType(AbstractIdentifier type) {
        this.type = type;
    }

    /**
     * Vérifie que le type du paramètre est bien défini.
     * Fixe sa définition et renvoie son type
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @return
     * @throws ContextualError
     */
    @Override
    public Type verifyDeclMembers(DecacCompiler compiler,
                                  EnvironmentExp localEnv,
                                  ClassDefinition currentClass) throws ContextualError {
        TypeDefinition defType = (TypeDefinition) compiler.predef.get(compiler.sym.create(this.type.getName().getName()));
        if (defType == null){
            throw new ContextualError("Le type du paramètre n'est pas défini", this.getLocation());
        }
        if (defType.getType().isNull() || defType.getType().isVoid()){
            throw new ContextualError("On ne peut pas avoir de paramètre de type null ou void", this.getLocation());
        }
        this.type.setDefinition(defType);
        this.type.setType(defType.getType());

        return defType.getType();
    }

    /**
     * Vérifie que la variable est bien initialisée et qu'aucune autre variable n'a le même nom.
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    @Override
    public void verifyDeclBody(DecacCompiler compiler,
                               EnvironmentExp localEnv,
                               ClassDefinition currentClass) throws ContextualError {
        VariableDefinition def = new VariableDefinition(this.type.getType(), this.name.getLocation());
        SymbolTable.Symbol key = compiler.sym.create(this.name.getName().toString());
        try{
            if (localEnv.getEnv().containsKey(key)){
                throw new EnvironmentExp.DoubleDefException();
            }
            else{
                localEnv.getEnv().put(key, def);
            }
        }
        catch (EnvironmentExp.DoubleDefException ex) {
            throw new ContextualError("Une autre variable déjà déclarée a le même nom", this.getLocation());
        }
    }

    
    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour declarer les parametres
     */
    @Override
    public void decompile(IndentPrintStream s) {
    	s.print(this.type.getName().getName()+" ");
    	s.print(this.name.getName().getName());
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	name.prettyPrint(s, prefix, false);
    	type.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        name.iterChildren(f);
        type.iterChildren(f);

    }
}
