import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PdfExtractor {

    private PdfExtractor() {
        throw new IllegalStateException("Utility class");
    }

    public static String getTextFromPdf(String filename) {
        try {
            File currentFile = new File(filename);
            PDDocument doc = PDDocument.load(currentFile);
            PDFTextStripper textStripper = new PDFTextStripper();
            String pdfText = textStripper.getText(doc);
            doc.close();
            return pdfText;
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Error extracting file: " + filename + ". See stacktrace.";
        }
    }
}
