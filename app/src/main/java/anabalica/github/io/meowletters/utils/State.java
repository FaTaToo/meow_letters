package anabalica.github.io.meowletters.utils;

import android.content.SharedPreferences;

import anabalica.github.io.meowletters.GameActivity;
import anabalica.github.io.meowletters.letters.Cell;
import anabalica.github.io.meowletters.letters.Letter;
import anabalica.github.io.meowletters.letters.LetterChain;
import anabalica.github.io.meowletters.letters.LetterGrid;
import anabalica.github.io.meowletters.metrics.Level;
import anabalica.github.io.meowletters.metrics.Score;

/**
 * Helper class to store and load GameActivity instance for persistence.
 * Kind of serialization/deserialization in shared preferences.
 *
 * @author Ana Balica
 */
public class State {
    private SharedPreferences sharedPreferences;

    public static final String STATE_LEVEL = "anabalica.github.io.meowletters.state_level";
    public static final String STATE_SCORE = "anabalica.github.io.meowletters.state_score";
    public static final String STATE_LETTER_GRID = "anabalica.github.io.meowletters.state_letter_grid";
    public static final String STATE_LETTER_CHAIN = "anabalica.github.io.meowletters.state_letter_chain";
    public static final String STATE_PENALTY = "anabalica.github.io.meowletters.state_penalty";
    public static final String STATE_CREATE_NEW_TIMER = "anabalica.github.io.meowletters.state_create_new_timer";
    public static final String STATE_TIMER = "anabalica.github.io.meowletters.state_timer";

    public State(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void save(GameActivity gameActivity) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // save metrics
        Level level = gameActivity.getLevel();
        Score score = gameActivity.getScore();
        editor.putInt(STATE_LEVEL, level.getLevel());
        editor.putInt(STATE_SCORE, score.getPoints());

        // save grid
        LetterGrid letterGrid = gameActivity.getLetterGrid();
        String stateLetterGrid = encodeLetterGrid(letterGrid);
        editor.putString(STATE_LETTER_GRID, stateLetterGrid);

        // save selected letter chain
        LetterChain letterChain = gameActivity.getSelectedLetterChain();
        String stateLetterChain = encodeLetterChain(letterChain);
        editor.putString(STATE_LETTER_CHAIN, stateLetterChain);

        // save penalty and createNewTimer bools
        boolean penalty = gameActivity.hasPenalty();
        editor.putBoolean(STATE_PENALTY, penalty);
        boolean createNewTimer = gameActivity.isCreateNewTimer();
        editor.putBoolean(STATE_CREATE_NEW_TIMER, createNewTimer);

        // save timer
        long millisUntilFinished = gameActivity.getTimerMillisUntilFinished();
        editor.putLong(STATE_TIMER, millisUntilFinished);
        editor.apply();
    }

    public Level getLevel() {
        int saved_level = sharedPreferences.getInt(STATE_LEVEL, 1);
        return new Level(saved_level);
    }

    public Score getScore() {
        int points = sharedPreferences.getInt(STATE_SCORE, 0);
        return new Score(points);
    }

    public LetterGrid getLetterGrid() {
        String letterGridString = sharedPreferences.getString(STATE_LETTER_GRID, "");
        return decodeLetterGrid(letterGridString);
    }

    public LetterChain getSelectedLetterChain() {
        String letterChainString = sharedPreferences.getString(STATE_LETTER_CHAIN, "");
        return decodeLetterChain(letterChainString);
    }

    public boolean getPenalty() {
        return sharedPreferences.getBoolean(STATE_PENALTY, false);
    }

    public boolean getCreateNewTimer() {
        return sharedPreferences.getBoolean(STATE_CREATE_NEW_TIMER, false);
    }

    public long getTimerMillisUntilFinished() {
        return sharedPreferences.getLong(STATE_TIMER, 0);
    }

    /**
     * Encode a Letter object as a single string to be stored in shared preferences as
     * "<row><column><letter char><1 if selected, 0 otherwise>".
     *
     * @param letter Letter object
     * @return String encoded letter
     */
    private String encodeLetter(Letter letter) {
        StringBuilder letterStringBuilder = new StringBuilder();
        Cell position = letter.getPosition();
        letterStringBuilder.append(String.valueOf(position.getRow()));
        letterStringBuilder.append(String.valueOf(position.getColumn()));
        letterStringBuilder.append(letter.getLetter());
        String isSelected = letter.isSelected() ? "1" : "0";
        letterStringBuilder.append(isSelected);
        return letterStringBuilder.toString();
    }

    /**
     * Decode a Letter object from a string
     *
     * @param letterString String containing encoded information about Letter object
     * @return Letter object
     */
    private Letter decodeLetter(String letterString) {

        int row = Integer.parseInt(Character.toString(letterString.charAt(0)));
        int column = Integer.parseInt(Character.toString(letterString.charAt(1)));
        String letterChar = Character.toString(letterString.charAt(2));
        boolean isSelected = Integer.parseInt(Character.toString(letterString.charAt(3))) == 1;

        return new Letter(letterChar, row, column, isSelected);
    }

    /**
     * Encode the game letter grid to a single string to be stored in shared preferences.
     * Only non empty cells are stored into the string. Letters are delimited using a semicolon.
     *
     * @param letterGrid LetterGrid object
     * @return String encoded letter grid
     */
    private String encodeLetterGrid(LetterGrid letterGrid) {
        int rows = letterGrid.getROWS();
        int columns = letterGrid.getCOLUMNS();
        Letter[][] grid = letterGrid.getGrid();
        StringBuilder gridStringBuilder= new StringBuilder();

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (grid[row][column] != null) {
                    gridStringBuilder.append(encodeLetter(grid[row][column]));
                    gridStringBuilder.append(";");
                }
            }
        }

        if (gridStringBuilder.length() != 0) {
            int lastCharIndex = gridStringBuilder.length() - 1;
            gridStringBuilder.deleteCharAt(lastCharIndex);
        }
        return gridStringBuilder.toString();
    }

    /**
     * Decode a LetterGrid object from a string
     *
     * @param letterGridString String containing encoded information about a LetterGrid object
     * @return LetterGrid object
     */
    private LetterGrid decodeLetterGrid(String letterGridString) {
        Letter[][] grid = new Letter[GameActivity.gridRows][GameActivity.gridColumns];
        if (letterGridString.length() > 0) {
            for (String letterString : letterGridString.split(";")) {
                Letter letter = decodeLetter(letterString);
                Cell position = letter.getPosition();
                int row = position.getRow();
                int column = position.getColumn();
                grid[row][column] = letter;
            }
        }
        return new LetterGrid(grid);
    }

    /**
     * Encode a letter chain to a single string to be stored in shared preferences. Letters are
     * delimited using a semicolon.
     *
     * @param letterChain LetterChain object
     * @return String encoded letter chain
     */
    private String encodeLetterChain(LetterChain letterChain) {
        StringBuilder chainStringBuilder = new StringBuilder();
        for (Letter letter : letterChain) {
            chainStringBuilder.append(encodeLetter(letter));
            chainStringBuilder.append(";");
        }

        if (chainStringBuilder.length() != 0) {
            int lastCharIndex = chainStringBuilder.length() - 1;
            chainStringBuilder.deleteCharAt(lastCharIndex);
        }
        return chainStringBuilder.toString();
    }

    /**
     * Decode a LetterChain object from a string
     *
     * @param letterChainString String containing encoded information about a LetterChain object
     * @return LetterChain object
     */
    private LetterChain decodeLetterChain(String letterChainString) {
        LetterChain letterChain = new LetterChain();
        if (letterChainString.length() > 0) {
            for (String letterString : letterChainString.split(";")) {
                letterChain.add(decodeLetter(letterString));
            }
        }
        return letterChain;
    }
}
