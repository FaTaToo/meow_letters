package anabalica.github.io.meowletters.letters;

/**
 * Representation of a letter from game grid
 *
 * @author Ana Balica
 */
public class Letter {
    private String letter;

    public Letter(String letter) {
        setLetter(letter);
    }

    public String getLetter() {
        return letter;
    }

    /**
     * Set letter string of the Letter. Must be one-char string from the
     * current alphabet.
     *
     * @param letter string letter
     * @throws IllegalArgumentException if letter param is not from the current
     * alphabet or isn't one char long
     */
    public void setLetter(String letter) throws IllegalArgumentException {
        String upperCaseLetter = letter.toUpperCase();
        if (!Alphabet.getCurrent().contains(upperCaseLetter)) {
            throw new IllegalArgumentException("Letter missing from the alphabet.");
        }
        if (letter.length() != 1) {
            throw new IllegalArgumentException("Letter must be exactly 1 char long.");
        }
        this.letter = upperCaseLetter;
    }

    /**
     * Check if two Letter objects are equal
     *
     * @param that Letter object
     * @return true if equal, false otherwise
     */
    public boolean equals(Letter that) {
        return this.letter.equals(that.letter);
    }

    /**
     * Get the next consecutive Letter from the alphabet.
     *
     * @return next letter or null if case there is no next letter
     */
    public Letter next() {
        String alphabet = Alphabet.getCurrent();
        if (alphabet.endsWith(letter)) {
            return null;
        }
        int current_index = alphabet.indexOf(letter);
        String next_char = Character.toString(alphabet.charAt(++current_index));
        return new Letter(next_char);
    }

    /**
     * Get the previous Letter from the alphabet.
     *
     * @return previous letter or null in case there is no previous letter
     */
    public Letter previous() {
        String alphabet = Alphabet.getCurrent();
        if (alphabet.startsWith(letter)) {
            return null;
        }
        int current_index = alphabet.indexOf(letter);
        String previous_char = Character.toString(alphabet.charAt(--current_index));
        return new Letter(previous_char);
    }
}
