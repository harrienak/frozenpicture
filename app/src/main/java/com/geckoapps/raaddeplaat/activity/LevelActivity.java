package com.geckoapps.raaddeplaat.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.geckoapps.raaddeplaat.R;
import com.geckoapps.raaddeplaat.model.Block;
import com.geckoapps.raaddeplaat.model.Level;
import com.geckoapps.raaddeplaat.model.Toolbar;
import com.geckoapps.raaddeplaat.utils.DatabaseHelper;
import com.geckoapps.raaddeplaat.utils.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sjoerd on 15-1-2016.
 */
public class LevelActivity extends Activity {
    @Bind(R.id.level_image)
    ImageView levelImageView;
    @Bind(R.id.level_block_container)
    LinearLayout blockContainer;
    @Bind(R.id.level_toolbar)
    Toolbar toolbar;
    @Bind(R.id.level_letters_container)LinearLayout lettersContainer;
    @Bind(R.id.level_next_container)LinearLayout nextContainer;
    @Bind(R.id.level_button_container)LinearLayout buttonContainer;

    private Level currentLevel;
    private ArrayList<Button> letters, woord, lettersInWord;
    private Typeface typeface;
    private DatabaseHelper db;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);

        ButterKnife.bind(this);

        db = new DatabaseHelper(this);
        try {
            db.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadAds();
        setViews();
        initLevel();
    }

    private void loadAds() {
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void initLevel() {
        getLevel();
        setLevel();
        currentLevel.showBlocks();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shuffleBlocks();
                singleAnimationLetter(0);
            }
        }, 1100);
    }

    @OnClick(R.id.button_axe)
    public void removeBlocks() {
        if (currentLevel.isHasShuffledBlocks()) {
            if (toolbar.spendCoins(Utils.PRIZE_HACK)) {
                currentLevel.removeBlocksForTurn();
            } else {
                showNotEnoughMoneyDialog();
            }
        } else {
            showDialog("ga nou shufflen", "je moet eerst shufflen daarna kun je hakken", "oke", "oke");
        }
    }

    @OnClick(R.id.button_shuffle)
    public void shuffleBlocksCheck() {
        if (toolbar.spendCoins(Utils.PRIZE_SHUFFLE)) {
            shuffleBlocks();
        } else {
            showNotEnoughMoneyDialog();
        }
    }

    private void shuffleBlocks() {
        if (currentLevel.shuffleIsPossible()) {
            currentLevel.shuffleBlocks();
        } else {
            showDialog("kan niet meer", "verrekte mongol je kan niet meer shufflen, ga toch jumpen", "Jumpen", "cancel");
        }
    }

    @OnClick(R.id.button_share)
    public void share() {
        toolbar.addCoins(100);
    }

    @OnClick(R.id.level_next_button)
    public void nextLevel(){
        toolbar.addLevel(1);
        initLevel();
        setLevel();
    }

    public void setNextContainerLayout(){
        nextContainer.setVisibility(View.VISIBLE);
        lettersContainer.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.INVISIBLE);
    }

    public void setLetterContainerLayout(){
        nextContainer.setVisibility(View.GONE);
        lettersContainer.setVisibility(View.VISIBLE);
        buttonContainer.setVisibility(View.VISIBLE);
    }


    boolean canRemove = true;

    @OnClick(R.id.buttonBom)
    public void removeLetters() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_removeletters))
                .setMessage(getString(R.string.text_removeletters) + " " + Utils.PRIZE_BOMB + " " + getString(R.string.coins_q))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(!canRemove){
                            Toast.makeText(LevelActivity.this, "you cant do this twice for 1 level", Toast.LENGTH_SHORT).show();
                        }
                        if (toolbar.spendCoins(Utils.PRIZE_BOMB)) {
                            canRemove = false;
                            dialog.dismiss();
                            removeLettersPeform();
                        } else {
                            dialog.dismiss();
                            showNotEnoughMoneyDialog();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.icon_remove)
                .show();
    }

    private void removeLettersPeform() {
        int aantalWeg = 0;
        canRemove = false;
        if (currentLevel.correct_letters.length == 15) {
            aantalWeg = 1;
        } else {
            aantalWeg = (int) (letters.size() - currentLevel.correct_letters.length) / 2;
            if (aantalWeg > 5)
                aantalWeg = 5;
            else if (aantalWeg < 1)
                aantalWeg = 1;
        }
        for (int i = 0; i < aantalWeg; i++) {
            for (int j = 0; j < letters.size(); j++) {
                if (currentLevel.getRemoveHelp().get(i).equalsIgnoreCase((String) letters.get(j).getText())) {
                    letters.get(j).setVisibility(Button.INVISIBLE);
                    break;
                }
            }
        }
    }

    @OnClick(R.id.buttonLetterHint)
    public void hintLetter() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_hintletter))
                .setMessage(getString(R.string.text_hintletter) + " " + Utils.PRIZE_HINT + " " + getString(R.string.coins_q))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!currentLevel.lettersAreClickable()) {
                            //bovenbalk.showToastMsg(getString(R.string.toast_hint));
                        } else if (toolbar.spendCoins(Utils.PRIZE_HINT)) {
                            hintLetterPeform();
                        } else {
                            dialog.dismiss();
                            showNotEnoughMoneyDialog();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.icon_hint)
                .show();
    }

    private void hintLetterPeform() {
        Random r = new Random();
        boolean notFound = true;
        int nr = -1;
        while (notFound) {
            nr = r.nextInt(currentLevel.woord_letters.length);
            if (currentLevel.woord_letters[nr].equals("")) {
                notFound = false;
            }
        }
        lettersInWord.get(nr).setText(currentLevel.correct_letters[nr].toUpperCase(Locale.US));
        lettersInWord.get(nr).setTextColor(getResources().getColor(R.color.color_greencorrect));
        lettersInWord.get(nr).setBackgroundResource(R.drawable.letter_button);
        lettersInWord.get(nr).setClickable(false);
        for (int i = 0; i < letters.size(); i++) {
            if (currentLevel.correct_letters[nr].equalsIgnoreCase((String) letters.get(i).getText())) {
                letters.get(i).setVisibility(Button.INVISIBLE);
                currentLevel.woord_letters[nr] = (String) letters.get(i).getText();
                currentLevel.woord_positie[nr] = i;
                break;
            }
        }
        if (!currentLevel.lettersAreClickable()) {
            checkLevel();
        }
    }

    private void showNotEnoughMoneyDialog() {
        showDialog("Niet genoeg doekoe", "GA kopen met je moker kop!", "GO TO THE SHOPPAA", "WEG HIER");
    }

    private void showDialog(String title, String message, String possitiveButton, String negativeButton) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(possitiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setLevel() {
        if (currentLevel != null) {
            //clear image view
            setLetterContainerLayout();

            //set block
            blockContainer.removeAllViews();
            LinearLayout horizontal = new LinearLayout(this);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1, 1);
            LinearLayout.LayoutParams lpBlock = new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT, 1);

            Block[][] blocks = new Block[currentLevel.getTiles()][currentLevel.getTiles()];

            for (int i = 0; i < currentLevel.getTiles(); i++) {
                for (int j = 0; j < currentLevel.getTiles(); j++) {
                    if (j == 0) {
                        horizontal = new LinearLayout(this);
                        horizontal.setOrientation(LinearLayout.HORIZONTAL);
                        horizontal.setLayoutParams(lp);
                        blockContainer.addView(horizontal);
                    }
                    Block b = new Block(this);
                    b.setLayoutParams(lpBlock);
                    b.setVisibility(View.INVISIBLE);
                    blocks[i][j] = b;
                    horizontal.addView(b);
                }
            }
            currentLevel.setBlocks(blocks);

            for (Button l : letters) {
                l.setClickable(true);
            }
            for (int i = 0; i < letters.size(); i++) {
                letters.get(i).setVisibility(Button.INVISIBLE);
                letters.get(i).setText(currentLevel.getLetters().get(i).toUpperCase(Locale.US));
            }

            for (Button b : woord) {
                b.setVisibility(Button.GONE);
                b.setText("");
                LinearLayout.LayoutParams params = (LayoutParams) b.getLayoutParams();
                params.setMargins(5, 0, 5, 0);
                b.setLayoutParams(params);
            }
            int currenti = 0;
            lettersInWord = new ArrayList<Button>();
            int currentPositie = 0;

            //1 word
            if (currentLevel.getWoorden().size() == 1 && currentLevel.getAnswer().length() <= 12) {
                for (int i = 0; i < woord.size(); i++) {
                    if (i < currentLevel.getAnswer().length()) {
                        woord.get(i).setVisibility(Button.VISIBLE);
                        lettersInWord.add(woord.get(i));
                        currentLevel.origin_positie[i] = currentPositie;
                        currentPositie++;
                    } else {
                        woord.get(i).setVisibility(Button.GONE);
                    }
                }
            } else if (currentLevel.getWoorden().size() == 1) {
                for (int i = 0; i < woord.size(); i++) {
                    if (i < currentLevel.getAnswer().length()) {
                        woord.get(i).setVisibility(Button.VISIBLE);
                        lettersInWord.add(woord.get(i));
                        currentLevel.origin_positie[i] = currentPositie;
                        currentPositie++;
                    } else {
                        woord.get(i).setVisibility(Button.GONE);
                    }
                }
            }
            //2 words
            else if (currentLevel.getWoorden().size() == 2) {
                if ((currentLevel.getWoorden().get(0).length() + currentLevel.getWoorden().get(1).length()) <= 12) {
                    for (int i = 0; i < currentLevel.getWoorden().get(0).length(); i++) {
                        woord.get(i).setVisibility(Button.VISIBLE);
                        lettersInWord.add(woord.get(i));
                        currentLevel.origin_positie[i] = currentPositie;
                        currentPositie++;
                        if (i == currentLevel.getWoorden().get(0).length() - 1) {
                            LinearLayout.LayoutParams params = (LayoutParams) woord.get(i).getLayoutParams();
                            params.setMargins(5, 0, 20, 0);
                            woord.get(i).setLayoutParams(params);
                        }
                    }
                    currenti = currentLevel.getWoorden().get(0).length();
                    for (int i = currenti; i < (currenti + currentLevel.getWoorden().get(1).length()); i++) {
                        woord.get(i).setVisibility(Button.VISIBLE);
                        lettersInWord.add(woord.get(i));
                        currentLevel.origin_positie[i] = currentPositie;
                        currentPositie++;
                    }
                    currenti = currenti + currentLevel.getWoorden().get(1).length();
                    for (int i = currenti; i < 12; i++) {
                        woord.get(i).setVisibility(Button.GONE);
                    }
                } else {
                    for (int i = 0; i < 12; i++) {
                        if (i < currentLevel.getWoorden().get(0).length()) {
                            woord.get(i).setVisibility(Button.VISIBLE);
                            lettersInWord.add(woord.get(i));
                            currentLevel.origin_positie[i] = currentPositie;
                            currentPositie++;
                        } else
                            woord.get(i).setVisibility(Button.GONE);
                    }
                    for (int i = 12; i < 12; i++) {
                        if (i < (12 + currentLevel.getWoorden().get(1).length())) {
                            lettersInWord.add(woord.get(i));
                            woord.get(i).setVisibility(Button.VISIBLE);
                            currentLevel.origin_positie[i] = currentPositie;
                            currentPositie++;
                        } else
                            woord.get(i).setVisibility(Button.GONE);
                    }
                }
            }

            //3 words
            else if (currentLevel.getWoorden().size() == 3) {
                for (int i = 0; i < currentLevel.getWoorden().get(0).length(); i++) {
                    woord.get(i).setVisibility(Button.VISIBLE);
                    currentLevel.origin_positie[i] = currentPositie;
                    currentPositie++;
                    lettersInWord.add(woord.get(i));
                    if (i == currentLevel.getWoorden().get(0).length() - 1) {
                        LinearLayout.LayoutParams params = (LayoutParams) woord.get(i).getLayoutParams();
                        params.setMargins(5, 0, 20, 0);
                        woord.get(i).setLayoutParams(params);
                    }
                }
                currenti = currentLevel.getWoorden().get(0).length();
                if ((currentLevel.getWoorden().get(0).length() + currentLevel.getWoorden().get(1).length()) <= 12) {
                    for (int i = currenti; i < (currenti + currentLevel.getWoorden().get(1).length()); i++) {
                        woord.get(i).setVisibility(Button.VISIBLE);
                        lettersInWord.add(woord.get(i));
                        currentLevel.origin_positie[i] = currentPositie;
                        currentPositie++;
                    }
                    currenti = currenti + currentLevel.getWoorden().get(1).length();
                }
                for (int i = currenti; i < 12; i++) {
                    woord.get(i).setVisibility(Button.GONE);
                }
                for (int i = 12; i < woord.size(); i++) {
                    if (i < (12 + currentLevel.getWoorden().get(2).length())) {
                        woord.get(i).setVisibility(Button.VISIBLE);
                        lettersInWord.add(woord.get(i));
                        currentLevel.origin_positie[i] = currentPositie;
                        currentPositie++;
                    } else {
                        woord.get(i).setVisibility(Button.GONE);
                    }
                }
            }

            for (Button l : lettersInWord) {
                l.setClickable(true);
                l.setTextColor(Color.WHITE);
                l.setBackgroundResource(R.drawable.btn_letter_empty);
            }
        }
    }

    private void getLevel() {
        db.openDataBase();
        currentLevel = db.getLevel(Utils.getSharedPref(this, Utils.SHARED_LEVEL), this, levelImageView, toolbar);
        db.close();
    }

    private void setViews() {
        typeface = Typeface.createFromAsset(getAssets(),
                "font/PAYTONEONE.TTF");

        letters = new ArrayList<>();
        letters.add((Button) findViewById(R.id.buttonLetter0));
        letters.add((Button) findViewById(R.id.buttonLetter1));
        letters.add((Button) findViewById(R.id.buttonLetter2));
        letters.add((Button) findViewById(R.id.buttonLetter3));
        letters.add((Button) findViewById(R.id.buttonLetter4));
        letters.add((Button) findViewById(R.id.buttonLetter5));
        letters.add((Button) findViewById(R.id.buttonLetter6));
        letters.add((Button) findViewById(R.id.buttonLetter7));
        //letters.add((Button) findViewById(R.id.buttonLetter8));
        letters.add((Button) findViewById(R.id.buttonLetter9));
        letters.add((Button) findViewById(R.id.buttonLetter10));
        letters.add((Button) findViewById(R.id.buttonLetter11));
        letters.add((Button) findViewById(R.id.buttonLetter12));
        letters.add((Button) findViewById(R.id.buttonLetter13));
        letters.add((Button) findViewById(R.id.buttonLetter14));
        letters.add((Button) findViewById(R.id.buttonLetter15));
        letters.add((Button) findViewById(R.id.buttonLetter16));
        //letters.add((Button) findViewById(R.id.buttonLetter17));

        woord = new ArrayList<>();
        woord.add((Button) findViewById(R.id.buttonWoord0));
        woord.add((Button) findViewById(R.id.buttonWoord1));
        woord.add((Button) findViewById(R.id.buttonWoord2));
        woord.add((Button) findViewById(R.id.buttonWoord3));
        woord.add((Button) findViewById(R.id.buttonWoord4));
        woord.add((Button) findViewById(R.id.buttonWoord5));
        woord.add((Button) findViewById(R.id.buttonWoord6));
        woord.add((Button) findViewById(R.id.buttonWoord7));
        woord.add((Button) findViewById(R.id.buttonWoord8));
        woord.add((Button) findViewById(R.id.buttonWoord9));
        woord.add((Button) findViewById(R.id.buttonWoord10));
        woord.add((Button) findViewById(R.id.buttonWoord11));
        /*woord.add((Button) findViewById(R.id.buttonWoord12));
        woord.add((Button) findViewById(R.id.buttonWoord13));
        woord.add((Button) findViewById(R.id.buttonWoord14));
        woord.add((Button) findViewById(R.id.buttonWoord15));
        woord.add((Button) findViewById(R.id.buttonWoord16));
        woord.add((Button) findViewById(R.id.buttonWoord17));
        woord.add((Button) findViewById(R.id.buttonWoord18));
        woord.add((Button) findViewById(R.id.buttonWoord19));*/

        for (int i = 0; i < letters.size(); i++) {
            final int i2 = i;
            letters.get(i).setTypeface(typeface);
            letters.get(i).setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {

                    onLetterClick(i2);
                }
            });
        }
        for (int i = 0; i < woord.size(); i++) {
            final int i2 = i;
            woord.get(i).setTypeface(typeface);
            woord.get(i).setOnClickListener(new View.OnClickListener() {
                public void onClick(View paramAnonymousView) {
                    onWoordLetterClick(i2);
                }
            });
        }
    }

    private void onLetterClick(int i) {
        if (currentLevel.lettersAreClickable()) {
            letters.get(i).setVisibility(Button.INVISIBLE);
            //addLetterinWord((String) letters.get(i).getText());

            boolean addWoord = true;
            int nr = 0;
            while (addWoord) {
                if (currentLevel.woord_letters[nr].length() < 1) {
                    lettersInWord.get(nr).setText((String) letters.get(i).getText());
                    lettersInWord.get(nr).setBackgroundResource(R.drawable.letter_button);
                    currentLevel.woord_letters[nr] = (String) letters.get(i).getText();
                    currentLevel.woord_positie[nr] = i;
                    addWoord = false;
                }
                nr++;
                if (!currentLevel.lettersAreClickable()) {
                    checkLevel();
                    break;
                }
            }
        } else {
            checkLevel();
        }
    }

    private void checkLevel() {
        boolean check = true;
        for (int i = 0; i < currentLevel.correct_letters.length; i++) {
            if (!currentLevel.correct_letters[i].equalsIgnoreCase(currentLevel.woord_letters[i])) {
                check = false;
            }
        }

        if (check) {
            singleAnimation(0);
        } else {
            for (int i = 0; i < lettersInWord.size(); i++) {
                final int i2 = i;
                Animation load = AnimationUtils.loadAnimation(this, R.anim.shake);
                lettersInWord.get(i).startAnimation(load);
                load.setAnimationListener(new AnimationListener() {
                    public void onAnimationEnd(Animation animation) {
                        lettersInWord.get(i2).setTextColor(Color.BLACK);
                    }

                    public void onAnimationRepeat(Animation arg0) {
                    }

                    public void onAnimationStart(Animation arg0) {
                        lettersInWord.get(i2).setTextColor(getResources().getColor(R.color.color_redfail));
                    }
                });
            }
        }
    }

    private void onWoordLetterClick(int i) {
        if (lettersInWord.get(currentLevel.origin_positie[i]).length() > 0) {
            lettersInWord.get(currentLevel.origin_positie[i]).setText("");
            lettersInWord.get(currentLevel.origin_positie[i]).setBackgroundResource(R.drawable.btn_letter_empty);
            letters.get(currentLevel.woord_positie[currentLevel.origin_positie[i]]).setVisibility(Button.VISIBLE);

            currentLevel.woord_letters[currentLevel.origin_positie[i]] = "";
            currentLevel.woord_positie[currentLevel.origin_positie[i]] = -1;
        }
    }

    private void singleAnimation(final int i) {
        for (Button l : letters) {
            l.setClickable(false);
        }
        if (i == lettersInWord.size()) {


            for (int j = 0; j < lettersInWord.size(); j++) {
                final int j2 = j;
                Animation load = AnimationUtils.loadAnimation(this, R.anim.correct2);
                lettersInWord.get(j).startAnimation(load);

                if (j2 == 1) {
                    load.setAnimationListener(new AnimationListener() {
                        public void onAnimationEnd(Animation animation) {
                            lettersInWord.get(j2).setText(currentLevel.correct_letters[j2].toUpperCase(Locale.US));

                            //LEVEL COMPLETE
                            setNextContainerLayout();
                        }

                        public void onAnimationRepeat(Animation arg0) {
                        }

                        public void onAnimationStart(Animation arg0) {
                        }
                    });
                }
            }
        } else {
            Animation ani = AnimationUtils.loadAnimation(this, R.anim.correct);
            lettersInWord.get(i).startAnimation(ani);

            ani.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    //lettersInWord.get(i).setTextColor(Color.BLACK);
                    singleAnimation((i + 1));
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationStart(Animation arg0) {
                }
            });
        }
    }

    private void singleAnimationLetter(final int i) {
        if (i < letters.size()) {
            Animation ani = AnimationUtils.loadAnimation(this, R.anim.letters);
            letters.get(i).setVisibility(Button.VISIBLE);
            letters.get(i).startAnimation(ani);
            ani.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    singleAnimationLetter((i + 1));
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationStart(Animation arg0) {
                }
            });
        }
    }
}
