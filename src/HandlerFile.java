import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

/*
 * this class will be in charge for operations tiwh files
 * Author: sba24317
 */
public class HandlerFile {

    // object that write on file
    public FileWriter oFileWritter;
    // object that write on file
    public FileWriter oFileErrorWritter;
    // object that read from file
    public Scanner oFileReader;
    //string auxiliar
    public String aux;
    // file In
    private File fileIn;
    // Object file out
    private File fileOut;
    // Object file Errors out
    private File fileErrorsOut;
    // indicates line was read.
    private int linePosition;
    // path for error file
    private String fileErrorLog;

     HandlerFile(String fileName) {       
        try {
            final LocalDateTime date = LocalDateTime.now();

            // create a new File
            fileIn = new File(fileName);
            // create a Scanner object
            oFileReader = new Scanner(fileIn);
            // create a fileout name
            fileOut = new File(fileIn.getParent() + "\\customerdiscount.txt"  );
            // delete old file out if It exists.
            fileOut.delete();
            // create a new file.
            fileOut.createNewFile();
            /// create a new writter.
            this.oFileWritter = new FileWriter(fileOut);

            // instace a file error log
            fileErrorLog = date.toString().substring(0,19).replace(":", "_").replace(".", "_");
            fileErrorLog = fileIn.getParent() + "\\customer_Errors-"+ fileErrorLog + ".log";

            fileErrorsOut = new File(fileErrorLog);
            fileErrorsOut.createNewFile();

            /// create a new writter for errors
            this.oFileErrorWritter = new FileWriter(fileErrorsOut);

        } catch (Exception error) {
            System.out.println("\nError setting the file handles, for file : <" + fileName + "> \nError is:" + error.getMessage());
            return;
        }
    }

    /*
     * this method will intereact with file and returns a Customer object
     */
    public Customer getCustomerFromFile() {
        String aux;
        String[] names;
        Customer myCustomer = new Customer();

        try {
            // read from file name ans second name
            names = this.oFileReader.nextLine() .split(" ");
            this.linePosition++;

            // test if is a valid string otherwise assing values
            if (names.length < 2) {
                throw new Exception("Could not find a line with first and second name");
            } else {
                // check if content for first name is valid
                if (names[0].matches("[a-zA-Z]+"))
                    myCustomer.setFirstName(names[0]);
                else
                    throw new Exception("First name should be letters");

                // check if content for second name is valid
                if (names[1].matches("[a-zA-Z0-9]+"))
                    myCustomer.setSecondName(names[1]);
                else
                    throw new Exception("Second name should be letter/number");
            }

            // read file again to fetch value
            aux = this.oFileReader.nextLine();
            linePosition++;

            // check if content for initial value is valid
            if (aux.matches("[0-9.]+"))
                myCustomer.setInitialValue(Double.parseDouble(aux));
            else
                throw new Exception("Value for inicital value should be numeric");

            // read file again to fetch value for classe
            aux = oFileReader.nextLine();
            linePosition++;

            // check if content for classe value is valid
            if (aux.matches("[1-3]"))
                myCustomer.setClasse(Integer.parseInt(aux));
            else
                throw new Exception("Value for classe should be 1/2/3");

            // read file again to fetch value for last purchase
            aux = this.oFileReader.nextLine();
            linePosition++;

            // check if content for last purchase is valid
            if (aux.matches("^(19|20)\\d{2}$"))
                myCustomer.setLastPurchaseYear(Integer.parseInt(aux));
            else
                throw new Exception("Value for year should be numeric from 1900 to 2099");

        } catch (Exception e) {
            // print error to console
            String errMsg = "- Error reading line: " + linePosition + " Error: " + e.getMessage();

            this.doWriteOnErrorLog(errMsg + "\n");

            // push the pointer in file to next line that represents a customer
            for (int i = (linePosition % 4); i < 4; i++) {
                if (oFileReader.hasNext()) {
                    oFileReader.nextLine();
                    linePosition++;
                }
            }
            myCustomer = null;
        }
        return myCustomer;
    }

    /*
     * Write data on file result
     * 
    */
    public boolean doWriteOnFile(Customer customer) {
        boolean result = true;
        try {
            // append data on file
            this.oFileWritter.append(customer.getFirstName() + " " + customer.getSecondName() + "\n");

            // append data on file
            this.oFileWritter.append(customer.getFinalValue() + "\n");            
            // flush data in file
            this.oFileWritter.flush();
        } catch (IOException e) {
            result = false;
            System.err.println("An error occurs while writing on file: " + fileOut.getAbsolutePath());    
            e.printStackTrace();
        }
        return result;
    }

    public boolean doWriteOnErrorLog(String errMsg) {
        boolean result = true; 
        try {
            this.oFileErrorWritter.append(errMsg + "\n");
            this.oFileErrorWritter.flush();
        } catch (IOException e) {
            System.out.println("Error occurs while attemp write on error file.");
            result = false;
        }
        return result;
    }

    public String getFileErrorLog(){
        return this.fileErrorLog;
    }

}
