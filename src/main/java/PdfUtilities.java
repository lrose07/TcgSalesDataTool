import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.joda.money.Money;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public final class PdfUtilities {

    private PdfUtilities() {
        throw new IllegalStateException("Utility class");
    }

    public static String getTextFromPdf(File file) {
        try {
            PDDocument doc = PDDocument.load(file);
            PDFTextStripper textStripper = new PDFTextStripper();
            String pdfText = textStripper.getText(doc);
            doc.close();
            return pdfText;
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return "Error extracting file: " + file.getName() + ". See stacktrace.";
        }
    }

    static ArrayList<TradingCardRecord> getCardLists(String text) {
        String cardDataStart = "Quantity Description Price Total Price\n";
        String cardDataEnd = " Total $";
        ArrayList<String> cardTextBlocks = new ArrayList<>();
        String[] initialChunks = text.split("For Any Issues With the Contents of Your Order:");
        for (String s : initialChunks) {
            if (s.contains("Shipping Method")) {
                String cardTextBlock = StringUtils.substringBetween(s, cardDataStart, cardDataEnd);
                cardTextBlocks.add(cardTextBlock.substring(0, cardTextBlock.lastIndexOf("\n")));
            }
        }
        ArrayList<String> cardEntries = new ArrayList<>();
        for (String s : cardTextBlocks) {
            String[] individualCards = s.split(" \\$[0-9]+\\.[0-9]+\n");
            for (String str : individualCards) {
                if (!str.contains("Order Number") && !str.contains("Quantity Description") && !str.contains("Force of Will")) {
                    cardEntries.add(removeExtraPrice(str.replace("\n", "")));
                }
            }
        }

        ArrayList<TradingCardRecord> cardsInOrder = new ArrayList<>();

        for (String s : cardEntries) {
            cardsInOrder.add(parseSingleCard(s));
        }

        return cardsInOrder;
    }

    private static String removeExtraPrice(String s) {
        return StringUtils.countMatches(s, "$") > 1 ? s.substring(0, s.lastIndexOf(" $")) : s;
    }

    private static TradingCardRecord parseSingleCard(String cardStr) {
        String str = cardStr;
        if (str.indexOf(' ') > 2) {
            str = fixLineMissingSpaces(str);
        }

        String[] cardDetailsArr;

        if (str.contains(" SM - ")) {
            cardDetailsArr = str.replace(" SM -", "").split(" - ");
        } else if (str.contains(" XY - ")) {
            cardDetailsArr = str.replace(" XY -", "").split(" - ");
        } else {
            cardDetailsArr = str.split(" - ");
        }

        String[] quantityAndProductLine = cardDetailsArr[0].split(" ");

        int cardQuantity = Integer.parseInt(quantityAndProductLine[0]);
        String cardProductLineString = quantityAndProductLine[1];

        String setAndCardName = cardDetailsArr[1];

        String setName = setAndCardName.substring(0, setAndCardName.lastIndexOf(":"));
        String cardName = setAndCardName.substring(setAndCardName.lastIndexOf(":") + 2);

        String priceAndQualityString = cardDetailsArr[cardDetailsArr.length - 1];
        String priceString = priceAndQualityString.substring(priceAndQualityString.lastIndexOf("$")).replace("$", "USD ");
        Money price = Money.parse(priceString);

        return new TradingCardRecord(
                cardQuantity,
                CardProductLine.valueOf(cardProductLineString.toUpperCase()),
                setName,
                cardName,
                price);
    }

    private static String fixLineMissingSpaces(String str) {
        StringBuilder sb = new StringBuilder(str);
        try {
            Integer.parseInt(str.substring(1, 1));
            sb.insert(2, ' ');
        } catch (NumberFormatException e) {
            sb.insert(1, ' ');
        }
        sb.insert(sb.indexOf("$"), ' ');
        return sb.toString();
    }
}
