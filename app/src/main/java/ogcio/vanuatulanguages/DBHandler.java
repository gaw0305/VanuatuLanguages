package ogcio.vanuatulanguages;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Grace Whitmore
 *
 */

class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Dictionary.db";
    private static final String TABLE_DICTIONARY = "DICTIONARY";
    private static final String TABLE_FLASH_CARDS = "FLASH_CARDS";
    private static final String TABLE_CATEGORIES = "CATEGORIES";
    private static final String TABLE_SETTINGS = "SETTINGS";
    private static final String KEY_ENGLISH = "ENGLISH";
    private static final String KEY_BISLAMA = "BISLAMA";
    private static final String KEY_EAST_AMBAE = "EAST_AMBAE";
    private static final String KEY_FRENCH = "FRENCH";
    private static final String KEY_FIRST_TIME_SEEN_ENGLISH = "FIRST_TIME_SEEN_ENGLISH";
    private static final String KEY_FIRST_TIME_SEEN_BISLAMA = "FIRST_TIME_SEEN_BISLAMA";
    private static final String KEY_FIRST_TIME_SEEN_EAST_AMBAE = "FIRST_TIME_SEEN_EAST_AMBAE";
    private static final String KEY_FIRST_TIME_SEEN_FRENCH = "FIRST_TIME_SEEN_FRENCH";
    private static final String KEY_STAR_ENGLISH = "STAR_ENGLISH";
    private static final String KEY_STAR_BISLAMA = "STAR_BISLAMA";
    private static final String KEY_STAR_EAST_AMBAE = "STAR_EAST_AMBAE";
    private static final String KEY_STAR_FRENCH = "STAR_FRENCH";
    private static final String KEY_CATEGORIES = "CATEGORIES";
    private static final String KEY_CATEGORY = "CATEGORY";
    private static final String KEY_KEEP_SCORE = "KEEP_SCORE";
    private static final String KEY_SCORE_TO_MATCH = "SCORE_TO_MATCH";
    private static final String KEY_EDITABLE = "EDITABLE";
    private static final String KEY_KNOWN_WORDS_ENGLISH = "KNOWN_WORDS_ENGLISH";
    private static final String KEY_KNOWN_WORDS_BISLAMA = "KNOWN_WORDS_BISLAMA";
    private static final String KEY_KNOWN_WORDS_EAST_AMBAE = "KNOWN_WORDS_EAST_AMBAE";
    private static final String KEY_KNOWN_WORDS_FRENCH = "KNOWN_WORDS_FRENCH";
    private static final String KEY_UNKNOWN_WORDS_ENGLISH = "UNKNOWN_WORDS_ENGLISH";
    private static final String KEY_UNKNOWN_WORDS_BISLAMA = "UNKNOWN_WORDS_BISLAMA";
    private static final String KEY_UNKNOWN_WORDS_EAST_AMBAE = "UNKNOWN_WORDS_EAST_AMBAE";
    private static final String KEY_UNKNOWN_WORDS_FRENCH = "UNKNOWN_WORDS_FRENCH";
    private static final String KEY_LEARNING_WORDS_ENGLISH = "LEARNING_WORDS_ENGLISH";
    private static final String KEY_LEARNING_WORDS_BISLAMA = "LEARNING_WORDS_BISLAMA";
    private static final String KEY_LEARNING_WORDS_EAST_AMBAE = "LEARNING_WORDS_EAST_AMBAE";
    private static final String KEY_LEARNING_WORDS_FRENCH = "LEARNING_WORDS_FRENCH";
    private static final String KEY_SHOW_STARRED_CARDS_DIALOG = "SHOW_STARRED_CARDS_DIALOG";
    private static final String KEY_SHOW_WHICH_LANGUAGES = "SHOW_WHICH_LANGUAGES";

    DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DICTIONARY_TABLE = "CREATE TABLE " + TABLE_DICTIONARY + "("
                + KEY_ENGLISH + " TEXT,"
                + KEY_BISLAMA + " TEXT,"
                + KEY_EAST_AMBAE + " TEXT,"
                + KEY_FRENCH + " TEXT,"
                + KEY_FIRST_TIME_SEEN_ENGLISH + " TEXT,"
                + KEY_FIRST_TIME_SEEN_BISLAMA + " TEXT,"
                + KEY_FIRST_TIME_SEEN_EAST_AMBAE + " TEXT,"
                + KEY_FIRST_TIME_SEEN_FRENCH + " TEXT,"
                + KEY_STAR_ENGLISH + " TEXT,"
                + KEY_STAR_BISLAMA + " TEXT,"
                + KEY_STAR_EAST_AMBAE + " TEXT,"
                + KEY_STAR_FRENCH + " TEXT,"
                + KEY_CATEGORY + " TEXT)";
        db.execSQL(CREATE_DICTIONARY_TABLE);
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_EDITABLE + " INT,"
                + KEY_SHOW_STARRED_CARDS_DIALOG + " INT,"
                + KEY_SHOW_WHICH_LANGUAGES + " TEXT)";
        db.execSQL(CREATE_SETTINGS_TABLE);
        String CREATE_CATEGORIES_TABLE = "CREATE TABLE " + TABLE_CATEGORIES + "("
                + KEY_CATEGORY + " TEXT,"
                + KEY_KNOWN_WORDS_ENGLISH + " TEXT,"
                + KEY_KNOWN_WORDS_BISLAMA + " TEXT,"
                + KEY_KNOWN_WORDS_EAST_AMBAE + " TEXT,"
                + KEY_KNOWN_WORDS_FRENCH + " TEXT,"
                + KEY_UNKNOWN_WORDS_ENGLISH + " TEXT,"
                + KEY_UNKNOWN_WORDS_BISLAMA + " TEXT,"
                + KEY_UNKNOWN_WORDS_EAST_AMBAE + " TEXT,"
                + KEY_UNKNOWN_WORDS_FRENCH + " TEXT,"
                + KEY_LEARNING_WORDS_ENGLISH + " TEXT,"
                + KEY_LEARNING_WORDS_BISLAMA + " TEXT,"
                + KEY_LEARNING_WORDS_EAST_AMBAE + " TEXT,"
                + KEY_LEARNING_WORDS_FRENCH + " TEXT)";

        db.execSQL(CREATE_CATEGORIES_TABLE);
        String CREATE_FLASH_CARDS_TABLE = "CREATE TABLE " + TABLE_FLASH_CARDS + "("
                + KEY_CATEGORIES + " TEXT,"
                + KEY_KEEP_SCORE + " TEXT,"
                + KEY_SCORE_TO_MATCH + " INT)";
        db.execSQL(CREATE_FLASH_CARDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DICTIONARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLASH_CARDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
        db.close();
    }

    int getNumLanguages() { return 4; }

    void insertCategory(String category) {
        StringBuilder numbers = new StringBuilder("0");
        for (int i = 0; i < getNumLanguages() - 2; i++)
            numbers.append(",").append("0");
        String numbersString = numbers.toString();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY, capitalize(category));
        contentValues.put(KEY_KNOWN_WORDS_ENGLISH, numbersString);
        contentValues.put(KEY_KNOWN_WORDS_BISLAMA, numbersString);
        contentValues.put(KEY_KNOWN_WORDS_EAST_AMBAE, numbersString);
        contentValues.put(KEY_KNOWN_WORDS_FRENCH, numbersString);
        contentValues.put(KEY_UNKNOWN_WORDS_ENGLISH, numbersString);
        contentValues.put(KEY_UNKNOWN_WORDS_BISLAMA, numbersString);
        contentValues.put(KEY_UNKNOWN_WORDS_EAST_AMBAE, numbersString);
        contentValues.put(KEY_UNKNOWN_WORDS_FRENCH, numbersString);
        contentValues.put(KEY_LEARNING_WORDS_ENGLISH, numbersString);
        contentValues.put(KEY_LEARNING_WORDS_BISLAMA, numbersString);
        contentValues.put(KEY_LEARNING_WORDS_EAST_AMBAE, numbersString);
        contentValues.put(KEY_LEARNING_WORDS_FRENCH, numbersString);
        db.insert(TABLE_CATEGORIES, null, contentValues);
        db.close();
    }

    void insertWord(ArrayList<String> words) {
        StringBuilder numbers = new StringBuilder("0");
        for (int i = 0; i < getNumLanguages() - 2; i++)
            numbers.append(",").append("0");
        String numbersString = numbers.toString();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ENGLISH, capitalize(words.get(0)));
        contentValues.put(KEY_BISLAMA, capitalize(words.get(1)));
        contentValues.put(KEY_EAST_AMBAE, capitalize(words.get(2)));
        contentValues.put(KEY_FRENCH, capitalize(words.get(3)));
        contentValues.put(KEY_FIRST_TIME_SEEN_ENGLISH, numbersString);
        contentValues.put(KEY_FIRST_TIME_SEEN_BISLAMA, numbersString);
        contentValues.put(KEY_FIRST_TIME_SEEN_EAST_AMBAE, numbersString);
        contentValues.put(KEY_FIRST_TIME_SEEN_FRENCH, numbersString);
        contentValues.put(KEY_STAR_ENGLISH, numbersString);
        contentValues.put(KEY_STAR_BISLAMA, numbersString);
        contentValues.put(KEY_STAR_EAST_AMBAE, numbersString);
        contentValues.put(KEY_STAR_FRENCH, numbersString);
        contentValues.put(KEY_CATEGORY, capitalize(words.get(words.size()-1)));
        db.insert(TABLE_DICTIONARY, null, contentValues);
        db.close();
    }

    ArrayList<String> getCategories() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CATEGORY + " from " + TABLE_CATEGORIES, null);
        ArrayList<String> categories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        Collections.sort(categories, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        cursor.close();
        db.close();
        return categories;
    }

    String getWordsCount(String category, String languageKey) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + languageKey + " from " + TABLE_CATEGORIES
                + " WHERE " + KEY_CATEGORY + "='" + category + "'", null);
        cursor.moveToFirst();
        String answer = cursor.getString(0);
        cursor.close();
        db.close();
        return answer;
    }

    String getSingleWordCount(String category, String languageKey, int languageInt) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + languageKey + " from " + TABLE_CATEGORIES
                + " WHERE " + KEY_CATEGORY + "='" + category + "'", null);
        cursor.moveToFirst();
        String answer = cursor.getString(0).split(",")[languageInt];
        cursor.close();
        db.close();
        return answer;
    }

    ArrayList<String> getCategoryWordsForFlashCards(String category, String frontOfCard, String backOfCard) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_DICTIONARY + " WHERE "
                + KEY_CATEGORY + "='" + category + "'", null);
        int languageInt = getLanguageNames().indexOf(frontOfCard.toUpperCase());
        int translateInt = getLanguageNames().indexOf(backOfCard.toUpperCase());
        ArrayList<String> words = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(languageInt);
                String translateWord = cursor.getString(translateInt);
                if (!word.equals("") && !translateWord.equals("") && !words.contains(word)) {
                    words.add(word);
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return words;
    }

    ArrayList<String> getNumWordsInCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_DICTIONARY + " WHERE " + KEY_CATEGORY
                + "='" + category + "'", null);

        ArrayList<String> languages = getLanguageNames();
        ArrayList<String> nums = new ArrayList<>();
        String curNums = "";
        for (int i = 0; i < languages.size(); i++) {
            for (int j = 0; j < languages.size(); j++) {
                if (i != j) {
                    String newNums = Integer.toString(getCategoryWordsForFlashCards(category, languages.get(i), languages.get(j)).size());
                    if (curNums.equals("")) curNums += newNums;
                    else curNums += "," + newNums;
                }
            }
            nums.add(curNums);
            curNums = "";
        }

//        ArrayList<String> nums = new ArrayList<>();
//        ArrayList<ArrayList<String>> wordLists = new ArrayList<>();
//        for (int i = 0; i < getNumLanguages(); i++) wordLists.add(new ArrayList<String>());
//        if (cursor.moveToFirst()) {
//            do {
//                for (int i = 0; i < getNumLanguages(); i++) {
//                    String word = cursor.getString(i);
//                    if (!word.equals("")) {
//                        if (getLanguageNames().indexOf(frontOfCard.toUpperCase()) == i && !wordLists.get(i).contains(word))
//                            wordLists.get(i).add(word);
//                        else if (getLanguageNames().indexOf(frontOfCard.toUpperCase()) != i)
//                            wordLists.get(i).add(word);
//                    }
//                }
//            } while (cursor.moveToNext());
//        }
//        StringBuilder numSublist;
//        for (int i = 0; i < wordLists.size(); i++) {
//            numSublist = new StringBuilder();
//            for (int j = 0; j < wordLists.size(); j++) {
//                if (i == j) j++;
//                if (j != wordLists.size()) {
//                    if (wordLists.get(i).size() < wordLists.get(j).size()) {
//                        if (numSublist.length() == 0)
//                            numSublist.append(Integer.toString(wordLists.get(i).size()));
//                        else
//                            numSublist.append(",").append(Integer.toString(wordLists.get(i).size()));
//                    }
//                    else {
//                        if (numSublist.length() == 0)
//                            numSublist.append(Integer.toString(wordLists.get(j).size()));
//                        else
//                            numSublist.append(",").append(Integer.toString(wordLists.get(j).size()));
//                    }
//                }
//            }
//            nums.add(numSublist.toString());
//        }
        cursor.close();
        db.close();
        return nums;

    }

    void updateWordsCount(String category, String list, String languageKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!category.equals(""))
            db.execSQL("UPDATE " + TABLE_CATEGORIES + " SET " + languageKey + "='" + list
                    + "' WHERE " + KEY_CATEGORY + "='" + category + "'");
        else
            db.execSQL("UPDATE " + TABLE_CATEGORIES + " SET " + languageKey + "='" + list + "'");
    }

    void updateSingleWordCount(String category, String item, String languageKey, int languageInt) {
        String[] wordsCount = getWordsCount(category, languageKey).split(",");
        wordsCount[languageInt] = item;
        StringBuilder updatedWordsCount = new StringBuilder();
        updatedWordsCount.append(wordsCount[0]);
        for (int i = 1; i < wordsCount.length; i++)
            updatedWordsCount.append(",").append(wordsCount[i]);
        SQLiteDatabase db = this.getWritableDatabase();
        if (!category.equals(""))
            db.execSQL("UPDATE " + TABLE_CATEGORIES + " SET " + languageKey + "='" + updatedWordsCount
                    + "' WHERE " + KEY_CATEGORY + "='" + category + "'");
        else
            db.execSQL("UPDATE " + TABLE_CATEGORIES + " SET " + languageKey + "='" + updatedWordsCount + "'");
        db.close();
    }

    ArrayList<String> getLanguageNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_DICTIONARY + " WHERE "
                + KEY_ENGLISH + "= 'English'", null);
        cursor.moveToFirst();
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < getNumLanguages(); i++) {
            String key = cursor.getString(i).toUpperCase();
            if (key.contains(" ")) key = key.replace(" ", "_");
            keys.add(key);
        }
        cursor.close();
        return keys;
    }

    ArrayList<String> getWordsByCategory(String language, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + language + " from " + TABLE_DICTIONARY
                + " WHERE " + KEY_CATEGORY + "='" + category + "'", null);
        cursor.moveToFirst();
        ArrayList<String> words = new ArrayList<>();
        do {
            if (!cursor.getString(0).equals("")) words.add(cursor.getString(0));
        } while (cursor.moveToNext());
        cursor.close();
        db.close();
        return words;
    }

    void initializeSettings() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        StringBuilder values = new StringBuilder();
        values.append("true");
        for (int i = 0; i < getNumLanguages()-1; i++) values.append(",true");
        contentValues.put(KEY_EDITABLE, 0);
        contentValues.put(KEY_SHOW_STARRED_CARDS_DIALOG, 1);
        contentValues.put(KEY_SHOW_WHICH_LANGUAGES, values.toString());
        db.insert(TABLE_SETTINGS, null, contentValues);
        db.close();
    }

    String getWhichLanguagesString() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_SHOW_WHICH_LANGUAGES + " from " + TABLE_SETTINGS, null);
        cursor.moveToFirst();
        String answer = cursor.getString(0);
        cursor.close();
        db.close();
        return answer;
    }

    void updateShowStarredCardsDialog() {
        SQLiteDatabase db = this.getWritableDatabase();
        int zero = 0;
        db.execSQL("UPDATE " + TABLE_SETTINGS + " SET " + KEY_SHOW_STARRED_CARDS_DIALOG + "=" + zero);
        db.close();
    }

    boolean showStarredCardsDialog() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_SHOW_STARRED_CARDS_DIALOG + " from "
            + TABLE_SETTINGS, null);
        cursor.moveToFirst();
        int answer = cursor.getInt(0);
        cursor.close();
        db.close();
        return (answer == 1);
    }

    void updateFirstTimeSeen(int languageInt, int score, String language, String curWord, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT FIRST_TIME_SEEN_" + language + " FROM " + TABLE_DICTIONARY
                + " WHERE " + language + "='" + curWord + "' and " + KEY_CATEGORY + "='" + category + "'", null);
        cursor.moveToFirst();
        String[] scores = cursor.getString(0).split(",");
//        int curScore = Integer.parseInt(scores[languageInt]);
        scores[languageInt] = Integer.toString(score);
        StringBuilder updatedScores = new StringBuilder();
        updatedScores.append(scores[0]);
        for (int i = 1; i < scores.length; i++)
            updatedScores.append(",").append(scores[i]);
        cursor.close();
        db.execSQL("UPDATE " + TABLE_DICTIONARY + " SET FIRST_TIME_SEEN_" + language + "='" + updatedScores
                + "' WHERE " + language + "='" + curWord + "' and " + KEY_CATEGORY + "='" + category + "'");
        db.close();
    }

    void updateStar(int updatedStar, int starInt, String word, String language, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT STAR_" + language + " from " + TABLE_DICTIONARY + " WHERE "
            + language + "='" + word + "' and " + KEY_CATEGORY + "='" + category + "'", null);
        cursor.moveToFirst();
        String[] starList = cursor.getString(0).split(",");
        starList[starInt] = Integer.toString(updatedStar);
        StringBuilder stars = new StringBuilder();
        stars.append(starList[0]);
        for (int i = 1; i < starList.length; i++)
            stars.append(",").append(starList[i]);
        db.execSQL("UPDATE " + TABLE_DICTIONARY + " SET STAR_" + language + "='" + stars + "' WHERE "
            + language + "='" + word + "' and " + KEY_CATEGORY + "='" + category + "'");
        cursor.close();
        db.close();
    }

    int isFirstTimeSeen(String language, String curWord, int languageInt, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (category.equals("Starred Cards")) return 0;
        Cursor cursor = db.rawQuery("SELECT FIRST_TIME_SEEN_" + language + " FROM " + TABLE_DICTIONARY
                + " WHERE " + language + "='" + curWord + "' and " + KEY_CATEGORY + "='" + category + "'", null);
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DICTIONARY + " WHERE " + language + "='"
//                + curWord + "' and " + KEY_CATEGORY + "='" + category + "'", null);
        cursor.moveToFirst();
        int curScore = Integer.parseInt(cursor.getString(0).split(",")[languageInt]);
        cursor.close();
        db.close();
        return curScore;
    }

    boolean isEmpty(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean empty = true;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        if (cursor != null && cursor.moveToFirst())
            empty = (cursor.getInt(0) == 0);
        if (cursor != null) cursor.close();
        db.close();
        return empty;
    }

    boolean isStarred(String word, String language, int languageInt, String star, boolean starred) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (starred)
            cursor = db.rawQuery("SELECT " + star + " from " + TABLE_DICTIONARY + " WHERE "
                    + language + "='" + word.split(",")[0] + "'", null);
        else
            cursor = db.rawQuery("SELECT " + star + " from " + TABLE_DICTIONARY + " WHERE "
                    + language + "='" + word + "'", null);
        cursor.moveToFirst();
        String something = cursor.getString(0);
        boolean answer = cursor.getString(0).split(",")[languageInt].equals("1");
        cursor.close();
        db.close();
        return answer;
    }

    ArrayList<String> getStarredCards(int starInt, int languageInt) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_DICTIONARY, null);
        ArrayList<String> starredCards = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(languageInt);
                if (word.equals("Fight")) {
                    String something = "hi";
                }
                String category = cursor.getString(cursor.getColumnCount() - 1);
                if (cursor.getString(languageInt + getNumLanguages() * 2).split(",")[starInt].equals("1")
                        && !starredCards.contains(word + "," + category))
                    starredCards.add(word + "," + category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return starredCards;
    }

    String getStarredCardCategory(String word, String language, int starInt, int languageInt) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * from " + TABLE_DICTIONARY + " WHERE "
                + language + "='" + word + "'", null);
        String category = "";
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getString(languageInt + (2 * getNumLanguages())).split(",")[starInt].equals("1"))
                    category = cursor.getString(cursor.getColumnCount() - 1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return category;
    }

    ArrayList<String> getAllLanguageOneWords(String languageKey, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (category.equals("All")) cursor = db.rawQuery("SELECT " + languageKey + " from "
            + TABLE_DICTIONARY, null);
        else cursor = db.rawQuery("SELECT " + languageKey + " from " + TABLE_DICTIONARY + " WHERE "
                + KEY_CATEGORY + "='" + capitalize(category) + "'", null);
        ArrayList<String> languageOneWords = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                languageOneWords.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.sort(languageOneWords, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        String prevKey = "";
        ArrayList<String> keySetDupesRemoved = new ArrayList<>();
        for (String key : languageOneWords) {
            if (!key.equals(prevKey))
                keySetDupesRemoved.add(key);
            prevKey = key;
        }
        return keySetDupesRemoved;
    }

    ArrayList<String> getAllFlashCardWords(int languageOne, int languageTwo, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        if (category.equals("all")) cursor = db.rawQuery("SELECT * from " + TABLE_DICTIONARY,
                null);
        else cursor = db.rawQuery("SELECT * from " + TABLE_DICTIONARY + " WHERE "
                + KEY_CATEGORY + "='" + capitalize(category) + "'", null);
        ArrayList<String> flashCardWords = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if (!cursor.getString(languageOne).equals("") && !cursor.getString(languageTwo).equals("")
                        && !flashCardWords.contains(cursor.getString(languageOne)))
                    flashCardWords.add(cursor.getString(languageOne));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return flashCardWords;
    }

    ArrayList<String> getAllLanguageOneWordsWithDupes() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_ENGLISH + " from "
                + TABLE_DICTIONARY, null);
        ArrayList<String> languageOneWords = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                languageOneWords.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Collections.sort(languageOneWords, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        return languageOneWords;
    }

    String getTranslationByCategory(String word, String translationKey, String languageKey,
                                    String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        if (category.equals("all"))
            cursor = db.rawQuery("SELECT " + translationKey + " from " + TABLE_DICTIONARY
                    + " WHERE " + languageKey + "='" + word + "'", null);
        else
            cursor = db.rawQuery("SELECT " + translationKey + " from " + TABLE_DICTIONARY
                + " WHERE " + languageKey + "='" + word + "' and " + KEY_CATEGORY + "='"
                + category + "'", null);
        StringBuilder translation = new StringBuilder();
        translation.append("Not in dictionary");
        if (cursor.moveToFirst()) {
            do {
                if (!cursor.getString(0).equals("")) {
                    if (translation.toString().equals("Not in dictionary")) {
                        translation = new StringBuilder();
                        translation.append(cursor.getString(0));
                    }
                    else {
                        String[] translations = translation.toString().split(", ");
                        boolean existingTranslation = false;
                        for (String item : translations) {
                            if (cursor.getString(0).equals(item))
                                existingTranslation = true;
                        }
                        if (!existingTranslation)
                            translation.append(", ").append(cursor.getString(0));
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return translation.toString();
    }

    int numCategoriesForWord(String word, String languageKey) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CATEGORY + " from " + TABLE_DICTIONARY + " WHERE "
            + languageKey + "='" + word + "'", null);
        int num = 0;
        ArrayList<String> categories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if (!categories.contains(cursor.getString(0))) {
                    num++;
                    categories.add(cursor.getString(0));
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        return num;
    }

    String getCategoryWhereWordHasMultipleCategories(String word, String languageKey, int num) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CATEGORY + " from " + TABLE_DICTIONARY
                + " WHERE " + languageKey + "='" + word + "'", null);
        String category = "";
        if (cursor.moveToFirst()) {
            do {
                num--;
                category = cursor.getString(0);
            } while (num >= 0 && cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return category;
    }

    private String capitalize(final String line) {
        if (line.equals("")) return "";
        else return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    int getLanguageInt(String languageOne, String languageTwo) {
        if (languageOne.toUpperCase().equals("ENGLISH") && languageTwo.toUpperCase().equals("BISLAMA")) return 0;
        else if (languageOne.equals("ENGLISH") && languageTwo.equals("EAST_AMBAE")) return 1;
        else if (languageOne.equals("ENGLISH") && languageTwo.equals("FRENCH")) return 2;

        else if (languageOne.equals("BISLAMA") && languageTwo.equals("ENGLISH")) return 0;
        else if (languageOne.equals("BISLAMA") && languageTwo.equals("EAST_AMBAE")) return 1;
        else if (languageOne.equals("BISLAMA") && languageTwo.equals("FRENCH")) return 2;

        else if (languageOne.equals("EAST_AMBAE") && languageTwo.equals("ENGLISH")) return 0;
        else if (languageOne.equals("EAST_AMBAE") && languageTwo.equals("BISLAMA")) return 1;
        else if (languageOne.equals("EAST_AMBAE") && languageTwo.equals("FRENCH")) return 2;

        else if (languageOne.equals("FRENCH") && languageTwo.equals("ENGLISH")) return 0;
        else if (languageOne.equals("FRENCH") && languageTwo.equals("BISLAMA")) return 1;
        else if (languageOne.equals("FRENCH") && languageTwo.equals("EAST_AMBAE")) return 2;
        return -1;
    }

    String getCategory(String word, String languageKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + KEY_CATEGORY + " from " + TABLE_DICTIONARY
            + " WHERE " + languageKey + "='" + word + "'", null);
        cursor.moveToFirst();
        String category = "";
        try {
            category = cursor.getString(0);
        } catch (CursorIndexOutOfBoundsException e) { e.printStackTrace(); }
        cursor.close();
        db.close();
        return category;
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

//    String getTableAsString(String category) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String tableString = String.format("Table %s:\n", TABLE_DICTIONARY);
//        Cursor allRows  = db.rawQuery("SELECT * FROM " + TABLE_DICTIONARY, null);
////        + " WHERE "
////                + KEY_CATEGORY + "='" + category + "'", null);
//        if (allRows.moveToFirst() ){
//            String[] columnNames = allRows.getColumnNames();
//            do {
//                for (String name: columnNames) {
//                    tableString += String.format("%s: %s\n", name,
//                            allRows.getString(allRows.getColumnIndex(name)));
//                }
//                tableString += "\n";
//
//            } while (allRows.moveToNext());
//        }
//        allRows.close();
//        db.close();
//        allRows.close();
//        return tableString;
//    }
}