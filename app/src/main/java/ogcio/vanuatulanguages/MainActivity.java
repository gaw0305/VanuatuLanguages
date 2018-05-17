package ogcio.vanuatulanguages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Written by Grace Whitmore
 *
 * The main window of the application, contains buttons to all the other activities, and builds
 * the databases if they do not exist.
 */
public class MainActivity extends AppCompatActivity {

    ArrayList<String> keys;
    BufferedReader fileReader = null;

    /**
     * Sets up the page
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildDB();
    }

    /**
     * Starts the dictionary activity when the dictionary button is clicked
     * @param view the current view that is passed to the button that is clicked
     */
    public void dictionaryButtonClicked(View view) {
        Intent intent = new Intent(this, DictionaryActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the flash cards activity when the flash cards button is clicked
     * @param view the current view that is passed to the button that is clicked
     */
    public void flashCardsButtonClicked(View view) {
        Intent intent = new Intent(this, ChooseFlashCardSet.class);
        startActivity(intent);
    }

    /**
     * Starts the sentences activity when the sentences button is clicked
     * @param view the current view that is passed to the button that is clicked
     */
    public void sentencesButtonClicked(View view) {
        Intent intent = new Intent(this, SentencesActivity.class);
        startActivity(intent);
    }

    /**
     * Builds the database, if it has not already been built
     */
    public void buildDB() {
        final DBHandler myDB = new DBHandler(this);
        final String DELIMITER = ",";

        if (myDB.isEmpty("DICTIONARY")) {
            try {
                String line;
                InputStream inputStream = MainActivity.this.getResources().openRawResource(R.raw.dictionary);

                ArrayList<String> values;

                boolean header = true;

                fileReader = new BufferedReader(new InputStreamReader(inputStream, "Windows-1252"));
                ArrayList<String> categories = new ArrayList<>();
                while ((line = fileReader.readLine()) != null) {
                    if (line.trim().length() > 0) {
                        values = new ArrayList<>(Arrays.asList(line.split(DELIMITER)));
                        ArrayList<String> trimmedValues = new ArrayList<>();
                        for (int i = 0; i < values.size(); i++)
                            trimmedValues.add(values.get(i).trim());
                        if (header) {
                            myDB.insertWord(values);
                            header = false;
                        }
                        else myDB.insertWord(trimmedValues);
                        String category = trimmedValues.get(trimmedValues.size() - 1);
                        if (!categories.contains(category) && !category.equals("Category")) {
                            categories.add(category);
                            myDB.insertCategory(category);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (fileReader != null) try { fileReader.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
            keys = myDB.getLanguageNames();

            ArrayList<String> categories = myDB.getCategories();
            for (int i = 0; i < categories.size(); i++) {
                if (!categories.get(i).equals("Sentences")) {
                    ArrayList<String> nums = myDB.getNumWordsInCategory(categories.get(i));
                    for (int j = 0; j < nums.size(); j++) {
                        myDB.updateWordsCount(categories.get(i), nums.get(j), "UNKNOWN_WORDS_" + keys.get(j));
                    }
                }
            }
        }
        if (myDB.isEmpty("SETTINGS"))
            myDB.initializeSettings();
        myDB.close();
    }
}


