package ogcio.vanuatulanguages;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class DictionaryActivity extends AppCompatActivity {

    ArrayList<String> keys;
    ArrayList<String> emptyList;
    ArrayAdapter<String> emptyAdapter;
    int curLanguageIndex;
    ArrayAdapter<String> adapter, searchAdapter;
    ArrayList<String> searchDictionary, keySet, dictionaryList;
    ListView listView;
    EditText searchView;
    String curLanguage;
    String curLanguageDB;
    int numCategories;
    ArrayList<String> languages;
    boolean wordSearched;
    String searchWord;
    AlertDialog alertDialog;
    String clickedWord;
    String curCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        final DBHandler myDB = new DBHandler(this);
        keys = myDB.getLanguageNames();

        Spinner language = (Spinner) findViewById(R.id.language);
        Spinner category = (Spinner) findViewById(R.id.categorySpinner);

        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        toolbar.setTitle("Vocab Lists");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        wordSearched = false;
        languages = new ArrayList<>(Arrays.asList(myDB.getWhichLanguagesString().split(",")));
        emptyList = new ArrayList<>(Collections.singletonList("Search for alternate spellings?"));
        emptyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emptyList);

        ArrayList<String> categoryList = myDB.getCategories();
        categoryList.remove("Category");
        categoryList.add(0, "All");
        curCategory = "All";
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryList);
        category.setAdapter(categoryAdapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!((TextView) view).getText().toString().equals(curCategory)) {
                    curCategory = ((TextView) view).getText().toString();
                    displayDictionaryPart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayList<String> languageList = new ArrayList<>();
        for (String key : keys) {
            if (key.contains("_")) key = firstLetterCapitalized(key.split("_")[0]) + " " + firstLetterCapitalized(key.split("_")[1]);
            else key = firstLetterCapitalized(key);
            languageList.add(key);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languageList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(dataAdapter);
        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!((TextView) view).getText().toString().equals(curLanguage)) {
                    curLanguage = ((TextView) view).getText().toString();
                    curLanguageDB = curLanguage.toUpperCase();
                    if (curLanguageDB.contains(" "))
                        curLanguageDB = curLanguageDB.replace(" ", "_");
                    switchLanguage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        listView = (ListView) findViewById(R.id.dictionary);

        dictionaryList = new ArrayList<>();
        adapter = new ArrayAdapter<>(DictionaryActivity.this, android.R.layout.simple_list_item_1, dictionaryList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wordClicked(view);
            }
        });

        searchDictionary = myDB.getAllLanguageOneWords(keys.get(curLanguageIndex), "All");
        searchAdapter = new ArrayAdapter<>(DictionaryActivity.this,
                android.R.layout.simple_list_item_1, searchDictionary);
        searchView = (EditText) findViewById(R.id.search);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        if (savedInstanceState != null) {
            curLanguage = savedInstanceState.getString("spinner");
            curLanguageDB = curLanguage.replace(" ", "_").toUpperCase();
            curLanguageIndex = keys.indexOf(curLanguageDB);
            curCategory = savedInstanceState.getString("category");
            String searchWord = savedInstanceState.getString("searchWord");
            language.setSelection(curLanguageIndex);
            category.setSelection(categoryList.indexOf(curCategory));
            this.searchWord = searchWord;
            if (!searchWord.equals("")) wordSearched = true;
            if (savedInstanceState.getString("dialog").equals("true")) {
                clickedWord = savedInstanceState.getString("word");
                showDefinitionAlertDialog();
            }
            keySet = myDB.getAllLanguageOneWords(curLanguageDB, curCategory);
            displayDictionaryPart();
        }
        else {
            curLanguageIndex = 0;
            curLanguage = keys.get(curLanguageIndex);
            curLanguageDB = keys.get(curLanguageIndex);
            if (curLanguage.contains("_")) {
                curLanguage = firstLetterCapitalized(curLanguage.split("_")[0]) + " " + firstLetterCapitalized(curLanguage.split("_")[1]);
            }
        }

        myDB.close();
    }

    public void search(CharSequence s) {
        if (s.length() == 0) listView.setAdapter(adapter);
        else {
            searchAdapter.getFilter().filter(s, new Filter.FilterListener() {
                @Override
                public void onFilterComplete(int count) {
                    if (count == 0) {
                        emptyList.clear();
                        emptyList.add("That word doesn't exist in this dictionary.\nClick here if you  would like to search for alternate spellings");
                        listView.setAdapter(emptyAdapter);
                    }
                    else listView.setAdapter(searchAdapter);
                }
            });
        }
    }

    public void showDefinitionAlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DictionaryActivity.this);
        DBHandler myDB = new DBHandler(DictionaryActivity.this);
        int size = keys.size();
        StringBuilder message = new StringBuilder();
        numCategories = myDB.numCategoriesForWord(clickedWord, keys.get(curLanguageIndex));
        for (int i = 0; i < numCategories; i++) {
            String category = myDB.getCategoryWhereWordHasMultipleCategories(clickedWord, keys.get(curLanguageIndex), i);
            if (message.length() != 0) message.append("\n\n");
            message.append("[ ").append(category).append(" ]");
            for (int j = 0; j < size; j++) {
                if (j != curLanguageIndex && languages.get(j).equals("true")) {
                    String translation = myDB.getTranslationByCategory(clickedWord, keys.get(j), keys.get(curLanguageIndex), category);
                    if (!translation.equals("Not in dictionary"))
                        message.append("\n").append(firstLetterCapitalized(keys.get(j)))
                                .append(": ").append(translation);
                }
            }
        }

        if (clickedWord.contains("''")) clickedWord = clickedWord.replace("''", "'");
        alert.setTitle(clickedWord);
        alert.setMessage(message);
        alert.setPositiveButton("Close", null);
        myDB.close();

        alertDialog = alert.create();
        alertDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (alertDialog != null && alertDialog.isShowing()) {
            savedInstanceState.putString("dialog", "true");
            savedInstanceState.putString("word", clickedWord);
        }
        else {
            savedInstanceState.putString("dialog", "false");
            savedInstanceState.putString("word", "");
        }
        savedInstanceState.putString("spinner", curLanguage);
        savedInstanceState.putString("category", curCategory);
        savedInstanceState.putString("searchWord", ((TextView) findViewById(R.id.search)).getText().toString());
    }

    public void wordClicked(View view) {
        clickedWord = ((TextView) view).getText().toString();
        if (clickedWord.contains("'")) clickedWord = clickedWord.replace("'", "''");
        String searchWord = capitalize(((EditText) findViewById(R.id.search)).getText().toString());
        if (clickedWord.equals("That word doesn't exist in this dictionary.\nClick here if you  would like to search for alternate spellings")) {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            emptyList.clear();
            ArrayList<String> twoLettersOff = new ArrayList<>();
            for (String wordToCheck : keySet) {
                int distance = computeDistance(searchWord, wordToCheck);
                if (distance <= 1) emptyList.add(wordToCheck);
                if (distance == 2) twoLettersOff.add(wordToCheck);
                if (!emptyList.contains(wordToCheck) && wordToCheck.contains(searchWord)
                        && computeDistance(searchWord, wordToCheck) <= 4)
                    emptyList.add(wordToCheck);
                if (!emptyList.contains(wordToCheck) && wordToCheck.contains(" ")) {
                    String words[] = wordToCheck.split(" ");
                    for (String splitWord : words) {
                        if (searchWord.equals(splitWord)) {
                            emptyList.add(wordToCheck);
                            break;
                        }
                    }
                }
            }
            if (emptyList.isEmpty()) emptyList.addAll(twoLettersOff);
            if (emptyList.isEmpty())
                emptyList.add("Not sure what word you're trying for...try a different spelling?");
            emptyAdapter.notifyDataSetChanged();
        }
        else showDefinitionAlertDialog();
    }

    /**
     * Returns the edit distance needed to convert string s1 to s2
     * If returns 0, the strings are same
     * If returns 1, that means either a character is added, removed or replaced
     */
    public static int computeDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    public void switchLanguage() {
        DBHandler myDB = new DBHandler(DictionaryActivity.this);
        curLanguageIndex = keys.indexOf(curLanguageDB);

        keySet = myDB.getAllLanguageOneWords(curLanguageDB, "All");
        Collections.sort(keySet, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareToIgnoreCase(t1);
            }
        });

        displayDictionaryPart();
        searchDictionary = myDB.getAllLanguageOneWords(keys.get(curLanguageIndex), "All");
        searchAdapter = new ArrayAdapter<>(DictionaryActivity.this,
                android.R.layout.simple_list_item_1, searchDictionary);
        if (wordSearched) {
            searchView.setText(searchWord);
            wordSearched = false;
        }
        else searchView.setText("");
        myDB.close();
    }

    @Override
    protected void onDestroy() {
        if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
        super.onDestroy();
    }

    public void displayDictionaryPart() {
        DBHandler myDB = new DBHandler(DictionaryActivity.this);
        dictionaryList.clear();
        if (curCategory.equals("All")) dictionaryList.addAll(keySet);
        else {
            ArrayList<String> categoryList = myDB.getWordsByCategory(curLanguageDB, curCategory);
            Collections.sort(categoryList, new Comparator<String>() {
                @Override
                public int compare(String s, String t1) {
                    return s.compareToIgnoreCase(t1);
                }
            });
            dictionaryList.addAll(categoryList);
        }
        if (dictionaryList.size() == 0) dictionaryList.add("No '" + curCategory + "' words in " + curLanguage);
        adapter.notifyDataSetChanged();
        myDB.close();
    }

    private String capitalize(final String line) {
        if (line.equals("")) return "";
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    private String firstLetterCapitalized(final String line) {
        if (line.equals("")) return "";
        else return line.substring(0,1) + line.substring(1).toLowerCase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
