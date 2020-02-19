package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tools.SymbolTable;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Register;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;

public class RegisterManager {
	//Gestion des Registres
	private int GB;
	private int LP;
	private static int regUsed = 0;
	private static int lastRegused = 0 ;
	private int regMax = 15;


	public int getGB() {
		return GB;
	}

	public void setGB(int gB) {
		GB = gB;
	}

	public int getLP() {
		return LP;
	}

	public void setLP(int lP) {
		LP = lP;
	}

	private boolean registres[]= new boolean[16];

	public RegisterManager() {
	}

	/**
	 * fonction permettant de limiter le nombre de registres utilisables
	 * @param regMax
	 */
	public void setRegMax(int regMax) {
		this.regMax = regMax;
	}

	/**
	 * fonction permettant de remonter le nombre de registres maximum
	 * @return regMax le nombre de registres max
	 */

	public int getRegMax() {
		return regMax;
	}


	/**
	 * renvoit un booleen indicant l'occupation ou non de l'ensemble des registres
	 * @return
	 */
	public boolean fullRegistres() {
		for (int i = 2; i<regMax; i++) {
			if(!registres[i]){
				return registres[i];
			}
		}
		return true;
	}

	/**
	 * renvoit le premier registre inoccupé
	 * @return un entier i correspondant au premier registre libre
	 */

	public int getRegLibre() {
		for (int i = 2; i<=regMax; i++) {
			if(!registres[i]){
				return i;}
			}

		return -1;
	}

	/**
	 * fonction indiquant l'occupation d'un registre
	 * @param i
	 */
	public void setReg(int i) {
		if (registres[i]){
			System.out.println("error");
		}
		registres[i] = true;
		regUsed++;
		lastRegused = i;
	}

	/**
	 * fonction permettant de libérer le registre i
	 * @param i
	 * @param compiler
	 */
	public void freeReg(int i, DecacCompiler compiler){
		if (regUsed>regMax-2){
			regUsed--;
		}
		else{
			registres[i] = false;
			regUsed--;
		}
	}



	public void Recupval(DecacCompiler compiler){
		if (regUsed>=regMax-2){
			compiler.addInstruction(new LOAD(Register.getR(lastRegused), Register.R0));
			compiler.addInstruction(new POP(Register.getR(lastRegused)));

		}
	}


	/**
	 * fonction permettant l'affectation d'un registre
	 * @param compiler
	 * @return
	 */
	public int affectReg(DecacCompiler compiler) {
		if (regUsed >= regMax-2) {
			compiler.addInstruction(new PUSH(Register.getR(lastRegused)));
			regUsed++;
			return lastRegused;

		}
		else {
			int i = getRegLibre();
			regUsed++;
			registres[i] = true;
			lastRegused = i;
			return lastRegused;
		}
	}

	/**
	 * fonction renvoyant le dernier registre utilisé
	 * @return
	 */
	public static int getLastRegused() {
		return lastRegused;
	}

	public  GPRegister manageRegisters (DecacCompiler compiler) {
		if (lastRegused == regMax ){
			compiler.addInstruction(new POP(Register.getR(lastRegused)));
			return Register.getR(lastRegused);

		}

		else {
			lastRegused ++ ;
			return Register.getR(lastRegused);
		}

	}


	/**
	 * fonction permettant d'indiquer le dernier registre utilisé
	 * @param left
	 */
	public void setLastRegused(int left) {
		lastRegused = left;
	}

	/**
	 * fonction renvoyant si un registre est libre
	 * @param i
	 * @return
	 */
    public boolean isFree(int i) {
		return registres[i];
    }
}
