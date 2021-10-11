package phonebook;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import javafx.collections.ObservableList;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

public class PdfGeneration {
    public void pdfGeneration(String fileName, ObservableList<Person> data) {
        try {
            //Dokumentum létrehozása
            String path = "C:\\Users\\user\\Desktop\\PDF File Phonebook\\" + fileName +".pdf";
            PdfWriter pdfWriter = new PdfWriter(path);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            pdfDocument.addNewPage();
            Document document = new Document(pdfDocument);

            //Kép elhelyezése a dokumentumban
            ClassLoader classLoader = getClass().getClassLoader();
            String imageSource = classLoader.getResource("Images/Phonebook.png").toExternalForm();
            ImageData imageData = ImageDataFactory.create(imageSource);
            Image phoneBookImage = new Image(imageData).scaleAbsolute(200, 100).setFixedPosition(1, 200, 700);
            document.add(phoneBookImage);

            document.add(new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n"));

            //Táblázat

            //Oszlopok, és táblázat létrehozása
            float[] columnWidth = {1, 4, 4, 6};
            Table pdfTable = new Table(columnWidth);
            pdfTable.setWidth(UnitValue.createPercentValue(100));
            //Cellák létrehozása, beállítása
            Cell cell = new Cell(0, 4);
            Paragraph headerParagraph = new Paragraph("Kontaktlista").setTextAlignment(TextAlignment.CENTER);
            cell.add(headerParagraph);
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
            pdfTable.addHeaderCell(cell);


           // String[] secondHeader = {"Sorszám", "Vezetéknév", "Keresztnév", "E-mail cím"};

            pdfTable.addCell("Sorszám").setTextAlignment(TextAlignment.CENTER);
            pdfTable.addCell("Vezetéknév").setTextAlignment(TextAlignment.CENTER);
            pdfTable.addCell("Keresztnév").setTextAlignment(TextAlignment.CENTER);
            pdfTable.addCell("E-mail cím").setTextAlignment(TextAlignment.CENTER);

            //Táblázat feltöltése adatokkal
            for(int i = 1; i<=data.size(); i++){
                Person currentPerson = data.get(i-1);

                pdfTable.addCell(String.valueOf(i));
                pdfTable.addCell(currentPerson.getLastName());
                pdfTable.addCell(currentPerson.getFirstName());
                pdfTable.addCell(currentPerson.getEmail());
            }

            document.add(pdfTable);

            //Aláírás
            Text signature = new Text("\n\n Generálva a Telefonkönyv alkalmazás segítségével.");
            Paragraph base = new Paragraph(signature);
            document.add(base);


            document.close();
        } catch (FileNotFoundException | MalformedURLException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }
}
