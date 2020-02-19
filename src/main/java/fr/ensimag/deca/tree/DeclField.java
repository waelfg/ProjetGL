package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.NullOperand;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/20/20.
 */
public class DeclField extends AbstractDeclField{

    public static int nbfield = 0;
    private AbstractIdentifier name;
    private AbstractIdentifier type;
    private AbstractInitialization initialization;
    private Visibility visib;




    public DeclField(AbstractIdentifier name,
                     AbstractIdentifier type,
                     AbstractInitialization initialization,
                     Visibility visib) {
        this.name = name;
        this.type = type;
        this.initialization = initialization;
        this.visib = visib;
    }

    public Visibility getVisib() {
        return visib;
    }

    public AbstractIdentifier getType() {
        return type;
    }

    public AbstractIdentifier getName() {
        return name;
    }

    public AbstractInitialization getInitialization() {
        return initialization;
    }

    /**
     * Vérifie que le type du champ est bien défini et qu'aucun autre champ ne porte le même nom.
     * Fixe la définition du champ
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    @Override
    public void verifyDeclMembers(DecacCompiler compiler,
                                  EnvironmentExp localEnv,
                                  ClassDefinition currentClass) throws ContextualError {
        currentClass.incNumberOfFields();

        TypeDefinition defType = (TypeDefinition) compiler.predef.get(compiler.sym.create(this.type.getName().getName()));
        if (defType == null){
            throw new ContextualError("Le type du champ n'est pas défini", this.getLocation());
        }
        if (defType.getType().isNull() || defType.getType().isVoid()){
            throw new ContextualError("On ne peut pas avoir de champ de type null ou void", this.getLocation());
        }
        this.type.setDefinition(defType);
        this.type.setType(defType.getType());
        this.name.setType(defType.getType());
        FieldDefinition defField = new FieldDefinition(defType.getType(),
                                                       this.getLocation(),
                                                       this.getVisib(), currentClass, currentClass.getNumberOfFields());
        SymbolTable.Symbol key = compiler.sym.create(this.name.getName().getName());
        try{
            if (localEnv.getEnv().containsKey(key)){
                throw new EnvironmentExp.DoubleDefException();
            }
            else{
                localEnv.getEnv().put(key, defField);
            }
        }
        catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Un champ ou une méthode a déjà le même nom dans cette classe : "
                    + this.name.getName(), this.getLocation());
        }
        this.name.setDefinition(defField);
    }

    /**
     * Vérifie que l'initialisation du champ est correcte
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    @Override
    public void verifyDeclBody(DecacCompiler compiler,
                               EnvironmentExp localEnv,
                               ClassDefinition currentClass) throws ContextualError {
        this.initialization.verifyInitialization(compiler, this.type.getType(), localEnv, currentClass);
    }


    /**
     * fonction générant le code assembleur d'initialisation d'un champ
     * @param compiler
     * @param classe
     */
    @Override
    public void codeGenDeclField(DecacCompiler compiler, AbstractIdentifier classe) {
        if (type.getType().isClass()){
            int init = compiler.managerReg.affectReg(compiler);
            compiler.addInstruction(new LOAD(new NullOperand(), Register.getR(init)));
        }
        else {
            initialization.codeGenInit(compiler);
        }
        int reg = compiler.managerReg.getLastRegused();
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R1));
        compiler.addInstruction(new STORE(Register.getR(reg), new RegisterOffset(nbfield, Register.getR(1))));
        classe.getClassDefinition().nfield.put(compiler.sym.create(name.getName().getName()), nbfield);
        name.setNumfield(nbfield);
        compiler.predef.registres.put(compiler.sym.create(name.getName().toString()), new RegisterOffset(nbfield, Register.getR(1)));

        nbfield++;
    }
    
    /**
     * @param s
     * On decompile l'arbre.
     */

    @Override
    public void decompile(IndentPrintStream s) {
    	
    	if(this.visib == Visibility.PROTECTED) {
    		s.print(this.visib.name().toLowerCase());
    	}
    	s.print(" ");
    	this.type.decompile(s);
    	s.print(" ");
    	this.name.decompile(s);
    	
    	if (this.initialization.getExpression() != null ) {
    		s.print(" = ");
        	this.initialization.decompile(s);
    	}
    	
    	s.println(";");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	this.name.prettyPrint(s, prefix, false);
        this.type.prettyPrint(s, prefix, false);
        this.initialization.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    	name.iterChildren(f);
    	type.iterChildren(f);
    	initialization.iterChildren(f);
    }
}
