package ogcio.vanuatulanguages;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class FlashCardsActivity extends AppCompatActivity {
    ArrayList<String> keysArray;
    String flip = "a";
    int nextInt = 0;
    String curWord = "";
    Boolean cardFlipped = false;
    String curLanguage;
    String backOfCard;
    AlertDialog alertDialog;
    boolean showScore = true;
    String categoryName;
    int knownWords;
    int unknownWords;
    int learningWords;
    int languageIndex;
    int front;
    int back;
    boolean starredCards = false;
    String starredCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flash_cards);

        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        toolbar.setTitle("Flash Cards");
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DBHandler myDB = new DBHandler(this);

        ArrayList<String> keys = myDB.getLanguageNames();

        if (savedInstanceState != null) {
            curLanguage = savedInstanceState.getString("frontOfCard");
            backOfCard = savedInstanceState.getString("backOfCard");
            front = savedInstanceState.getInt("front");
            back = savedInstanceState.getInt("back");
            curWord = savedInstanceState.getString("word");
            flip = savedInstanceState.getString("flip");
            nextInt = savedInstanceState.getInt("nextInt");
            cardFlipped = savedInstanceState.getBoolean("cardFlipped");
            categoryName = savedInstanceState.getString("categoryName");
            languageIndex = savedInstanceState.getInt("languageIndex");
            keysArray = savedInstanceState.getStringArrayList("keysArray");
            unknownWords = savedInstanceState.getInt("unknownWords");
            knownWords = savedInstanceState.getInt("knownWord");
            learningWords = savedInstanceState.getInt("learningWords");
        }
        else {
            categoryName = getIntent().getStringExtra("category");
            curLanguage = getIntent().getStringExtra("frontWord").toUpperCase();
            backOfCard = getIntent().getStringExtra("backWord").toUpperCase();
            front = getIntent().getIntExtra("front", 0);
            back = getIntent().getIntExtra("back", 0);

            if (categoryName.equals("Starred Cards")) {
                myDB = new DBHandler(FlashCardsActivity.this);
                if (myDB.showStarredCardsDialog()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FlashCardsActivity.this);
                    builder.setTitle("Starred Cards");
                    builder.setMessage("Starred Cards are a little different from the other flash card " +
                            "sets. Rather than keeping track of what you get right and wrong in order " +
                            "to remove cards from the stack, words in the Starred Cards stack will remain " +
                            "until you remove the star from them.");
                    builder.setPositiveButton("Got it!", null);
                    builder.setNegativeButton("Don't show again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHandler myDB = new DBHandler(FlashCardsActivity.this);
                            myDB.updateShowStarredCardsDialog();
                            myDB.close();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                myDB.close();
                starredCards = true;
            }

            languageIndex = myDB.getLanguageInt(curLanguage.toUpperCase().replace(" ", "_"),
                    backOfCard.toUpperCase().replace(" ", "_"));
            keysArray = new ArrayList<>();
            if (starredCards) {
                int starNum = keys.indexOf(curLanguage.toUpperCase().replace(" ", "_"));
                keysArray = myDB.getStarredCards(languageIndex, starNum);
            }
            else keysArray = myDB.getCategoryWordsForFlashCards(categoryName, curLanguage.toUpperCase().replace(" ", "_"),
                    backOfCard.toUpperCase().replace(" ", "_"));

            if (keysArray.size() == 0) ((TextView) findViewById(R.id.flashCardContent)).setText(R.string.empty_stack);
            else if (starredCards)
                unknownWords = keysArray.size();
            else {
                knownWords = Integer.parseInt(myDB.getSingleWordCount(categoryName,
                        "KNOWN_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                        languageIndex));
                unknownWords = Integer.parseInt(myDB.getSingleWordCount(categoryName,
                        "UNKNOWN_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                        languageIndex));
                learningWords = Integer.parseInt(myDB.getSingleWordCount(categoryName,
                        "LEARNING_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                        languageIndex));
            }

            if (getIntent().getBooleanExtra("restart", false)) {
                for (String word : keysArray) {
                    myDB.updateFirstTimeSeen(languageIndex, 0, curLanguage.toUpperCase().replace(" ", "_"), word.replace("'", "''"), categoryName);
                }
            }
        }
        if (starredCards) {
            String unknownString = Integer.toString(unknownWords);
            ((TextView) findViewById(R.id.unknownWordsNumber)).setText(unknownString);
            findViewById(R.id.knownWordsNumber).setVisibility(View.INVISIBLE);
            findViewById(R.id.learningWordsNumber).setVisibility(View.INVISIBLE);
            findViewById(R.id.knownWordsTitle).setVisibility(View.INVISIBLE);
            findViewById(R.id.learningWordsTitle).setVisibility(View.INVISIBLE);
        }
        else {
            String unknownString = Integer.toString(unknownWords);
            String knownString = Integer.toString(knownWords);
            String learningString = Integer.toString(learningWords);
            ((TextView) findViewById(R.id.unknownWordsNumber)).setText(unknownString);
            ((TextView) findViewById(R.id.knownWordsNumber)).setText(knownString);
            ((TextView) findViewById(R.id.learningWordsNumber)).setText(learningString);
        }

        showScore = true;


        if (knownWords == 0 && unknownWords == 0 && learningWords == 0)
            ((TextView) findViewById(R.id.flashCardContent)).setText(R.string.empty_stack);
        else if (knownWords == keysArray.size())
            ((TextView) findViewById(R.id.flashCardContent)).setText(R.string.stack_complete);
        else if (savedInstanceState != null) {
            nextInt--;
            showNewWord();
        }
        else{
            shuffleData();
            showNewWord();
        }
        myDB.close();
    }

    public void starClicked(View view) {
        DBHandler myDB = new DBHandler(this);
        ImageView starOff = (ImageView) findViewById(R.id.starOff);
        ImageView starOn = (ImageView) findViewById(R.id.starOn);
        if (starOff.getVisibility() == View.VISIBLE) {
            starOff.setVisibility(View.GONE);
            starOn.setVisibility(View.VISIBLE);
            if (starredCards) {
                String unknownString = Integer.toString(unknownWords);
                ((TextView) findViewById(R.id.unknownWordsNumber)).setText(unknownString);
                myDB.updateStar(1, languageIndex, curWord.replace("'", "''"),
                        curLanguage.toUpperCase().replace(" ", "_"), categoryName);
            }
            else myDB.updateStar(1, languageIndex, curWord.replace("'", "''"),
                    curLanguage.toUpperCase().replace(" ", "_"), categoryName);

        }
        else {
            starOff.setVisibility(View.VISIBLE);
            starOn.setVisibility(View.GONE);
            if (categoryName.equals("Starred Cards"))
                myDB.updateStar(0, languageIndex, curWord, curLanguage.toUpperCase(), myDB.getCategory(curWord, curLanguage));
            else
                myDB.updateStar(0, languageIndex, curWord, curLanguage.toUpperCase(), categoryName);
            if (starredCards) {
                unknownWords--;
                String unknownString = Integer.toString(unknownWords);
                ((TextView) findViewById(R.id.unknownWordsNumber)).setText(unknownString);
                String category = myDB.getCategory(curWord, curLanguage);
                myDB.updateStar(0, languageIndex, curWord, curLanguage.toUpperCase(), category);
                showNewWord();
            }
            else myDB.updateStar(0, languageIndex, curWord, curLanguage.toUpperCase(), categoryName);
        }
        myDB.close();
    }

    public void shuffleData() {
        Random random = new Random();
        for (int i = 0; i < keysArray.size(); i++) {
            int index1 = random.nextInt(keysArray.size());
            int index2 = random.nextInt(keysArray.size());

            String tmp = keysArray.get(index1);
            keysArray.set(index1, keysArray.get(index2));
            keysArray.set(index2, tmp);
        }
    }

    public void showNewWord() {
        DBHandler myDB = new DBHandler(this);
        if (nextInt >= keysArray.size())
            stackFinished();
        else {
//            curWord = keysArray.get(nextInt);
            if (starredCards) {
                curWord = keysArray.get(nextInt).split(",")[0];
                starredCategory = keysArray.get(nextInt).split(",")[1];
            }
            else curWord = keysArray.get(nextInt);
            if (myDB.isStarred(curWord.replace("'", "''"),
                    curLanguage.toUpperCase().replace(" ", "_"), languageIndex,
                    "STAR_" + curLanguage.toUpperCase().replace(" ", "_"),
                    starredCards) || starredCards) {
                findViewById(R.id.starOn).setVisibility(View.VISIBLE);
                findViewById(R.id.starOff).setVisibility(View.GONE);
            }
            else {
                findViewById(R.id.starOn).setVisibility(View.GONE);
                findViewById(R.id.starOff).setVisibility(View.VISIBLE);
            }
            while (curWord.equals("Not in dictionary") || translate().equals("Not in dictionary")
                    || (myDB.isFirstTimeSeen(curLanguage.toUpperCase().replace(" ", "_"),
                    curWord.replace("'", "''"), languageIndex, categoryName) == 4
                    && !starredCards)) {
                nextInt++;
                if (nextInt >= keysArray.size()) {
                    curWord = "";
                    stackFinished();
                    break;
                }
                else if (starredCards) {
                    curWord = keysArray.get(nextInt).split(",")[0];
                    starredCategory = keysArray.get(nextInt).split(",")[1];
                }
                else curWord = keysArray.get(nextInt);
            }
            nextInt++;
            ((TextView) findViewById(R.id.flashCardContent)).setText(curWord);
            cardFlipped = false;
            flip = "a";
        }
        myDB.close();
    }

    public void stackFinished() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FlashCardsActivity.this);
        if (unknownWords == 0 && learningWords == 0) {
            builder.setMessage("Congratulations, you've finished the stack!");
            builder.setNegativeButton("Good for me!", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        else {
            builder.setMessage("End of stack reached. Shuffle before restarting stack?");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nextInt = 0;
                    showNewWord();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    nextInt = 0;
                    shuffleData();
                    showNewWord();
                }
            });
        }
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void flashCardButtonClicked(View view) {
        if (!curWord.equals("") && !((TextView) view.findViewById(R.id.flashCardContent)).getText().toString().equals("Empty Stack")) {
            if (flip.equals("a")) {
                String translation = translate();

                ((TextView) findViewById(R.id.flashCardContent)).setText(translation);
                flip = "b";

                if (starredCards) findViewById(R.id.next).setVisibility(View.VISIBLE);
                else {
                    findViewById(R.id.rightAnswer).setVisibility(View.VISIBLE);
                    findViewById(R.id.wrongAnswer).setVisibility(View.VISIBLE);
                }
            } else {
                ((TextView) findViewById(R.id.flashCardContent)).setText(curWord);
                flip = "a";
                if (starredCards) findViewById(R.id.next).setVisibility(View.INVISIBLE);
                else {
                    findViewById(R.id.rightAnswer).setVisibility(View.INVISIBLE);
                    findViewById(R.id.wrongAnswer).setVisibility(View.INVISIBLE);
                }
            }
            cardFlipped = true;
        }
    }

    public String translate() {
        DBHandler myDB = new DBHandler(this);
        String curWordNoComma = curWord;
        String translation;
        if (curWord.contains(", ")) curWordNoComma = curWord.split(", ")[0];
//        return "";
        if (!categoryName.equals("Starred Cards"))
            translation = myDB.getTranslationByCategory(curWordNoComma.replace("'", "''"),
                    backOfCard.toUpperCase().replace(" ", "_"),
                    curLanguage.toUpperCase().replace(" ", "_"), categoryName);
//        String category = keysArray.get(nextInt - 1).split(",")[1];
        else translation = myDB.getTranslationByCategory(curWordNoComma.replace("'", "''"),
                backOfCard.toUpperCase().replace(" ", "_"),
                curLanguage.toUpperCase().replace(" ", "_"), starredCategory);
        myDB.close();
        return translation;
    }

    public void nextClicked(View view) {
        findViewById(R.id.next).setVisibility(View.INVISIBLE);
        showNewWord();
    }

    public void knownWordClicked(View view) {
        DBHandler myDB = new DBHandler(this);
        int curScore = myDB.isFirstTimeSeen(curLanguage.toUpperCase().replace(" ", "_"), curWord.replace("'", "''"), languageIndex, categoryName);
        if (curScore == 0) {
            knownWords++;
            unknownWords--;
            myDB.updateSingleWordCount(categoryName, Integer.toString(unknownWords),
                    "UNKNOWN_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateSingleWordCount(categoryName, Integer.toString(knownWords),
                    "KNOWN_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateFirstTimeSeen(languageIndex, 4,
                    curLanguage.toUpperCase().replace(" ", "_"),
                    curWord.replace("'", "''"), categoryName);
        }
        else if (curScore == 1) {
            learningWords++;
            unknownWords--;
            myDB.updateSingleWordCount(categoryName, Integer.toString(learningWords),
                    "LEARNING_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateSingleWordCount(categoryName, Integer.toString(unknownWords),
                    "UNKNOWN_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateFirstTimeSeen(languageIndex, 2,
                    curLanguage.toUpperCase().replace(" ", "_"),
                    curWord.replace("'", "''"), categoryName);
        }
        else if (curScore == 2) {
            myDB.updateFirstTimeSeen(languageIndex, 3, curLanguage.toUpperCase().replace(" ", "_"),
                    curWord.replace("'", "''"), categoryName);
        }
        else if (curScore == 3) {
            knownWords++;
            learningWords--;
            myDB.updateSingleWordCount(categoryName, Integer.toString(learningWords),
                    "LEARNING_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateSingleWordCount(categoryName, Integer.toString(unknownWords),
                    "UNKNOWN_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateFirstTimeSeen(languageIndex, 4,
                    curLanguage.toUpperCase().replace(" ", "_"),
                    curWord.replace("'", "''"), categoryName);
        }
        String unknownString = Integer.toString(unknownWords);
        String knownString = Integer.toString(knownWords);
        String learningString = Integer.toString(learningWords);
        ((TextView) findViewById(R.id.unknownWordsNumber)).setText(unknownString);
        ((TextView) findViewById(R.id.knownWordsNumber)).setText(knownString);
        ((TextView) findViewById(R.id.learningWordsNumber)).setText(learningString);
        showNewWord();
        findViewById(R.id.rightAnswer).setVisibility(View.GONE);
        findViewById(R.id.wrongAnswer).setVisibility(View.GONE);
        myDB.close();
    }

    public void unknownWordClicked(View view) {
        DBHandler myDB = new DBHandler(this);
        int curScore = myDB.isFirstTimeSeen(curLanguage.toUpperCase().replace(" ", "_"),
                curWord.replace("'", "''"), languageIndex, categoryName);
        if (curScore == 0) {
            myDB.updateFirstTimeSeen(languageIndex, 1,
                    curLanguage.toUpperCase().replace(" ", "_"),
                    curWord.replace("'", "''"), categoryName);
        }
        else if (curScore == 2 || curScore == 3) {
            learningWords--;
            unknownWords++;
            myDB.updateSingleWordCount(categoryName, Integer.toString(learningWords),
                    "LEARNING_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateSingleWordCount(categoryName, Integer.toString(unknownWords),
                    "UNKNOWN_WORDS_" + curLanguage.toUpperCase().replace(" ", "_"),
                    languageIndex);
            myDB.updateFirstTimeSeen(languageIndex, 1,
                    curLanguage.toUpperCase().replace(" ", "_"),
                    curWord.replace("'", "''"), categoryName);
        }
        String unknownString = Integer.toString(unknownWords);
        String knownString = Integer.toString(knownWords);
        String learningString = Integer.toString(learningWords);
        ((TextView) findViewById(R.id.unknownWordsNumber)).setText(unknownString);
        ((TextView) findViewById(R.id.knownWordsNumber)).setText(knownString);
        ((TextView) findViewById(R.id.learningWordsNumber)).setText(learningString);
        showNewWord();
        findViewById(R.id.rightAnswer).setVisibility(View.GONE);
        findViewById(R.id.wrongAnswer).setVisibility(View.GONE);
        myDB.close();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("frontOfCard", curLanguage);
        savedInstanceState.putString("backOfCard", backOfCard);
        savedInstanceState.putInt("front", front);
        savedInstanceState.putInt("back", back);
        savedInstanceState.putString("word", curWord);
        savedInstanceState.putString("flip", flip);
        savedInstanceState.putInt("nextInt", nextInt);
        savedInstanceState.putBoolean("cardFlipped", cardFlipped);
        savedInstanceState.putString("categoryName", categoryName);
        savedInstanceState.putInt("languageIndex", languageIndex);
        savedInstanceState.putStringArrayList("keysArray", keysArray);
        savedInstanceState.putInt("unknownWords", unknownWords);
        savedInstanceState.putInt("knownWord", knownWords);
        savedInstanceState.putInt("learningWords", learningWords);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
