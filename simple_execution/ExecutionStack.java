/**
 * Represents stack memory that is used to preserve the state of registers
 * when the CPU has to jump to a subroutine
 * 
 * @author Bishrut Bhattarai
 */

public class ExecutionStack {
    // Maximum stack address is 0x3FF
    private final static int MAX_SIZE = 0x3FF + 1;
    
    private int registerStates[];
    private int top;
    private int size;

    public ExecutionStack() {
        this.registerStates = new int[MAX_SIZE];
        this.top = MAX_SIZE;
        this.size = 0;
    }

    public void push(int value) {
        if (this.top == 0) {
            System.err.println("Stack overflow, program terminated");
            System.exit(1);
        }
        else {
            registerStates[--this.top] = value;
            this.size++;
        }
    }

    public int pop() { 
        int temp = registerStates[this.top++];
        this.size --;
        return temp;
    }

    public String getStatusString() {

        String status = "";

        if (this.size == 0) {
            status = "Nothing in the stack!\n";
        }
        else {
            for (int i = MAX_SIZE - 1; i >= MAX_SIZE - this.size ; i--) {
                String memoryLocation = Integer.toHexString(i).toUpperCase();
                String cellContents = Integer.toHexString(this.registerStates[i]).toUpperCase();

                String update = "Stack contents at " + memoryLocation + " = " + cellContents + "\n";
                status += update;
            }
        }

        return status;
    }
}   