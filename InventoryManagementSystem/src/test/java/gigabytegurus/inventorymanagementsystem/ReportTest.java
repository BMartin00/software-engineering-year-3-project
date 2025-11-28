// A00316625 Nagasai Chintalapati
// A00318851 Martin  Banyanszki
// A00320456 Justas Zabitis
// A00317072 Hamza Hussain

package gigabytegurus.inventorymanagementsystem;

import junit.framework.TestCase;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportTest extends TestCase {

    private List<Item> sampleItems;

    @Override
    protected void setUp() {
        sampleItems = new ArrayList<>();

        Supplier supplierA = new Supplier(1, "Supplier A", "a@test.com");

        sampleItems.add(new Item(1, "Test Item 1", "Category A", "M", "Red", 10.99, 15, supplierA));
        sampleItems.add(new Item(2, "Test Item 2", "Category B", "L", "Blue", 19.99, 5, supplierA));
        sampleItems.add(new Item(3, "Test Item 3", "Category C", "S", "Green", 5.99, 30, supplierA));
    }

    
    //Test #: 1
    //Obj: Test generating a PDF report successfully
    //Input(s): sampleItems list, filePath = "test_inventory_report.pdf"
    //Expected Output: PDF file is created and not empty
    public void testGeneratePDF() {
        try {
            Report report = new Report();

            // Only the filename , Report class will add Downloads path
            String filePath = "test_inventory_report.pdf";

            // Check correct  location
            String finalPath = System.getProperty("user.home") + "/Downloads/" + filePath;
            File file = new File(finalPath);

            if (file.exists()) file.delete();

            report.generatePDF(sampleItems, filePath);

            assertTrue("PDF file was not created", file.exists());
            assertTrue("PDF file is empty", file.length() > 0);

            file.delete();

        } catch (Exception e) {
            fail("PDF generation threw exception: " + e.getMessage());
        }
    }

    //Test #: 2
    //Obj: Test generating an Excel report successfully
    //Input(s): sampleItems list, filePath = "test_inventory_report.xlsx"
    //Expected Output: Excel file is created and not empty
    public void testGenerateExcel() {
        try {
            Report report = new Report();

            String filePath = "test_inventory_report.xlsx";
            String finalPath = System.getProperty("user.home") + "/Downloads/" + filePath;

            File file = new File(finalPath);
            if (file.exists()) file.delete();

            report.generateExcel(sampleItems, filePath);

            assertTrue("Excel file was not created", file.exists());
            assertTrue("Excel file is empty", file.length() > 0);

            file.delete();

        } catch (Exception e) {
            fail("Excel generation threw exception: " + e.getMessage());
        }
    }
}
