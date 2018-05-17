package ogcio.vanuatulanguages;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Written by Grace Whitmore
 *
 * The Activity Controlling the Sentences UI. Allows the user to see translations of different
 * helpful sentences in various languages. If they are in developer mode, the user can add new
 * sentences, delete others, and edit those that are already in the app
 */
public class SentencesActivity extends AppCompatActivity {

    Spinner firstLanguage;
    Spinner secondLanguage;
    DBHandler myDB;
    ArrayList<String> sentencesList;
    ArrayAdapter<String> adapter;
    ListView sentencesListView;
    String secondLanguageString;
    String languageOne;
    String languageTwo;
    String languageOneDB;
    String languageTwoDB;
    String secondLanguageStringDB;
    ArrayList<String> keys;
    String sentence;

    /**
     * Sets up the page, including loading the savedInstanceState as needed
     * Sets the spinners at the top of the screen to their default position of English and Bislama
     * Sets up the onItemClickListener for the ListView containing the list of sentences
     *
     * @param savedInstanceState loads the savedInstanceState, if it exists
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentences);

        myDB = new DBHandler(this);
        keys = myDB.getLanguageNames();

        // Sets up the toolbar with it's title and adds a back button to navigate to the previous
        // screen
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        toolbar.setTitle("Helpful Sentences");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Sets up the default sentences list to display the English sentences
        sentencesList = myDB.getAllLanguageOneWords("ENGLISH", "Sentences");
        sentencesListView = (ListView) findViewById(R.id.sentences);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sentencesList);
        sentencesListView.setAdapter(adapter);

        // The onItemClickListener for the list of sentences
        sentencesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SentencesActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.sentence, null);
                builder.setView(dialogView);
                // Gets the sentence that was clicked
                sentence = ((TextView) view).getText().toString();
                if (sentence.contains("'")) sentence = sentence.replace("'", "''");
                // Gets the name of the language that should be displayed as the translated sentence
                // If it is a user added language, saves the string as "EXTRA", which is how it is
                // accessed in the database
                secondLanguageString = secondLanguage.getSelectedItem().toString();
                // Gets the translated string in the chosen language and sets it as the message
                // of the AlertDialog

//                final String message = "";
                final String message = myDB.getTranslationByCategory(sentence,
                        secondLanguageString.toUpperCase().replace(" ", "_"),
                        firstLanguage.getSelectedItem().toString().toUpperCase().replace(" ", "_"),
                        "Sentences");
                ((TextView) dialogView.findViewById(R.id.textView)).setText(message);
                if (sentence.contains("''")) sentence = sentence.replace("''", "'");
                builder.setTitle(sentence);
                // If done is clicked, the AlertDialog closes
                builder.setPositiveButton("Done", null);
                // If the user is in developer mode, add some other options to the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Creates the spinners, adding the language options, including a user inputted language,
        // if applicable
        firstLanguage = (Spinner) findViewById(R.id.firstLanguage);
        secondLanguage = (Spinner) findViewById(R.id.secondLanguage);

        ArrayList<String> firstLanguageList = new ArrayList<>();
        for (String key : keys) {
            if (key.contains("_")) {
                key = firstLetterCapitalized(key.split("_")[0])
                        + " " + firstLetterCapitalized(key.split("_")[1]);
                firstLanguageList.add(key);
            }
            else firstLanguageList.add(firstLetterCapitalized(key));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, firstLanguageList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstLanguage.setAdapter(dataAdapter);
        // Default selection is English
        firstLanguage.setSelection(0);
        // Sets the onItemSelectedListener, making it so that the two spinners cannot select the
        // same language (which would mean the translation would be the same as the original sentence)
        // Updates the ListView based on which language is selected
        firstLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageOne = firstLanguage.getSelectedItem().toString();
                sentencesList = myDB.getAllLanguageOneWords(languageOne.toUpperCase().replace(" ", "_"), "Sentences");
                adapter = new ArrayAdapter<>(SentencesActivity.this, android.R.layout.simple_list_item_1, sentencesList);
                sentencesListView.setAdapter(adapter);
                if (firstLanguage.getSelectedItem().toString().equals(secondLanguage.getSelectedItem().toString())) {
                    if (keys.indexOf(secondLanguage.getSelectedItem().toString().toUpperCase().replace(" ", "_")) == 0)
                        secondLanguage.setSelection(1);
                    else secondLanguage.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ArrayList <String> secondLanguageList = new ArrayList<>();
        for (String key : keys) {
            if (key.contains("_")) {
                key = firstLetterCapitalized(key.split("_")[0])
                        + " " + firstLetterCapitalized(key.split("_")[1]);
                secondLanguageList.add(key);
            }
            else secondLanguageList.add(firstLetterCapitalized(key));
        }
        dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, secondLanguageList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondLanguage.setAdapter(dataAdapter);
        // Default selection is Bislama
        secondLanguage.setSelection(1);
        // Sets the onItemSelectedListener, making it so that the two spinners cannot select the
        // same language (which would mean the translation would be the same as the original sentence)
        secondLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageTwo = secondLanguage.getSelectedItem().toString();
                if (firstLanguage.getSelectedItem().toString().equals(secondLanguage.getSelectedItem().toString())) {
                    if (keys.indexOf(firstLanguage.getSelectedItem().toString().toUpperCase().replace(" ", "_")) == 0)
                        firstLanguage.setSelection(1);
                    else firstLanguage.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        myDB.close();
    }

    /**
     * @param line the string to be returned that should have a capitalized first letter and
     *             all other letter lowercase
     * @return the string with a capitalized first letter and all other letters lowercase
     */
    private String firstLetterCapitalized(final String line) {
        if (line.equals("")) return "";
        else return line.substring(0,1) + line.substring(1).toLowerCase();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("languageOne", languageOne);
        savedInstanceState.putString("languageTwo", languageTwo);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
