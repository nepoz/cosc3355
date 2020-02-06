/**
 * @author Bishrut Bhattarai
 * 
 * This class represents basic Main Memory
 */

 public class Memory {

    private int addresses[];

    public Memory() {

        // Can have addresses ranging from 0x000 to 0xFFF
        this.addresses = new int[0xFFF + 1];
    }

    /**
     * Retrieves the contents stored at a particular location.
     * 
     * @param memAddress: The memory address that is being pulled from.
     * @return Whatever is in location memAddress (not very secure, but...).
     */
    public int retrieveContents(int memAddress) {

        return this.addresses[memAddress];
    }

    /**
     * Adds data to a specified location in memory
     * 
     * @param memAddress: The memory address that data is being written to
     * @param data: The data that is being stored in memory
     */
    public void storeDataIn(int memAddress, int data) {

        this.addresses[memAddress] = data;
    }

    /**
     * Similar to toString, but I wanted to make the method name more suited
     * to its purpose. Returns the status of 3 key memory locations
     * 
     * @return The status of memory locations 0x940, 0x941, and 0x942
     */
    public String getStatusString() {

        String status = "";
        
        for (int i = 0x940; i <= 0x942; i++) {
            String memLocation = Integer.toHexString(i).toUpperCase();
            String cellContents = Integer.toHexString(addresses[i]).toUpperCase();

            status += ("Memory " + memLocation + " = " + cellContents + "\n");
        }

        return status;
    }
 }