import org.joda.money.Money;

import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class UserTerminal {
    private final Scanner scanner = new Scanner(System.in);
    private final Map<String, Money> magicMap;
    private final Map<String, Money> pokeMap;
    private final Map<String, Money> yugiohMap;
    private final Map<String, Money> vanguardMap;

    UserTerminal(Map<String, Money> magicMap, Map<String, Money> pokeMap, Map<String, Money> yugiohMap, Map<String, Money> vanguardMap) {
        this.magicMap = magicMap;
        this.pokeMap = pokeMap;
        this.yugiohMap = yugiohMap;
        this.vanguardMap = vanguardMap;

        startTerminal();
    }

    private void startTerminal() {
        System.out.println("Welcome");
        System.out.println("Select a product line.");
        CardProductLine productLine = CardProductLine.valueOf(scanner.nextLine().toUpperCase());

        System.out.println("Select a set.");
        String setName = scanner.nextLine();

        switch (productLine) {
            case MAGIC:
                processRequest(magicMap, setName);
                break;
            case POKEMON:
                processRequest(pokeMap, setName);
                break;
            case YUGIOH:
                processRequest(yugiohMap, setName);
                break;
            case CARDFIGHT:
                processRequest(vanguardMap, setName);
                break;
            default:
                System.out.println("Something went wrong");
        }

        startTerminal();
    }

    private void processRequest(Map<String, Money> productMap, String setName) {
        if (productMap.containsKey(setName)) {
            System.out.println("The total for the set is: " + productMap.get(setName));
        } else {
            System.out.println("No set found. Possible sets are:\n");
            Set<String> possibleSets = productMap.keySet();
            for (String s : possibleSets) {
                System.out.println(s);
            }
        }
    }
}
