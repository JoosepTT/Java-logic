import java.io.File;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Filetree {

    // abiklass faili taanete arvu meeldejätmiseks
    public static class FailiKirje {
        File file;
        int tase; // sügavus / taanete arv

        // konstruktor, mis salvestab nii faili kui ka selle taande
        FailiKirje(File file, int tase) {
            this.file = file;
            this.tase = tase;
        }
    }

    /**
     *
     * @param tee - juurfailitee
     */
    // peaklass, kus toimub järjekorra abil failipuu sorteerimine
    // väljastatakse juurkausta sisu ning järjekorda pannakse alamkaustad ning failid, mis käiakse omakorda samamoodi läbi
    public static void failipuu(String tee) {
        File juur = new File(tee);
        if (!juur.exists()) { // kui sisendina antud failitee eksisteerib
            System.out.println("Sellist failiteed ei eksisteeri!: " + tee);
            return;
        }

        Queue<FailiKirje> järjekord = new ArrayDeque<>();
        järjekord.add(new FailiKirje(juur, 0)); // kõigepealt lisatakse massiivi failipuu juur


        // järjekorra sisu väljastamine (töötlemisel lisatakse madalama taseme leiud järjekorda, et neid hiljem läbi käia)
        while (!järjekord.isEmpty()) { // kuni järjekorras on veel faile või kaustu
            FailiKirje kirje = järjekord.remove(); // massiivi algusest hakatakse järjest sama tasandi elemente välja võtma
            File element = kirje.file; // määratakse faili objekt
            int tase = kirje.tase; // ja faili taanete arv ehk nn tase
            String taanded = "\t".repeat(tase);

            if (element.isDirectory()) { // kui tegemist on kaustaga
                System.out.println(taanded + "[" + element.getName() + "]");

                File[] kaustaSisu = element.listFiles(); // massiiv, kuhu kogutakse failipuus allapoole liikudes eraldi iga kausta sisu
                if (kaustaSisu == null || kaustaSisu.length == 0) continue; // väljaarvatud juhul kui kaust on tühi või sellele pole mingil põhjusel ligipääsu

                // kaustade ja failide eraldamine töödeldavast kaustast
                List<File> kaustad = new ArrayList<>();
                List<File> failid = new ArrayList<>();

                for (File f : kaustaSisu) { // kaustasisu läbikäimisel nopitakse välja  selles sisalduvad kaustad ja failid
                    if (f.isDirectory()) kaustad.add(f);
                    else if (f.isFile()) {
                        double failiMaht = f.length() / 1024.0; // failisuurus teisendatakse kohe kilobaitideks
                        if (failiMaht <= 500.0) { // kontroll, et mitte töödelda nõutud maksimaalsest suurusest suuremaid faile
                            failid.add(f);
                        }
                    }
                }

                // pärast jagamist sorteeritakse objektid tähestikulises järjekorras üle
                kaustad.sort(Filetree::võrdle); // sorteerimine viitega võrdle meetodile
                failid.sort(Filetree::võrdle);

                // töödeldavast kaustast leitud kaustade ja failide lisamine järjekorda, et neid järgmises tasandis töödelda
                for (File kaust : kaustad) {
                    järjekord.add(new FailiKirje(kaust, tase + 1));
                }
                for (File fail : failid) {
                    järjekord.add(new FailiKirje(fail, tase + 1));
                }

            } else if (Files.isRegularFile(element.toPath())) { // kui tegemist on failga
                double failiMaht = element.length() / 1024.0;
                if (failiMaht <= 500.0) {
                    System.out.printf("%s%s (%.2f KB)%n", taanded, element.getName(), failiMaht); // sõne elemendi vormistamine
                }
            }
        }
    }

    // abimeetod failinimede võrdlemiseks
    public static int võrdle(File f1, File f2) {
        return f1.getName().compareToIgnoreCase(f2.getName()); // failide sorteerimine tähestiku alusel
    }

    public static void main(String[] args) {
        // enter path to target folder:
        failipuu(args[0]);

    }

}
