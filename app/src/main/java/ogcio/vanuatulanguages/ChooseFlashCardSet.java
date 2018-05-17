package ogcio.vanuatulanguages;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class ChooseFlashCardSet extends AppCompatActivity {

    ArrayList<FlashCardWord> flashCardSetNames;
    String frontOfCardLanguage = "";
    String backOfCardLanguage = "";
    String frontOfCardLanguageDB = "";
    String backOfCardLanguageDB = "";
    int languageIndex;
    int front;
    int back;
    CustomWordAdapter arrayAdapter;
    ListView listView;
    ArrayList<String> keys;
    Handler handler;
    ProgressDialog progressDialog;
    int languageChosen;
    ArrayList<String> languageList;
    ArrayAdapter<String> languageAdapter;
    Spinner chooseLanguageSpinner;
    boolean languagesAlreadyChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_flash_cards);

        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        toolbar.setTitle("Flash Cards");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState != null) {
            frontOfCardLanguage = savedInstanceState.getString("frontOfCard");
            frontOfCardLanguageDB = frontOfCardLanguage.toUpperCase().replace(" ", "_");
            backOfCardLanguage = savedInstanceState.getString("backOfCard");
            backOfCardLanguageDB = backOfCardLanguage.toUpperCase().replace(" ", "_");
            front = savedInstanceState.getInt("front");
            back = savedInstanceState.getInt("back");
            languageIndex = savedInstanceState.getInt("languageIndex");

            if (!frontOfCardLanguage.equals("") && !backOfCardLanguage.equals("")) {
                languagesAlreadyChosen = true;
                showFlashCards();
            }
            else chooseLanguage();
        }
        else chooseLanguage();
    }

    public void chooseLanguage() {
        findViewById(R.id.chooseLanguagesRelativeLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.chooseFlashCardsRelativeLayout).setVisibility(View.INVISIBLE);
        languageChosen = 0;

        DBHandler myDB = new DBHandler(ChooseFlashCardSet.this);
        chooseLanguageSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayList<String> languages = myDB.getLanguageNames();
        languageList = new ArrayList<>();
        for (String language : languages) {
            if (language.contains("_")) {
                language = firstLetterCapitalized(language.split("_")[0])
                        + " " + firstLetterCapitalized(language.split("_")[1]);
                languageList.add(language);
            }
            else languageList.add(firstLetterCapitalized(language));
        }
        languageList.add(0, "Select a Language");
        languageAdapter = new ArrayAdapter<>(ChooseFlashCardSet.this,
                android.R.layout.simple_spinner_item, languageList);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseLanguageSpinner.setAdapter(languageAdapter);
        chooseLanguageSpinner.setSelection(0);
        chooseLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    String language = ((TextView) view).getText().toString();
                    if (languageChosen == 0 && !language.equals("Select a Language")) {
                        frontOfCardLanguage = language;
                        frontOfCardLanguageDB = frontOfCardLanguage.toUpperCase().replace(" ", "_");
                        languageChosen = 1;
                        languageList.remove(i);
                        chooseLanguageSpinner.setSelection(0);
                        languageAdapter.notifyDataSetChanged();
                        ((TextView) findViewById(R.id.chooseLanguageTextView)).setText("Back of card language:");
                    }
                    else if (!language.equals("Select a Language")){
                        backOfCardLanguage = language;
                        backOfCardLanguageDB = backOfCardLanguage.toUpperCase().replace(" ", "_");
                        languagesAlreadyChosen = true;
                        showFlashCards();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        myDB.close();
    }

    public void showFlashCards() {
        findViewById(R.id.chooseLanguagesRelativeLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.chooseFlashCardsRelativeLayout).setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.flashCardOptions);
        DBHandler myDB = new DBHandler(this);
        flashCardSetNames = new ArrayList<>();
        arrayAdapter = new CustomWordAdapter(ChooseFlashCardSet.this, R.layout.word, flashCardSetNames);
        listView.setAdapter(arrayAdapter);

        keys = myDB.getLanguageNames();

        final Spinner frontOfCard = (Spinner) findViewById(R.id.frontOfCard);
        final Spinner backOfCard = (Spinner) findViewById(R.id.backOfCard);

        ArrayList<String> frontOfCardList = new ArrayList<>();
        frontOfCardList.add("Front of card");
        for (String key : keys) {
            if (key.contains("_")) {
                key = firstLetterCapitalized(key.split("_")[0])
                        + " " + firstLetterCapitalized(key.split("_")[1]);
                frontOfCardList.add(key);
            }
            else frontOfCardList.add(firstLetterCapitalized(key));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, frontOfCardList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frontOfCard.setAdapter(dataAdapter);

        frontOfCard.setSelection(keys.indexOf(frontOfCardLanguageDB) + 1);
        frontOfCard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    String languageString = ((TextView) view).getText().toString();
                    if (!languageString.equals("Front of card")) {
                        front = keys.indexOf(languageString.toUpperCase().replace(" ", "_"));
                        String curLanguage = frontOfCardLanguage;
                        frontOfCardLanguage = languageString;
                        frontOfCardLanguageDB = frontOfCardLanguage.toUpperCase().replace(" ", "_");
                        if (frontOfCardLanguage.equals(backOfCardLanguage))
                            backOfCard.setSelection(keys.indexOf(curLanguage.toUpperCase()) + 1);
                        else if (!languagesAlreadyChosen && !backOfCardLanguage.equals("")) {
                            setLanguageIndex();
                            updateCategoryList();
                        }
                        else languagesAlreadyChosen = false;
                    }
                    else frontOfCardLanguage = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        final ArrayList<String> backOfCardList = new ArrayList<>();
        backOfCardList.add("Back of card");
        for (String key : keys) {
            if (key.contains("_")) {
                key = firstLetterCapitalized(key.split("_")[0])
                        + " " + firstLetterCapitalized(key.split("_")[1]);
                backOfCardList.add(key);
            }
            else backOfCardList.add(firstLetterCapitalized(key));
        }
        dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, backOfCardList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        backOfCard.setAdapter(dataAdapter);
        backOfCard.setSelection(keys.indexOf(backOfCardLanguage.toUpperCase().replace(" ", "_")) + 1);
        backOfCard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    String languageString = ((TextView) view).getText().toString();
                    if (!languageString.equals("Back of card")) {
                        back = keys.indexOf(languageString.toUpperCase());
                        String curLanguage = backOfCardLanguage;
                        backOfCardLanguage = languageString;
                        backOfCardLanguageDB = backOfCardLanguage.toUpperCase().replace(" ", "_");
                        if (backOfCardLanguage.equals(frontOfCardLanguage))
                            frontOfCard.setSelection(keys.indexOf(curLanguage.toUpperCase()) + 1);
                        else if (!languagesAlreadyChosen && !frontOfCardLanguage.equals("")) {
                            setLanguageIndex();
                            updateCategoryList();
                        }
                        else languagesAlreadyChosen = false;
                    } else
                        backOfCardLanguage = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final View v = view;

                if (frontOfCard.getSelectedItem().toString().equals("Front of card"))
                    Toast.makeText(ChooseFlashCardSet.this, "Choose a language for the front of the card", Toast.LENGTH_LONG).show();
                else if (backOfCard.getSelectedItem().toString().equals("Back of card"))
                    Toast.makeText(ChooseFlashCardSet.this, "Choose a language for the back of the card", Toast.LENGTH_LONG).show();
                else if (frontOfCard.getSelectedItem().equals(backOfCard.getSelectedItem()))
                    Toast.makeText(ChooseFlashCardSet.this, "Choose different languages for front and back", Toast.LENGTH_LONG).show();
                else if (view.findViewById(R.id.imageView).getVisibility() == View.VISIBLE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChooseFlashCardSet.this);
                    builder.setMessage("Would you like to restart this stack?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHandler myDB = new DBHandler(ChooseFlashCardSet.this);
                            String category = ((TextView) v.findViewById(R.id.category)).getText().toString();
                            ArrayList<String> nums = myDB.getNumWordsInCategory(category);
                            for (int i = 0; i < nums.size(); i++) {
                                myDB.updateWordsCount(category, nums.get(i), "UNKNOWN_WORDS_" + keys.get(i));
                                myDB.updateWordsCount(category, "0,0,0", "KNOWN_WORDS_" + keys.get(i));
                                myDB.updateWordsCount(category, "0,0,0", "LEARNING_WORDS_" + keys.get(i));

                            }
                            goToNextPage(frontOfCard, backOfCard, v, true);
                            myDB.close();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    builder.create().show();
                }
                else
                    goToNextPage(frontOfCard, backOfCard, view, false);
            }
        });

        myDB.close();
    }

    private String firstLetterCapitalized(final String line) {
        if (line.equals("")) return "";
        else return line.substring(0,1) + line.substring(1).toLowerCase();
    }

    public void goToNextPage(Spinner frontOfCard, Spinner backOfCard, View view, boolean restart) {
        Intent intent = new Intent(ChooseFlashCardSet.this, FlashCardsActivity.class);
        intent.putExtra("category", ((TextView) view.findViewById(R.id.category)).getText().toString());
        intent.putExtra("frontWord", frontOfCard.getSelectedItem().toString().toUpperCase());
        intent.putExtra("backWord", backOfCard.getSelectedItem().toString().toUpperCase());
        intent.putExtra("front", front);
        intent.putExtra("back", back);
        intent.putExtra("restart", restart);
        startActivity(intent);
    }

    public void setLanguageIndex() {
        languageIndex = 0;
        DBHandler myDB = new DBHandler(ChooseFlashCardSet.this);
        languageIndex = myDB.getLanguageInt(frontOfCardLanguageDB, backOfCardLanguageDB);
        myDB.close();
    }

    public void updateCategoryList() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what==0) {
                    progressDialog.dismiss();
                    arrayAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                DBHandler myDB = new DBHandler(ChooseFlashCardSet.this);
                ArrayList<String> flashCardCategories = myDB.getCategories();
                flashCardCategories.remove("Sentences");
                flashCardSetNames.clear();
                for (String category : flashCardCategories) {
                    int amount = myDB.getCategoryWordsForFlashCards(category, frontOfCardLanguageDB, backOfCardLanguageDB).size();
                    if (amount != 0) {
                        String knownWords = myDB.getWordsCount(category, "KNOWN_WORDS_" + frontOfCardLanguageDB).split(",")[languageIndex];
                        String unknownWords = myDB.getWordsCount(category, "UNKNOWN_WORDS_" + frontOfCardLanguageDB).split(",")[languageIndex];
                        String learningWords = myDB.getWordsCount(category, "LEARNING_WORDS_" + frontOfCardLanguageDB).split(",")[languageIndex];

                        if (unknownWords.equals("0") && learningWords.equals("0"))
                            flashCardSetNames.add(new FlashCardWord(category, knownWords, unknownWords,
                                    learningWords, true));
                        else
                            flashCardSetNames.add(new FlashCardWord(category, knownWords, unknownWords,
                                    learningWords, false));
                    }
                }
                flashCardSetNames.add(0, new FlashCardWord("Starred Cards", "-1",
                        Integer.toString(myDB.getStarredCards(languageIndex, front).size()), "-1", false));
                myDB.close();
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("frontOfCard", frontOfCardLanguage);
        savedInstanceState.putString("backOfCard", backOfCardLanguage);
        savedInstanceState.putInt("front", front);
        savedInstanceState.putInt("back", back);
        savedInstanceState.putInt("languageIndex", languageIndex);
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        languagesAlreadyChosen = true;
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!backOfCardLanguage.equals("") && !frontOfCardLanguage.equals("")) updateCategoryList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}