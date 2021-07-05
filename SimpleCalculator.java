import java.io.*;
import java.util.*;


public class SimpleCalculator {

	private Scanner input = new Scanner(System.in);
	private PrintStream output = System.out;
	private Map<String, Register> registerMap = new HashMap<String, Register>();


	public void run( String [] args ) {
		showInstructions();
		checkForFileArgument(args);
		mainLoop();
	}

	private void showInstructions() {
		output.println("Simple Calculator");
		output.println("** Inputs **");
		output.println("1) <register> <operation> <value>");
		output.println("2) print <register>");
		output.println("3) quit");
		output.println("** launch with inputs from file 'java SimpleCalculator <filename>' **");
		output.println();
	}

	private void checkForFileArgument( String [] args ) {
		if (args.length == 0) return;
		else if (args.length != 1) exitProgram("pass only 1 filename argument. see inputs.");
		else try {
			Scanner fileScanner = new Scanner(new FileReader(args[0]));
			String fileLine;
			while(fileScanner.hasNext()){
				fileLine = fileScanner.nextLine().toLowerCase();
				processInput(fileLine);
			}
		} catch (FileNotFoundException e) {
			exitProgram("please enter a valid file.");
		}
	}

	private void mainLoop(){
		while(true){
			processInput( input.nextLine().toLowerCase() );
		}
	}

	private void exitProgram(String exitMessage) {
		System.out.println(exitMessage + "quiting...");
		System.exit(0);
	}

	private void processInput( String inputLine  ){
		
		String [] inputArray = inputLine.split(" ");

		if ( inputLine.equals("") ) return;
		else if ( inputLine.equals("quit") ) exitProgram(""); 
		
		else if ( inputArray.length == 2 && inputArray[0].equals("print") ) {
			if (registerMap.containsKey(inputArray[1]))  System.out.println(registerMap.get(inputArray[1]).getValue());
			else System.out.println("register does not exist");
		}	

		else if ( inputArray.length == 3 && checkValidOperation(inputArray[1]) ) {
			OperatorOperand opp = checkValidRegisterOperatorOperand(inputArray[0], inputArray[1], inputArray[2]);
			if (opp != null) {
				if ( registerMap.containsKey(inputArray[0]) ) {
					registerMap.get(inputArray[0]).operation( opp );
				} else if ( !registerMap.containsKey(inputArray[0]) ) {
					registerMap.put( inputArray[0], new Register(inputArray[0]));
					registerMap.get( inputArray[0]).operation( opp );
				}
			}
		} else System.out.println("please check your syntax. Invalid command: " + inputLine);	
		return;
	}


	private boolean checkValidOperation(String operator) {
		if (operator.equals("add") || operator.equals("subtract") || operator.equals("multiply")) return true;
		else return false;
	}


	private OperatorOperand checkValidRegisterOperatorOperand(String register, String operation, String operand) {
	
		OperatorOperand opp = null;

		if (registerMap.containsKey(operand)) opp = new OperatorOperand(operation, registerMap.get(operand));

		else if (register.matches("-?(0|[1-9]\\d*)")) {
			System.out.println("invalid register name");
			return opp;	
		}
	
		else if (operand.matches("-?(0|[1-9]\\d*)")) try {
			opp = new OperatorOperand(operation, Integer.parseInt(operand));
		} catch (Exception e) {
			System.out.println("invalid value, caused: " + e);
		}

		else {
			registerMap.put(operand, new Register(operand));
			opp = new OperatorOperand(operation, registerMap.get(operand));
		}
		return opp;
	}

	//---------------------------------------------------------------------------------------//

	public static void main ( String [] args ) {
		SimpleCalculator sc = new SimpleCalculator();
		sc.run( args );
	}

	//---------------------------------------------------------------------------------------//

	class OperatorOperand {
		String operation;
		Register register;
		int value;
		OperatorOperand (String opp, Register register){
			this.operation = opp;
			this.register = register;
		}
		OperatorOperand (String opp, int value){
			this.operation = opp;
			this.value = value;
		}
		String getOpperation(){
			return operation;
		}
		int getValue(){
			if (register != null) return register.getValue();
			else return value;
		}
	}


	class Register {
		String name;
		int value;
		Queue<OperatorOperand> lazyEvaluationOperations;

		Register(String name){
			this.name = name;
			this.value = 0;
			this.lazyEvaluationOperations = new LinkedList<OperatorOperand>();
		}

		void operation( OperatorOperand opp ) {
			lazyEvaluationOperations.add(opp);
		}

		String getName(){
			return name;
		}



		int getValue(){
			while(lazyEvaluationOperations.size() != 0) {
				OperatorOperand opp = lazyEvaluationOperations.poll();
				if (opp.getOpperation().equals("add")) value += opp.getValue();
				else if (opp.getOpperation().equals("subtract")) value -= opp.getValue();
				else if (opp.getOpperation().equals("multiply")) value *= opp.getValue();
			}
			return value;
		}
	
	}




}






