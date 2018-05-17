package ogcio.vanuatulanguages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by whitmorg on 06/10/2017.
 *
 */

class CustomWordAdapter extends ArrayAdapter<FlashCardWord> {

    private ArrayList<FlashCardWord> wordList = new ArrayList<>();

    CustomWordAdapter(Context context, int textViewResourceId, ArrayList<FlashCardWord> objects) {
        super(context, textViewResourceId, objects);
        wordList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.word, null);
        TextView category = (TextView) v.findViewById(R.id.category);
        TextView unknownWordsNumber = (TextView) v.findViewById(R.id.unknownWordsNumber);
        TextView knownWordsNumber = (TextView) v.findViewById(R.id.knownWordsNumber);
        TextView learningWordsNumber = (TextView) v.findViewById(R.id.learningWordsNumber);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        category.setText(wordList.get(position).getWord());
        String word = wordList.get(position).getWord();
        if (wordList.get(position).getUnknownWords().equals("-1") &&
                wordList.get(position).getKnownWords().equals("-1") &&
                wordList.get(position).getLearningWords().equals("-1"))
                v.findViewById(R.id.layout).setVisibility(View.GONE);
        else if (wordList.get(position).getWord().equals("Starred Cards")) {
            v.findViewById(R.id.learningWords).setVisibility(View.VISIBLE);
            ((TextView) v.findViewById(R.id.learningWords)).setText("Starred Cards:");
            learningWordsNumber.setText(wordList.get(position).getUnknownWords());
            v.findViewById(R.id.unknownWords).setVisibility(View.INVISIBLE);
            v.findViewById(R.id.knownWords).setVisibility(View.INVISIBLE);
            unknownWordsNumber.setVisibility(View.INVISIBLE);
            knownWordsNumber.setVisibility(View.INVISIBLE);
        }
        else if (wordList.get(position).getWord().equals("Loading...")) {
            ((TextView) v.findViewById(R.id.learningWords)).setText("Loading...");
            v.findViewById(R.id.unknownWords).setVisibility(View.GONE);
            v.findViewById(R.id.knownWords).setVisibility(View.GONE);
            v.findViewById(R.id.learningWords).setVisibility(View.GONE);
            unknownWordsNumber.setVisibility(View.GONE);
            knownWordsNumber.setVisibility(View.GONE);
            learningWordsNumber.setVisibility(View.GONE);
        }
        else {
            unknownWordsNumber.setText(wordList.get(position).getUnknownWords());
            knownWordsNumber.setText(wordList.get(position).getKnownWords());
            learningWordsNumber.setText(wordList.get(position).getLearningWords());
            if (wordList.get(position).isStackComplete()) {
                imageView.setVisibility(View.VISIBLE);
                v.findViewById(R.id.layout).setVisibility(View.INVISIBLE);
//                unknownWords.setVisibility(View.GONE);
//                knownWords.setVisibility(View.GONE);
//                learningWords.setVisibility(View.GONE);
//                v.findViewById(R.id.unknownWords).setVisibility(View.GONE);
//                v.findViewById(R.id.knownWords).setVisibility(View.GONE);
//                v.findViewById(R.id.learningWords).setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.checkmark);
            } else {
                imageView.setVisibility(View.INVISIBLE);
                v.findViewById(R.id.layout).setVisibility(View.VISIBLE);
//                unknownWords.setVisibility(View.VISIBLE);
//                knownWords.setVisibility(View.VISIBLE);
//                learningWords.setVisibility(View.VISIBLE);
//                v.findViewById(R.id.unknownWords).setVisibility(View.VISIBLE);
//                v.findViewById(R.id.knownWords).setVisibility(View.VISIBLE);
//                v.findViewById(R.id.learningWords).setVisibility(View.VISIBLE);
            }
        }
//        imageView.setTag("true");
        return v;
    }

}
