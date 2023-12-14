import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class InfectionList {

    private String iso2;
    private LinkedList<Integer> infectionsDataList = new LinkedList<Integer>();

    public InfectionList(String filename, String iso2) {

        this.iso2 = iso2;

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

                if (parts[1].equals(iso2)) {
                    infectionsDataList.add(Integer.parseInt(parts[4]));
                }
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

    public LinkedList<Integer> getInfections() {
        return infectionsDataList;
    }

    public int getNumDatapoints() {
        return infectionsDataList.size();
    }

    public String getCountryISO2() {
        return iso2;
    }

}