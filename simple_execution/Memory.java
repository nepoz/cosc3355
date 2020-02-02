/**
 * @author Bishrut Bhattarai
 * 
 * This class represents basic Main Memory
 */

 public class Memory {

    private int addresses[];

    public Memory() {
        this.addresses = new int[0xFFF + 1];
    }

    public int retrieveContents(int memAddress) {

        return this.addresses[memAddress];
    }

    public void storeDataIn(int memAddress, int data) {

        this.addresses[memAddress] = data;
    }

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