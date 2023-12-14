import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class PopulationData {

    private LinkedList<PopulationEntry> populationEntryList = new LinkedList<PopulationEntry>();

    public PopulationData(String filename) {

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

            reader.readLine(); // Kopfzeile

            while (reader.ready()) {
                String line = reader.readLine();

                // Leere Zeilen überspringen
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");

                String code = parts[0].substring(1, parts[0].length() - 1);
                Double population = Double.parseDouble(parts[3].substring(1, parts[3].length() - 1));
                populationEntryList.add(new PopulationEntry(code, population));
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

    public double getPopulationForCode(String code) {

        for (PopulationEntry populationEntry : populationEntryList) {
            if (code.equals(populationEntry.getCode())) {
                return 1000 * populationEntry.getPopulation();
            }

        }

        return -1;
    }

}