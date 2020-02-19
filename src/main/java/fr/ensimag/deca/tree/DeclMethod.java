package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.*;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

import java.io.PrintStream;

/**
 * Created by saunievi on 1/20/20.
 */
public class DeclMethod extends AbstractDeclMethod {

    private AbstractIdentifier name;
    private AbstractIdentifier type;
    private AbstractMethodBody methodBody;
    private ListDeclParam listParam;


    public DeclMethod(AbstractIdentifier name, AbstractIdentifier type, AbstractMethodBody methodBody, ListDeclParam listParam) {
        Validate.notNull(name);
        Validate.notNull(type);
        Validate.notNull(methodBody);
        Validate.notNull(listParam);
        this.name = name;
        this.type = type;
        this.methodBody = methodBody;
        this.listParam = listParam;
    }


    public AbstractIdentifier getName() {
        return name;
    }

    public AbstractIdentifier getType() {
        return type;
    }


    /**
     * Vérifie que le type de retour de la méthode est bien défini, qu'aucun champ ou méthode ne possède le même nom,
     * que la signature et le type de retour de la méthode sont cohérents en cas de redéfinition.
     * Fixe la définition
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    @Override
    public void verifyDeclMembers(DecacCompiler compiler,
                                  EnvironmentExp localEnv,
                                  ClassDefinition currentClass) throws ContextualError {
        TypeDefinition defType = (TypeDefinition) compiler.predef.get(compiler.sym.create(this.type.getName().getName()));
        if (defType == null){
            throw new ContextualError("Le type de retour de la méthode n'est pas défini", this.getLocation());
        }
        if (defType.getType().isNull()){
            throw new ContextualError("Une méthode ne peut pas renvoyer un type null", this.getLocation());
        }
        this.type.setDefinition(defType);
        this.type.setType(defType.getType());
        Type retour = this.type.getType();
        this.name.setType(retour);
        type.setDefinition(new TypeDefinition(retour, Location.BUILTIN));
        Signature sig = listParam.verifyDeclMembers(compiler, localEnv, currentClass);
        SymbolTable.Symbol key = compiler.sym.create(this.name.getName().getName());
        EnvironmentExp envParent = localEnv.getParentEnvironment();
        Definition dejadef = envParent.getEnv().get(key);
        MethodDefinition defMethod;
        if (!(dejadef == null)){
            MethodDefinition defSuperMethod = dejadef.asMethodDefinition("Une méthode de même "
                                                                         + "nom a déjà été déclaré"
                                                                         + "dans cette classe :"
                                                                         + this.getName(), this.getLocation());
            if(!sig.equals(defSuperMethod.getSignature())){
                throw new ContextualError("La signature de la méthode à définir "
                                            +" ne correspond pas à celle de la méthode héritée", this.getLocation());
            }
            if(!retour.isSubType(defSuperMethod.getType())){
                throw new ContextualError("Le type de retour de la méthode"
                                            +" doit être un sous-type du type de retour "
                                            +"de la méthode héritée", this.getLocation());
            }
            defMethod = new MethodDefinition(retour,
                    this.getLocation(),
                    sig,
                    defSuperMethod.getIndex());
        }
        else{
            currentClass.incNumberOfMethods();
            defMethod = new MethodDefinition(retour,
                    this.getLocation(),
                    sig,
                    currentClass.getNumberOfMethods() + currentClass.getNumberOfFields());
        }
        try{
            if (localEnv.getEnv().containsKey(key)){
                throw new EnvironmentExp.DoubleDefException();
            }
            else{
                localEnv.getEnv().put(key, defMethod);
            }
        }
        catch (EnvironmentExp.DoubleDefException e) {
            throw new ContextualError("Une méthode ou un champ a déjà le même nom dans cette classe : "
                    + this.name.getName(), this.getLocation());
        }
        this.name.setDefinition(defMethod);
    }

    /**
     * Vérifie que la liste de paramètres et le corps de la méthode sont corrects.
     * @param compiler
     * @param localEnv
     * @param currentClass
     * @throws ContextualError
     */
    @Override
    public void verifyDeclBody(DecacCompiler compiler,
                               EnvironmentExp localEnv,
                               ClassDefinition currentClass) throws ContextualError {
        EnvironmentExp envMethod = new EnvironmentExp(localEnv);
        this.listParam.verifyDeclBody(compiler, envMethod, currentClass);
        this.methodBody.verifyMethodBody(compiler, envMethod, currentClass, this.name.getType());
    }


    /**
     * fonction générant le code assembleur d'une méthode
     * @param compiler
     * @param classname
     */
    @Override
    public void codeGenMethod(DecacCompiler compiler, AbstractIdentifier classname) {
        compiler.addLabel(new Label("code."+classname.getName().getName()+"."+name.getName().getName()));
        compiler.addInstruction(new TSTO(listParam.size()+1));
        compiler.addInstruction(new BOV(new Label("pile_pleine")));

        boolean reg2 = compiler.managerReg.isFree(2);
        boolean reg3 = compiler.managerReg.isFree(3);

        if (reg2) {
            compiler.addInstruction(new PUSH(Register.getR(2)));
        }
        if (reg3) {
            compiler.addInstruction(new PUSH(Register.getR(3)));
        }

        compiler.managerReg.freeReg(2, compiler);
        compiler.managerReg.freeReg(3, compiler);
        int actuel = compiler.managerReg.getRegMax();
        compiler.managerReg.setRegMax(4);
        methodBody.codeGenBody(compiler);
        compiler.managerReg.setRegMax(actuel);
        if (reg3) {
            compiler.addInstruction(new POP(Register.getR(3)));
        }
        if (reg2) {
            compiler.addInstruction(new POP(Register.getR(2)));
        }
    }

    
    /**
     * @param s
     * On decompile l'arbre.
     * On genere les caracteres necessaires pour declarer une methode
     */
    @Override
    public void decompile(IndentPrintStream s) {
    	type.decompile(s);
    	s.print(" ");
    	name.decompile(s);
    	s.print("(");
    	listParam.decompile(s);
    	s.println("){");
    	s.indent();
    	methodBody.decompile(s);
    	s.unindent();
    	s.println("}");
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
    	this.name.prettyPrint(s, prefix, false);
        this.type.prettyPrint(s, prefix, false);
        this.methodBody.prettyPrint(s, prefix, false);
        this.listParam.prettyPrint(s, prefix, false);
    }

    @Override
    protected void iterChildren(TreeFunction f) {
    	this.name.iterChildren(f);
        this.type.iterChildren(f);
        this.methodBody.iterChildren(f);
        this.listParam.iterChildren(f);
    }



}
