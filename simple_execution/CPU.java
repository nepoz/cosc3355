import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Bishrut Bhattarai
 * 
 * This class is a simple representation of a CPU
 */

 public class CPU {
    
    // OPCODES USED TO DECIPHER INSTRUCTIONS
    private final int LOAD_AC_MEM = 0b0001;
    private final int STORE_AC_MEM = 0b0010;
    private final int LOAD_AC_REG = 0b0011;
    private final int STORE_AC_REG = 0b0100;
    private final int ADD_AC_MEM = 0b0101;
    private final int LOAD_REG_OPERAND = 0b0110;
    private final int ADD_AC_REG = 0b0111;
    private final int MULT_AC_REG = 0b1000;
    private final int SUBT_AC_REG = 0b1001;
    private final int DIV_AC_REG = 0b1010;
    private final int JUMP_TO_SUBROUT = 0b1011;
    private final int RET_FRM_SUBROUT = 0b1100;
    private final int HALT = 0b1111;

    // CPU REGISTERS. Data type int as data is 16 bits.
    private int accumulator;                // Holds data values as they are being processed
    private int instructionReg;             // Holds the value of the current instruction being executed
    private int programCounter;             // Address of the next memory location to advance to
    private int generalReg;                 // A general purpose register that can hold intermediary values.
    
    // Memory that CPU needs to effectively operate
    private Memory memory;                  // Main memory; holds data and instructions
    private ExecutionStack stack;           // Preserves register states for when we jump to a subroutine
    
    // Constructor
    public CPU() {

        this.accumulator = 0x0;
        this.instructionReg = 0x0;
        this.programCounter = 0x0;
        this.generalReg = 0x0;
        this.memory = new Memory();
        this.stack = new ExecutionStack();
    }

    // Utility function to give a status string of registers
    private String getRegisterStatus() {
        String status = "";

        status += ("AC = " + Integer.toHexString(this.accumulator).toUpperCase() + "\n");
        status += ("IR = " + Integer.toHexString(this.instructionReg).toUpperCase()+ "\n");
        status += ("PC = " + Integer.toHexString(this.programCounter).toUpperCase() + "\n");
        status += ("REG = " + Integer.toHexString(this.generalReg).toUpperCase() + "\n");

        return status;
    }

    // CPU operations that will be called for different opCodes

    /**
     * Retrieves data from a location in the main memory and stores it in the accumulator
     * 
     * @param memoryAddress: The memory address that accumulator is being loaded from.
     */
    private void loadAccumulatorFromMemory(int memoryAddress) {

        this.accumulator = this.memory.retrieveContents(memoryAddress);
    }

    /**
     * Writes the current contents of the accumulator to the specified memory address.
     * 
     * @param memoryAddress: The memory address that the accumulator's contents are being stored in.
     */
    private void storeAccumulatorToMemory(int memoryAddress) {

        this.memory.storeDataIn(memoryAddress, this.accumulator);
    }

    /**
     * Loads the contents of the general register into the accumulator.
     */
    private void loadAccumulatorFromGeneralReg() {

        this.accumulator = this.generalReg;
    }

    /**
     * Writes contents of accumulator to general register. 
     */
    private void storeAccumulatorToGeneralReg() {
        
        this.generalReg = this.accumulator;
    }

    /**
     * Adds the data contained in the specified memory address to the
     * contents of the accumulator
     * 
     * @param memoryAddress: The memory address that data will be retrieved from
     */
    private void addToAccumulatorFromMemory(int memoryAddress) {

        int dataInMemory = this.memory.retrieveContents(memoryAddress);
        this.accumulator += dataInMemory;
    }

    /**
     * Loads a passed operand into the general register.
     * 
     * @param operand: The operand that is being loaded into the general register.
     */
    private void loadGeneralRegWithOperand(int operand) {
        
        this.generalReg = operand;
    }

    /**
     * Adds the contents of the general register to whatever is in the accumulator.
     * The sum is stored in the accumulator.
     */
    private void addToAccumulatorFromGeneralReg() {

        this.accumulator += this.generalReg;
    }

    /**
     * Multiplies the contents of the accumulator and general register.
     * The product is stored in the accumulator.
     */
    private void multiplyAccumulatorGeneralReg() {
        
        this.accumulator *= this.generalReg;
    }

    /**
     * The data in the general register is subtracted from the 
     * data in the accumulator.
     */
    private void subtractGeneralRegFromAccumulator() {

        this.accumulator -= this.generalReg;
    }

    /**
     * The data in the accumulator is divided by the data in 
     * the general register. The quotient is stored in the 
     * accumulator
     * 
     * THIS IMPLEMENTS INTEGER DIVISION
     * 
     */
    private void divideAccumulatorByGeneralReg() {

        if (this.generalReg != 0) {
            this.accumulator /= this.generalReg;
        } else {
            System.out.println("Fatal error! Division by 0 attempted");
            System.exit(1);
        }
    }

    private void jumpToSubroutineAt(int memoryAddress) {

        // Preserve current register states
        this.stack.push(this.programCounter);
        this.stack.push(this.instructionReg);
        this.stack.push(this.accumulator);
        this.stack.push(this.generalReg);

        this.programCounter = memoryAddress;
    }

    private void returnFromSubroutine(PrintWriter outWriter, int subroutineNum, int instrCount) {
        
        // Before we pop stuff off, we need to show values before return
        outWriter.println("========== Before Return from Subroutine " + subroutineNum +" Status=======");
        
        outWriter.println("=============Stack Status=============");
        outWriter.print(this.stack.getStatusString());

        outWriter.println("=============Registers & Memory Status=============");
        outWriter.print(getRegisterStatus());
        outWriter.print(this.memory.getStatusString());
        outWriter.println("Number of instructions executed = " + instrCount + "\n");

        // Restore program state using values at the top of the stack
        // Stack has to be popped in opposite order to how we pushed
        this.generalReg = this.stack.pop();
        this.accumulator = this.stack.pop();
        this.instructionReg = this.stack.pop();
        this.programCounter = this.stack.pop();
    }

    private void halt(PrintWriter outWriter, int instrCount) {
        
        outWriter.println("======End of Program Status======");
        
        outWriter.println("=============Stack Status=============");
        outWriter.print(this.stack.getStatusString());

        outWriter.println("=============Registers & Memory Status=============");
        outWriter.print(getRegisterStatus());
        outWriter.print(this.memory.getStatusString());
        outWriter.println("Number of instructions executed = " + instrCount + "\n");    
    }


    private int retrieveOpCode(int instruction) {
        
        String instructionHex = Integer.toHexString(instruction);
        String opCodeHex = instructionHex.substring(0,1);

        // We have 4 bit opcodes, so the first 4 bits make up the opcode
        int opCode =  Integer.parseInt(opCodeHex, 16);

        return opCode;
    }

    private int retrieveAddress(int instruction) {
        
        String instructionHex = Integer.toHexString(instruction);

        // The first hex digit is the opcode, so the other 3 make up the address
        String address = instructionHex.substring(1);

        return Integer.parseInt(address, 16);
    }

    /**
     * Loads the program passed by the user in "input.txt" into the main memory.
     * This simulates how computers would start the execution of a user program; it first
     * needs to be in the main memory.
     * 
     * @param fileName: The name of the file that contains user program
     * @throws FileNotFoundException if specified file is not in the directory
     */
    private void loadUserProgramToMemory(String fileName) throws FileNotFoundException {

        File input = new File(fileName);
        Scanner scan = new Scanner(input);
        int numInstructions = 0;


        while (scan.hasNextLine()) {
            
            String currentLine = scan.nextLine().replaceAll("\\s", "");
            char firstCharacter = currentLine.charAt(0);
            int sliceStart = currentLine.indexOf(".") + 1;
            int sliceEnd = currentLine.indexOf(";");

            if (Character.isDigit(firstCharacter)) {
                numInstructions += 1;
                
                String instructionString = currentLine.substring(sliceStart, sliceEnd);
                int memoryToStoreAt = Integer.parseInt(instructionString.substring(0, 3), 16);
                int instruction = Integer.parseInt(instructionString.substring(3), 16);
                
                this.memory.storeDataIn(memoryToStoreAt, instruction);

                // Initialize program counter for execution
                if (numInstructions == 1) {
                    this.programCounter = memoryToStoreAt;
                }
            }
        }

        scan.close();
    }

    // Fetch - execute cycle
    public void run(String fileName) throws FileNotFoundException, IOException {

        // First, we need to load the user program into the main memory
        loadUserProgramToMemory(fileName);

        // Going to open our PrintWiter for output reporting
        FileWriter outStream = new FileWriter("output.txt");
        PrintWriter outWriter = new PrintWriter(outStream);


        // Now we continue executing until we encounter a "halt" opcode
        boolean halt = false;
        int subCount = 0;
        int instructionsExecuted = 0;

        while (!halt) {
            
            this.instructionReg = this.memory.retrieveContents(this.programCounter++);
            int opCode = retrieveOpCode(this.instructionReg);
            int address = retrieveAddress(this.instructionReg);

            switch(opCode) {

                case LOAD_AC_MEM:
                    loadAccumulatorFromMemory(address);
                    break;
                case STORE_AC_MEM:
                    storeAccumulatorToMemory(address);
                    break;
                case LOAD_AC_REG:
                    loadAccumulatorFromGeneralReg();
                    break;
                case STORE_AC_REG:
                    storeAccumulatorToGeneralReg();
                    break;
                case ADD_AC_MEM:
                    addToAccumulatorFromMemory(address);
                    break;
                case LOAD_REG_OPERAND:
                    loadGeneralRegWithOperand(address);
                    break;
                case ADD_AC_REG:
                    addToAccumulatorFromGeneralReg();
                    break;
                case MULT_AC_REG:
                    multiplyAccumulatorGeneralReg();
                    break;
                case SUBT_AC_REG:
                    subtractGeneralRegFromAccumulator();
                    break;
                case DIV_AC_REG:
                    divideAccumulatorByGeneralReg();
                    break;
                case JUMP_TO_SUBROUT:
                    jumpToSubroutineAt(address);
                    subCount += 1;
                    break;
                case RET_FRM_SUBROUT:
                    returnFromSubroutine(outWriter, subCount, instructionsExecuted + 1);
                    break;
                case HALT:
                    halt(outWriter, instructionsExecuted + 1);
                    halt = true;
                    break;
                default:
                    System.out.println("Fatal error, opcode " + Integer.toBinaryString(opCode) + " not recognized");
                    System.exit(1);
            }

            instructionsExecuted++;
        }

        outWriter.close();
    }


    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        CPU cpu = new CPU();
        cpu.run("C:\\Users\\Bish\\cosc3355\\simple_execution\\input.txt");
    }
 }