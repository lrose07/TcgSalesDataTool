import org.apache.commons.io.FileUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalysisToolDriver {

    public static void main(String... args) {

        SeleniumUtilities.pullOrderSlips();
        AwsUtilities.connectToS3();

        // Exiting here while building Selenium and S3 parts
        System.exit(0);

        List<TradingCardRecord> tradingCardsOrdered = new ArrayList<>();

        File inputDir = new File("src/main/resources/ordersToProcess");
        List<File> files = (List<File>) FileUtils.listFiles(inputDir, new String[]{"pdf"}, false);
        System.out.println(files);
        for (File file : files) {
            String rawTextFromPdf = PdfUtilities.getTextFromPdf(file);
            tradingCardsOrdered.addAll(PdfUtilities.getCardLists(rawTextFromPdf));
        }

        List<TradingCardRecord> magicCards = new ArrayList<>();
        List<TradingCardRecord> pokemonCards = new ArrayList<>();
        List<TradingCardRecord> yugiohCards = new ArrayList<>();
        List<TradingCardRecord> vanguardCards = new ArrayList<>();

        // split card lists by product line, then by set
        for (TradingCardRecord tradingCardRecord : tradingCardsOrdered) {
            switch (tradingCardRecord.getProductLine()) {
                case MAGIC:
                    magicCards.add(tradingCardRecord);
                    break;
                case POKEMON:
                    pokemonCards.add(tradingCardRecord);
                    break;
                case YUGIOH:
                    yugiohCards.add(tradingCardRecord);
                    break;
                case CARDFIGHT:
                    vanguardCards.add(tradingCardRecord);
                    break;
                default:
                    // no op
            }
        }

        Map<String, Money> magicTotals = calculateTotals(magicCards);
        Map<String, Money> pokemonTotals = calculateTotals(pokemonCards);
        Map<String, Money> yugiohTotals = calculateTotals(yugiohCards);
        Map<String, Money> vanguardTotals = calculateTotals(vanguardCards);

        new UserTerminal(magicTotals, pokemonTotals, yugiohTotals, vanguardTotals);
    }

    private static Map<String, Money> calculateTotals(List<TradingCardRecord> cards) {
        Map<String, Money> tempTotals = new HashMap<>();

        for (TradingCardRecord cardRecord : cards) {
            String setName = cardRecord.getSetName();
            Money runningTotal;

            if (tempTotals.containsKey(setName)) {
                runningTotal = tempTotals.get(setName);
            } else {
                runningTotal = Money.zero(CurrencyUnit.USD);
            }
            runningTotal = runningTotal.plus(cardRecord.getPrice().multipliedBy(cardRecord.getQuantity()));
            tempTotals.put(setName, runningTotal);
        }

        return tempTotals;
    }
}
