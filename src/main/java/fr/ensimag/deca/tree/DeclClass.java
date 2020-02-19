package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.*;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.LEA;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.STORE;
import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;
import java.util.Iterator;

/**
 * Declaration of a class (<code>class name extends superClass {members}<code>).
 *
 * @author gl49
 * @date 01/01/2020
 */
public class DeclClass extends AbstractDeclClass {

    private AbstractIdentifier name;
    private AbstractIdentifier superClass;
    private ListDeclField listField;
    private ListDeclMethod listMethod;


    public DeclClass(AbstractIdentifier name, AbstractIdentifier superClass, ListDeclField listField, ListDeclMethod listMethod) {
        Validate.notNull(name);
        Validate.notNull(superClass);
        this.name = name;
        this.superClass = superClass;
        this.listField = listField;
        this.listMethod = listMethod;
    }

    public AbstractIdentifier getSuperClass() {
        return superClass;
    }

    public AbstractIdentifier getName() {
        return name;
    }

    public int addr = 0;

    @Override
    public void decompile(IndentPrintStream s) {
        //s.print("class { ... A FAIRE ... }");
    	s.print(" class ");
    	s.print(this.name.getName().getName());

    	if (this.superClass != null && this.superClass.getName().getName() != "Object") {
    		s.print(" extends ");
    		this.superClass.decompile(s);
    	}
    	s.println(" {");
    	s.indent();
    	this.listField.decompile(s);
    	this.listMethod.decompile(s);
    	s.unindent();
    	s.println(" }");
    }

    /**
     * Vérifie que la superclass est bien définie, et qu'aucune autre classe ne porte le même nom.
     * Si c'est le cas, ajoute la classe à l'environnement Type du compiler
     * @param compiler
     * @throws ContextualError
     */
    @Override
    protected void verifyClass(DecacCompiler compiler) throws ContextualError {
        SymbolTable.Symbol keySuper = compiler.sym.create(this.superClass.getName().toString());
        TypeDefinition defTypeSuper = (TypeDefinition) compiler.predef.get(keySuper);
        if (defTypeSuper == null){
            throw new ContextualError("La superclasse n'est pas définie", this.getLocation());
        }
        if (!defTypeSuper.getType().isClass()){
            throw new ContextualError("La superclasse donnée n'est pas une classe"
                                                , this.getLocation());
        }
        ClassDefinition defClassSuper = (ClassDefinition) defTypeSuper;
        ClassType classType = new ClassType(this.name.getName(), this.getLocation(), defClassSuper);
        ClassDefinition defClass = new ClassDefinition(classType, this.getLocation(), defClassSuper);
        SymbolTable.Symbol key = compiler.sym.create(this.name.getName().toString());
        try {
            compiler.predef.put(key, defClass);
        }
        catch (EnvironmentType.DoubleDefException e){
            throw new ContextualError("Une autre classe du même nom a déjà été définie", this.getLocation());
        }
        this.name.setDefinition(defClass);
        this.name.setType(classType);
    }

    /**
     * Vérifie que la superclasse est bien définie, initialise les champs NumberofFields et NumberofMethods de la classe
     * Vérifie ensuite que les déclarations de la liste de champs et de la liste de méthodes sont correctes
     * @param compiler
     * @throws ContextualError
     */
    @Override
    protected void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError {
        SymbolTable.Symbol keySuper = compiler.sym.create(this.superClass.getName().toString());
        if (compiler.predef.get(keySuper) == null){
            throw new ContextualError("La superclasse n'est pas définie", this.getLocation());
        }
        if (!compiler.predef.get(keySuper).getType().isClass())
        {
            throw new ContextualError("La superclasse donnée n'est pas une classe", this.getLocation());
        }
        int nof = 0;
        int nom = 0;
        if (this.superClass.getClassDefinition() != null){
            nof = superClass.getClassDefinition().getNumberOfFields();
            nom = superClass.getClassDefinition().getNumberOfMethods();
        }
        this.name.getClassDefinition().setNumberOfFields(nof);
        this.name.getClassDefinition().setNumberOfMethods(nom);
        EnvironmentExp classEnv = this.name.getClassDefinition().getMembers();
        this.listField.verifyDeclMembers(compiler, classEnv, this.name.getClassDefinition());
        this.listMethod.verifyDeclMembers(compiler, classEnv, this.name.getClassDefinition());
    }

    /**
     * Vérifie que les initialisations des champs et les corps des méthodes sont corrects
     * @param compiler
     * @throws ContextualError
     */
    @Override
    protected void verifyClassBody(DecacCompiler compiler) throws ContextualError {
        EnvironmentExp classEnv = this.name.getClassDefinition().getMembers();
        this.listField.verifyDeclBody(compiler, classEnv, this.name.getClassDefinition());
        this.listMethod.verifyDeclBody(compiler, classEnv, this.name.getClassDefinition());
    }


    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	this.name.prettyPrint(s, prefix, false);
        this.superClass.prettyPrint(s, prefix, false); 
        this.listField.prettyPrint(s, prefix, false);
        this.listMethod.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    	name.iterChildren(f);
    	superClass.iterChildren(f);
    	listField.iterChildren(f);
    	listMethod.iterChildren(f);
        //throw new UnsupportedOperationException("Not yet supported");
    }



    @Override
    public void codeGenclassTable(DecacCompiler compiler) {
        int reg = compiler.managerReg.getLastRegused() ;
        compiler.addInstruction(new LOAD(new ImmediateInteger(0), Register.getR(reg)));
        compiler.addInstruction(new STORE(Register.getR(reg), new RegisterOffset(compiler.pointeur_pile,Register.GB)));
        name.getClassDefinition().setAdresspile(compiler.pointeur_pile);
        compiler.pointeur_pile++ ;
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        compiler.addInstruction(new STORE(Register.getR(reg), new RegisterOffset(compiler.pointeur_pile,Register.GB)));
        name.getClassDefinition().setAdresspile(compiler.pointeur_pile);
        compiler.pointeur_pile++ ;
        Label codemth = new Label("code." +this.getName().getName()) ;
        compiler.addLabel(codemth);
        this.codeGenDeclClass(compiler);
    }


    /**
     * fonction générant en assembleur la première appartition d'une classe
     * @param compiler
     */
    @Override
    public void codeGenDeclClass(DecacCompiler compiler) {
        compiler.addComment("Construction de la Table des Méthodes");
        SymbolTable.Symbol key = compiler.sym.create(superClass.getName().getName());
        if (superClass.getName().getName().equals("Object")){
            compiler.addInstruction(new LEA(new RegisterOffset(1, Register.GB), Register.R0));
        }

        else{
            DAddr reg = compiler.predef.registres.get(key);
            compiler.addInstruction(new LEA(reg, Register.R0));
        }
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.pointeur_pile, Register.GB)));
        name.getClassDefinition().setAdresspile(compiler.pointeur_pile);
        compiler.pointeur_pile++;
        compiler.addInstruction(new LOAD(new LabelOperand(new Label("code.Object.equals")), Register.R0));
        compiler.addInstruction(new STORE(Register.R0, new RegisterOffset(compiler.pointeur_pile, Register.GB)));
        compiler.pointeur_pile++;
        listMethod.codeGenlistMethod(compiler, name);
    }



    @Override
    public void setPile_address(RegisterOffset registerOffset) {

    }

    /**
     * fonction générant l'initialisation des différents champs d'une classe
     * @param compiler
     */

    @Override
    public void codeGenDeclFields(DecacCompiler compiler) {
            Iterator<AbstractDeclField> it = listField.iterator();
            compiler.addComment("initialisation des champs de la classe " + getName().getName());
        compiler.addLabel(new Label("init." + getName().getName().toString()));
            DeclField.nbfield = 1;
            if (superClass!= null && superClass.getName().getName() != "Object") {
                name.getClassDefinition().nfield = superClass.getClassDefinition().nfield;
            }
            if (superClass != null && superClass.getName().getName()!="Object") {
                compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.R0));
                compiler.addInstruction(new PUSH(Register.R0));
                if (!superClass.getName().getName().equals("Object")) {
                    compiler.addInstruction(new BSR(new LabelOperand(new Label("init." + superClass.getName().getName()))));
                }
                compiler.addInstruction(new SUBSP(1));

            }

        if (superClass!= null && superClass.getName().getName() != "Object") {
            DeclField.nbfield += superClass.getClassDefinition().getNumberOfFields();
        }
            while (it.hasNext()) {

                AbstractDeclField field = it.next();
                field.codeGenDeclField(compiler, name);

            }
            compiler.addInstruction(new RTS());
    }


}
