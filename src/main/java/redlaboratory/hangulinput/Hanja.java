package redlaboratory.hangulinput;

import com.opencsv.bean.CsvBindByName;

public class Hanja implements Comparable<Hanja> {

    @CsvBindByName(column = "hangul")
    private String hangul;

    @CsvBindByName(column = "hanja")
    private String hanja;

    public Hanja() {
    }

    public Hanja(String hangul) {
        this.hangul = hangul;
    }

    public char getHangul() {
        return hangul.charAt(0);
    }

    public char getHanja() {
        return hanja.charAt(0);
    }

    @Override
    public int compareTo(Hanja o) {
        return getHangul() - o.getHangul();
    }

}
