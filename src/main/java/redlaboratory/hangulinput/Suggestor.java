package redlaboratory.hangulinput;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Suggestor {

    public class Suggestion {

        private char character;

        public Suggestion(char character) {
            this.character = character;
        }

        public char getCharacter() {
            return character;
        }

    }

    private boolean valid = false;

    private List<Hanja> hanjas;

    private boolean suggestionMode = false;
    private List<Suggestion> suggestions;
    private int suggestionSelectedIndex;

    public Suggestor() {
        InputStream fileInputstream = getClass().getClassLoader().getResourceAsStream("hanja.csv");

        if (fileInputstream != null) {
            hanjas = new CsvToBeanBuilder(new InputStreamReader(fileInputstream, StandardCharsets.UTF_8))
                    .withType(Hanja.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build()
                    .parse();

            valid = true;
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setProperSuggestions(char c) {
        if (isValid()) {
            int index = Collections.binarySearch(hanjas, new Hanja(new String(new char[] {c})));

            if (index >= 0 && hanjas.get(index).getHangul() == c) {
                int low = index;
                int high = index;

                while (low - 1 >= 0 && hanjas.get(low - 1).getHangul() == c)
                    low--;
                while (high < hanjas.size() && hanjas.get(high).getHangul() == c)
                    high++;

                List<Suggestor.Suggestion> newSuggestions = new ArrayList<>();

                for (int i = low; i < high; i++) {
                    newSuggestions.add(new Suggestor.Suggestion(hanjas.get(i).getHanja()));
                }

                setSuggestionMode(true);
                setSuggestions(newSuggestions);
            }
        }
    }

    public void setSuggestionMode(boolean suggestionMode) {
        this.suggestionMode = suggestionMode;
    }

    public boolean getSuggestionMode() {
        return suggestionMode;
    }

    public void setSuggestions(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
        suggestionSelectedIndex = 0;
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public int getSelectedIndex() {
        return suggestionSelectedIndex;
    }

    public void increaseSelectIndex() {
        int mod = suggestions.size();
        suggestionSelectedIndex = ((suggestionSelectedIndex - 1) % mod + mod) % mod;
    }

    public void decreaseSelectIndex() {
        int mod = suggestions.size();
        suggestionSelectedIndex = (suggestionSelectedIndex + 1) % mod;
    }

    public char getSelectedChar() {
        return suggestions.get(suggestionSelectedIndex).getCharacter();
    }

}
