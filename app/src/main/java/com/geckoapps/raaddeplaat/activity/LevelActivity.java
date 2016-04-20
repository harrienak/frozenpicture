package com.geckoapps.raaddeplaat.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    @Bind(R.id.level_letters_container)
    LinearLayout lettersContainer;
    @Bind(R.id.level_next_container)
    LinearLayout nextContainer;
    @Bind(R.id.level_button_container)
    RelativeLayout
            buttonContainer;
    @Bind(R.id.level_completed_container)
    LinearLayout completedContainer;
    @Bind(R.id.level_tutorial_overlay)
    LinearLayout tutorialContainer;
    @Bind(R.id.level_image_container)
    RelativeLayout levelImageContainer;

    @Bind(R.id.level_completed_title)
    TextView titleCompleted;
    @Bind(R.id.level_completed_subtitle)
    TextView subtitleCompleted;
    @Bind(R.id.level_completed_coins)
    TextView coinsCompleted;
    @Bind(R.id.level_guy)
    ImageView guy;
    @Bind(R.id.level_guy_completed)
    ImageView guyCompleted;
    @Bind(R.id.level_cloud)
    TextView cloud;
    @Bind(R.id.level_cloud_completed)
    TextView cloudCompleted;
    @Bind(R.id.level_overlay)
    LinearLayout overlay;
    @Bind(R.id.buttonLetterHint)
    Button hint;
    @Bind(R.id.buttonBom)
    Button bom;
    @Bind(R.id.button_axe)
    Button axe;
    @Bind(R.id.tutorial_axe)
    Button axeTutorial;
    @Bind(R.id.button_shuffle)
    Button shuffleButton;
    @Bind(R.id.button_share)
    Button shareButton;
    @Bind(R.id.level_sunrise)
    ImageView sunrise;

    private Level currentLevel;
    private ArrayList<Button> letters, woord, lettersInWord;
    private Typeface typeface;
    private DatabaseHelper db;
    private boolean tutorialGoing = false;

    public static boolean progressGoing = false;

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
        sunrise.clearAnimation();
        sunrise.setVisibility(View.INVISIBLE);
        shuffleButton.setVisibility(View.VISIBLE);
        axe.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);

        getLevel();
        setLevel();
        currentLevel.showBlocks();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shuffleBlocks();
                if (Utils.getSharedPref(LevelActivity.this, Utils.SHARED_LEVEL) == 1) {
                    checkTutorial();
                } else {
                    singleAnimationLetter(0);
                }
            }
        }, 200);
    }

    @OnClick(R.id.button_axe)
    public void removeBlocks() {
        if (!tutorialGoing || !LevelActivity.progressGoing) {
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
    }

    @OnClick(R.id.button_shuffle)
    public void shuffleBlocksCheck() {
        if (!tutorialGoing || !LevelActivity.progressGoing) {
            if (toolbar.spendCoins(Utils.PRIZE_SHUFFLE)) {
                shuffleBlocks();
            } else {
                showNotEnoughMoneyDialog();
            }
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
        if (!tutorialGoing || !LevelActivity.progressGoing) {
            toolbar.addCoins(100);
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            101);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                shareScreen();
            }
        }
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void shareScreen() {
        RelativeLayout allImages = (RelativeLayout) findViewById(R.id.share_layout);
        allImages.setBackgroundResource(R.drawable.fp_background);
        Bitmap emojis = screenShot(allImages);
        allImages.setBackgroundResource(android.R.color.transparent);
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), emojis, "fp_" + currentLevel.getNr(), null);
        Uri bmpUri;

        if (pathofBmp != null) {
            Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
            bmpUri = Uri.parse(pathofBmp);
            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
            emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent1.putExtra(Intent.EXTRA_TITLE, "test in je bakkes");
            emailIntent1.putExtra(Intent.EXTRA_TEXT, getString(R.string.level_share_title) + "; " + "https://play.google.com/store/apps/details?id=com.xxx.frozenpicture");
            emailIntent1.setType("image/png");
            startActivity(emailIntent1);
        } else {
            Toast.makeText(this, getString(R.string.level_share_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 101: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    shareScreen();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @OnClick(R.id.level_next_button)
    public void nextLevel() {
        toolbar.addLevel(1);
        for (Button l : lettersInWord) {
            l.clearAnimation();
        }
        initLevel();
    }

    public void setLetterContainerLayout() {
        nextContainer.setVisibility(View.GONE);
        lettersContainer.setVisibility(View.VISIBLE);
        completedContainer.clearAnimation();
        completedContainer.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.VISIBLE);
        blockContainer.clearAnimation();
        blockContainer.setVisibility(View.VISIBLE);
        tutorialContainer.setVisibility(View.GONE);


    }


    boolean canRemove = true;

    @OnClick(R.id.buttonBom)
    public void removeLetters() {
        if (!tutorialGoing || !LevelActivity.progressGoing) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_removeletters))
                    .setMessage(getString(R.string.text_removeletters) + " " + Utils.PRIZE_BOMB + " " + getString(R.string.coins_q))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (!canRemove) {
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
        if (!tutorialGoing || !LevelActivity.progressGoing) {
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
        lettersInWord.get(nr).setBackgroundResource(R.drawable.letter_button_correct);


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
            bom.setVisibility(View.INVISIBLE);
            hint.setVisibility(View.INVISIBLE);

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
                    if (tutorialGoing) {
                        if (i2 == 4) {
                            tutorialOnLetterClick(i2);
                        } else {
                            cloud.setText(getString(R.string.tutorial_wrong_letter));
                        }
                    } else {
                        onLetterClick(i2);
                    }
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
        titleCompleted.setTypeface(typeface);
        subtitleCompleted.setTypeface(typeface);
        coinsCompleted.setTypeface(typeface);
        cloud.setTypeface(typeface);
        shareButton.setTypeface(typeface);
        shuffleButton.setTypeface(typeface);
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
                        lettersInWord.get(i2).setBackgroundResource(R.drawable.letter_button);
                    }

                    public void onAnimationRepeat(Animation arg0) {
                    }

                    public void onAnimationStart(Animation arg0) {
                        lettersInWord.get(i2).setBackgroundResource(R.drawable.letter_button_false);

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
            //for (int j = 0; j < lettersInWord.size(); j++) {

            levelCompleted();
                /*
                final int j2 = j;
                Animation load = AnimationUtils.loadAnimation(this, R.anim.correct2);
                lettersInWord.get(j).startAnimation(load);
                lettersInWord.get(j).setBackgroundResource(R.drawable.letter_button_correct);
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
                }*/
            //}
        } else {
            Animation ani = AnimationUtils.loadAnimation(this, R.anim.correct);
            lettersInWord.get(i).startAnimation(ani);

            ani.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
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
        } else if (hint.getVisibility() == View.INVISIBLE) {
            Animation ani = AnimationUtils.loadAnimation(this, R.anim.letters);
            hint.setVisibility(Button.VISIBLE);
            hint.startAnimation(ani);
            ani.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    singleAnimationLetter((i + 1));
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationStart(Animation arg0) {
                }
            });
        } else if (bom.getVisibility() == View.INVISIBLE) {
            Animation ani = AnimationUtils.loadAnimation(this, R.anim.letters);
            bom.setVisibility(Button.VISIBLE);
            bom.startAnimation(ani);
            ani.setAnimationListener(new AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    singleAnimationLetter((i + 1));
                }

                public void onAnimationRepeat(Animation arg0) {
                }

                public void onAnimationStart(Animation arg0) {
                }
            });
        } else if (tutorialGoing) {
            showGuy2();
        }

    }

    ///////////////////////////
    //LEVEL COMPLETED ANIMATION
    ///////////////////////////
    private void levelCompleted() {
        fadeOutStuff();
        correctButtonFloat();
        showOakenGuy();

       /* for (int j = 0; j < lettersInWord.size(); j++) {

            Animation load = AnimationUtils.loadAnimation(this, R.anim.correct2);
            lettersInWord.get(j).startAnimation(load);
            lettersInWord.get(j).setBackgroundResource(R.drawable.letter_button_correct);
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
        }*/
    }

    private void fadeOutStuff() {
        final AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        lettersContainer.setAnimation(alphaAnimation);

        axe.setAnimation(alphaAnimation);
        shareButton.setAnimation(alphaAnimation);
        shuffleButton.setAnimation(alphaAnimation);

        alphaAnimation.start();
        lettersContainer.setVisibility(View.INVISIBLE);
        axe.setVisibility(View.INVISIBLE);
        shareButton.setVisibility(View.INVISIBLE);
        shuffleButton.setVisibility(View.INVISIBLE);
    }

    private void correctButtonFloat() {
        for (int i = 0; i < lettersInWord.size(); i++) {
            final int j = i;
            lettersInWord.get(j).setBackgroundResource(R.drawable.letter_button_correct);
            int duration = Utils.getRandom((j * 300) + 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, -50);
                    translateAnimation.setRepeatMode(Animation.REVERSE);
                    translateAnimation.setRepeatCount(Animation.INFINITE);
                    translateAnimation.setDuration(Utils.getRandom(1000, 2000));
                    translateAnimation.setFillAfter(true);
                    lettersInWord.get(j).setAnimation(translateAnimation);
                    translateAnimation.start();
                }
            }, duration);
        }
    }

    private void showOakenGuy() {
        Animation animGuy = AnimationUtils.loadAnimation(this, R.anim.anim_guy);
        guyCompleted.setBackgroundResource(R.drawable.oaken_normal);
        guyCompleted.setAnimation(animGuy);
        animGuy.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                guyCompleted.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                guyCompleted.setBackgroundResource(R.drawable.anim_oaken_correct);
                ((AnimationDrawable) guyCompleted.getBackground()).start();
                hideOakenGuy();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animGuy.start();
    }

    private void hideOakenGuy() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animGuy = AnimationUtils.loadAnimation(LevelActivity.this, R.anim.anim_guy_down);
                guyCompleted.clearAnimation();
                guyCompleted.setAnimation(animGuy);
                animGuy.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        guyCompleted.clearAnimation();
                        guyCompleted.setVisibility(View.GONE);
                        levelCompletedPart2();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                animGuy.start();
            }
        }, 1250);
    }

    private void levelCompletedPart2() {
        startSunrise();
        // rotateFrame();
        setNextContainerLayout();
    }

    private void startSunrise() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_sunrise);
        sunrise.setAnimation(animation);
        animation.start();
    }

    private void rotateFrame() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.frame_rotate);
        levelImageView.setAnimation(animation);
        animation.start();
    }

    public void setNextContainerLayout() {
        //nextContainer.setVisibility(View.VISIBLE); //later infaden
        lettersContainer.setVisibility(View.GONE);
        buttonContainer.setVisibility(View.INVISIBLE);
        tutorialContainer.setVisibility(View.GONE);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadeoutcompleted);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showTextInBlock();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        blockContainer.setAnimation(animation);
        animation.start();
        blockContainer.setVisibility(View.GONE);

        // completedContainer.setVisibility(View.VISIBLE);
    }

    private void showTextInBlock() {
        completedContainer.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadeincompleted);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                showScoreLevel();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        completedContainer.setAnimation(animation);
        animation.start();
    }

    private void showScoreLevel() {
        //stars

        coinsCompleted.setText("x" + "coins");


        nextContainer.setVisibility(View.VISIBLE);
    }

    ///////////////////////////
    //TUTORIAL
    ///////////////////////////
    private void checkTutorial() {
        tutorialGoing = true;
        overlay.setVisibility(View.VISIBLE);
        axe.setVisibility(View.INVISIBLE);
        animateGuy();
        // add end: singleAnimationLetter(0);
    }

    private void animateGuy() {
        guy.setBackgroundResource(R.drawable.anim_oaken);
        Animation animGuy = AnimationUtils.loadAnimation(this, R.anim.anim_guy);
        animGuy.setFillAfter(true);
        animGuy.setFillEnabled(true);
        cloud.setText(getString(R.string.tutorial_intro));
        guy.setAnimation(animGuy);
        animGuy.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                guy.setVisibility(View.VISIBLE);

                cloud.setVisibility(View.VISIBLE);
                tutorialContainer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((AnimationDrawable) guy.getBackground()).start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animGuy.start();
    }

    @OnClick(R.id.tutorial_axe)
    public void axeClick() {
        guy.clearAnimation();
        cloud.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        guy.setVisibility(View.GONE);
        axeTutorial.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadeout));
        axe.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein));
        currentLevel.removeBlocksForTurn();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setLetterContainerLayout();
                singleAnimationLetter(0);
            }
        }, 800);
        ;
    }

    private void showGuy2() {
        Animation animGuy = AnimationUtils.loadAnimation(this, R.anim.anim_guy);
        animGuy.setFillAfter(true);
        animGuy.setFillEnabled(true);
        cloud.setText(getString(R.string.tutorial_letters));
        guy.setAnimation(animGuy);
        animGuy.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                guy.setVisibility(View.VISIBLE);
                cloud.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((AnimationDrawable) guy.getBackground()).start();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animGuy.start();
    }

    private void tutorialOnLetterClick(int i2) {
        guy.clearAnimation();
        guy.setVisibility(View.GONE);
        cloud.setVisibility(View.GONE);
        onLetterClick(i2);
        tutorialGoing = false;
    }

}
