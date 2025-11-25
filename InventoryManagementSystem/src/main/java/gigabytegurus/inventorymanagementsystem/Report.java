package gigabytegurus.inventorymanagementsystem;

import java.util.Date;
import java.io.FileOutputStream;
import java.util.List;
import java.awt.Color;


import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Report
{
 // PDF METHOD 
    public void generatePDF(List<Item> items, String filePath)
    {
        try (Document document = new Document())
        {
            // Determine the user's Downloads folder and build full save path
            String downloads = System.getProperty("user.home") + "/Downloads/";
            String savePath = downloads + filePath;

            // Create a new PDF document and prepare to write to the output file
            PdfWriter.getInstance(document, new FileOutputStream(savePath));

            document.open();

            // Add a simple title and timestamp
            document.add(new Paragraph("Inventory Report"));
            document.add(new Paragraph("Generated on: " + new Date()));
            document.add(new Paragraph("\n"));

            // Loop through each item and write its details into the PDF
            for (Item item : items)
            {
                String text =
                    "ID: " + item.getItemId() +
                    ", Name: " + item.getName() +
                    ", Category: " + item.getCategory() +
                    ", Size: " + item.getSize() +
                    ", Colour: " + item.getColour() +
                    ", Price: €" + item.getPrice() +
                    ", Quantity: " + item.getQuantity() +
                    ", Supplier: " +
                       (item.getSupplier() != null ? item.getSupplier().getName() : "N/A");

                Paragraph p = new Paragraph(text);

                // If stock is low, highlight this PDF entry in red
                if (item.getQuantity() < 20)
                {
                    p.getFont().setColor(Color.RED);
                }

                // Add entry to the document
                document.add(p);
            }

            // Close the file when finished writing
            document.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    // EXCEL METHOD 
    public void generateExcel(List<Item> items, String filePath)
    {
        // Disable the noisy Log4j2 message from Apache POI
        System.setProperty("org.apache.logging.log4j.simplelog.StatusLogger.level", "OFF");

        try (Workbook workbook = new XSSFWorkbook())
        {
            // Build full path inside the user's Downloads folder
            String downloads = System.getProperty("user.home") + "/Downloads/";
            String savePath = downloads + filePath;

            // Create the sheet that will hold the report
            Sheet sheet = workbook.createSheet("Inventory Report");

            // Title row
            Row titleRow = sheet.createRow(0);
            titleRow.createCell(0).setCellValue("Inventory Report");

            // Timestamp row
            Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("Generated on: " + new Date());

            // Empty spacer row
            sheet.createRow(2);

            // Header row describing the table columns
            Row header = sheet.createRow(3);
            header.createCell(0).setCellValue("Item ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Category");
            header.createCell(3).setCellValue("Size");
            header.createCell(4).setCellValue("Colour");
            header.createCell(5).setCellValue("Price (€)");
            header.createCell(6).setCellValue("Quantity");
            header.createCell(7).setCellValue("Supplier");

            // Style used to mark low stock rows in red
            CellStyle redStyle = workbook.createCellStyle();
            redStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
            redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            int rowIndex = 4;

            // Write one row per item
            for (Item item : items)
            {
                Row row = sheet.createRow(rowIndex++);
                boolean lowStock = item.getQuantity() < 20;

                // Prepare cells for the row
                Cell[] cells = new Cell[]{
                    row.createCell(0),
                    row.createCell(1),
                    row.createCell(2),
                    row.createCell(3),
                    row.createCell(4),
                    row.createCell(5),
                    row.createCell(6),
                    row.createCell(7)
                };

                // Fill in the cell values
                cells[0].setCellValue(item.getItemId());
                cells[1].setCellValue(item.getName());
                cells[2].setCellValue(item.getCategory());
                cells[3].setCellValue(item.getSize());
                cells[4].setCellValue(item.getColour());
                cells[5].setCellValue(item.getPrice());
                cells[6].setCellValue(item.getQuantity());
                cells[7].setCellValue(item.getSupplier() != null ? item.getSupplier().getName() : "None");

                // Highlight low-stock rows in Excel
                if (lowStock)
                {
                    for (Cell c : cells)
                        c.setCellStyle(redStyle);
                }
            }

            // Resize all columns so text fits nicely
            for (int i = 0; i <= 7; i++)
                sheet.autoSizeColumn(i);

            // Save the Excel file
            FileOutputStream out = new FileOutputStream(savePath);
            workbook.write(out);
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
