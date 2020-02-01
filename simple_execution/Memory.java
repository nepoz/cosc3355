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

    public void memDump() {
        for (int i = 0; i < this.addresses.length; i++) {
            if (this.addresses[i] > 0) {
                System.out.println(Integer.toHexString(i) + ": " + Integer.toHexString(this.addresses[i]));
            }
        }
    }
 }