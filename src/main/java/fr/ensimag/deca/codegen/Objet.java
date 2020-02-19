package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.ImmediateInteger;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.RegisterOffset;
import fr.ensimag.ima.pseudocode.instructions.*;

public class Objet {

    /**
     * fonction permettant de générer la méthode equals de object
     * @param compiler
     */
    public static void codeGenEq(DecacCompiler compiler){
        compiler.addComment("Methode Equals");
        compiler.addLabel(new Label("code.Object.equals"));
        compiler.addInstruction(new PUSH(Register.getR(2)));
        compiler.addInstruction(new PUSH(Register.getR(3)));
        compiler.addInstruction(new LOAD(new RegisterOffset(-2, Register.LB), Register.getR(2)));
        compiler.addInstruction(new LOAD(new RegisterOffset(-3, Register.LB), Register.getR(3)));
        compiler.addInstruction(new CMP(Register.getR(2), Register.getR(3)));
        compiler.addInstruction(new BEQ(new Label("true")));
        compiler.addInstruction(new ADD(new ImmediateInteger(0), Register.R0));
        compiler.addInstruction(new BRA(new Label("fin")));
        compiler.addLabel(new Label("true"));
        compiler.addInstruction(new ADD(new ImmediateInteger(0), Register.R0));
        compiler.addLabel(new Label("fin"));
        compiler.addInstruction(new BRA(new Label("fin.Object.equals")));
        compiler.addInstruction(new WSTR("Erreur: sortie de la méthode equals sans return"));
        compiler.addInstruction(new WNL());
        compiler.addInstruction(new ERROR());
        compiler.addLabel(new Label("fin.Object.equals"));
        compiler.addInstruction(new POP(Register.getR(3)));
        compiler.addInstruction(new POP(Register.getR(2)));
        compiler.addInstruction(new RTS());
    }
}
