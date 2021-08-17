import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CountryCodes {

    private LinkedList<CountryCode> countryCodesList = new LinkedList<CountryCode>();

    public CountryCodes(String filename) {

        File infile = new File("csv-files\\" + filename);
        FileReader filereader = null;

        try {
            filereader = new FileReader(infile);
        }

        catch (Exception e) {
            System.out.println("Datei nicht gefunden");
        }

        BufferedReader reader = new BufferedReader(filereader);

        try {
            reader.readLine(); // header line
            while (reader.ready()) {
                String line = reader.readLine();
                String[] parts = line.split(",");

                if (parts.length <= 16) {
                    // Einige Zeilen der CSV Datei sind fehlerhaft,
                    // weil die Kommas in Länderbezeichnungen nicht markiert
                    // wurden und dadurch diese Zeilen zu oft geteilt wird.
                    // Diese Zeilen werden hier übersprungen.

                    try {
                        countryCodesList.add(new CountryCode(Integer.toString(Integer.parseInt(parts[9])), parts[10]));
                    } catch (NumberFormatException ex) {
                        System.out.println("Fehler beim hinzufügen eines Ländercodes");
                    }

                }

                // for (CountryCode c : countryCodesList) {
                // System.out.println(c.getCode());
                // }

            }
        } catch (Exception e) {
            System.out.println("Fehler beim Lesen der Datei");
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (Exception e) {
            System.out.println("Fehler beim SchließenderDatei");
        }

    }

    public String getCodeForISO2(String iso2) {

        for (CountryCode countryCode : countryCodesList) {
            if (iso2.equals(countryCode.getISO2())) {
                return countryCode.getCode();
            }
        }
        return "N/A";

    }

    public String getISO2ForCode(String code) {

        for (CountryCode countryCode : countryCodesList)

        {

            if (code.equals(countryCode.getCode())) {
                return countryCode.getISO2();
            }

        }

        return "N/A";
    }

}