package com.geckoapps.raaddeplaat.model;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.geckoapps.raaddeplaat.activity.LevelActivity;
import com.geckoapps.raaddeplaat.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Sjoerd on 15-1-2016.
 */


public class Level {
    private int nr;
    private Context context;
    private String imageResource;
    private int tiles;
    private String answer;
    private String answer_en;
    private String answer_nl;
    private int tilesPerTurn;
    private int coins;
    private Block[][] blocks;
    private boolean hasShuffledBlocks;
    private ImageView image;
    private List<Block> shuffledBlocks;
    private Toolbar toolbar;

    private ArrayList<String> letters;
    private final int max_letters = 16;
    private String[] alfabet = {"a", "b", "c", "d", "e", "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    private ArrayList<String> woorden;
    public String[] woord_letters;
    public String[] correct_letters;
    public int[] woord_positie;
    public int[] origin_positie;
    private int numberOfLetters;
    private ArrayList<String> removeHelp;

    public Level(Context context, int nr,  String imageResource, String answer_en, String answer_nl, int tiles, int tilesPerTurn, int coins, ImageView image, Toolbar toolbar) {
        this.nr = nr;
        this.context = context;
        this.imageResource = imageResource;
        this.tiles = tiles;
        this.answer_en = answer_en;
        this.answer_nl = answer_nl;
        this.tilesPerTurn = tilesPerTurn;
        this.coins = coins;
        this.image = image;
        this.toolbar = toolbar;

        if(Locale.getDefault().getLanguage().equals("nl")){
            answer = this.answer_nl;
        } else {
            answer = this.answer_en;
        }

        image.setVisibility(View.INVISIBLE);
        image.setImageResource(context.getResources().getIdentifier(imageResource,
                "drawable", context.getPackageName()));

        letters = new ArrayList<>();

        woorden = new ArrayList<>();
        String[] w = answer.split(" ");
        for (int i = 0; i < w.length; i++) {
            woorden.add(w[i]);
        }

        removeHelp = new ArrayList<>();
        if(getNr() == 1){
            if(Locale.getDefault().getLanguage().equals("nl")){
                letters.add("H");removeHelp.add("H");
                letters.add("I");removeHelp.add("I");
                letters.add("I");removeHelp.add("I");
                letters.add("F");removeHelp.add("F");
                letters.add("T");removeHelp.add("T");
                letters.add("A");removeHelp.add("A");
                letters.add("G");removeHelp.add("G");
                letters.add("L");removeHelp.add("L");

                letters.add("W");removeHelp.add("W");
                letters.add("Z");removeHelp.add("Z");
                letters.add("J");removeHelp.add("J");
                letters.add("V");removeHelp.add("V");
                letters.add("R");removeHelp.add("R");
                letters.add("K");removeHelp.add("K");
                letters.add("E");removeHelp.add("E");
                letters.add("E");removeHelp.add("E");
                numberOfLetters = 8;


            }else{
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");

                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
                letters.add("G");removeHelp.add("G");
            }
        } else {
            for (int i = 0; i < answer.length(); i++) {
                if (!answer.substring(i, i + 1).equals(" ")) {
                    letters.add(answer.substring(i, i + 1));
                }
            }
            numberOfLetters = letters.size();

            for (int i = letters.size(); i < max_letters; i++) {
                Random r = new Random();
                String l = alfabet[r.nextInt(alfabet.length)];
                letters.add(l);
                removeHelp.add(l);
            }
            Collections.shuffle(this.letters);
            Collections.shuffle(this.removeHelp);
        }

        resetData();
        if(getNr() == 1){
            if(Locale.getDefault().getLanguage().equals("nl")) {
                correct_letters[0] = "G";
                correct_letters[1] = "E";
                correct_letters[2] = "I";
                correct_letters[3] = "L";

                correct_letters[4] = "W";
                correct_letters[5] = "I";
                correct_letters[6] = "J";
                correct_letters[7] = "F";
            } else{

            }
        }

        toolbar.initProgressBar( (tiles*tiles) );
    }


    public void resetData() {
        woord_letters = new String[numberOfLetters];
        correct_letters = new String[numberOfLetters];
        woord_positie = new int[numberOfLetters];
        origin_positie = new int[20];
        for (int i = 0; i < woord_letters.length; i++) {
            if(getNr() != 1) {
                correct_letters[i] = letters.get(i);
            }
            woord_letters[i] = "";
            woord_positie[i] = -1;
        }
    }


    public void setBlocks(Block[][] blocks) {
        //set 10% coins
        Random r = new Random();

        int count = 0;
        while (count < coins) {
            int x = r.nextInt(tiles);
            int y = r.nextInt(tiles);
            if (!blocks[x][y].isHasCoin()) {
                blocks[x][y].setHasCoin(true);
                count++;
            }
        }
        this.blocks = blocks;

        shuffledBlocks = new ArrayList<>();
        for (int x = 0; x < getTiles(); x++) {
            for (int y = 0; y < getTiles(); y++) {
                shuffledBlocks.add(this.blocks[x][y]);
            }
        }
        Collections.shuffle(shuffledBlocks);

        showBlocks();
    }


    public int getTiles() {
        return tiles;
    }

    private Block getBlock(int x, int y) {
        return blocks[x][y];
    }

    public void showBlocks() {
        for (int x = 0; x < getTiles(); x++) {
            for (int y = 0; y < getTiles(); y++) {
                getBlock(x,y).setVisibility(View.VISIBLE);
            }
        }
        image.setVisibility(View.VISIBLE);
    }

    public void removeBlocksForTurn() {
        hasShuffledBlocks = false;
        for (int j = 0; j < shuffledBlocks.size(); j++) {
            if (shuffledBlocks.get(j).isSelected()) {
                //add coins
                if(shuffledBlocks.get(j).isHasCoin()){
                    toolbar.addCoins(Utils.PRIZE_FOR_COIN);
                }
                shuffledBlocks.get(j).breakBlock();
            }
        }
        int count = tilesPerTurn;
        while (count > 0) {
            for (int j = 0; j < shuffledBlocks.size(); j++) {
                if (shuffledBlocks.get(j).isSelected()) {
                    shuffledBlocks.remove(j);
                    count--;
                    break;
                }
            }
        }
        hasShuffledBlocks = true;
        LevelActivity.progressGoing = true;
        toolbar.animateProgressBar(tilesPerTurn);

        if (isHasShuffledBlocks()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    shuffleBlocks();
                }
            }, 1500);
        }
    }

    public void shuffleBlocks() {
        Collections.shuffle(shuffledBlocks);

        for (int j = 0; j < shuffledBlocks.size(); j++) {
            shuffledBlocks.get(j).setSelected(false);
        }
        if(!shuffledBlocks.isEmpty()) {
            for (int i = 0; i < tilesPerTurn; i++) {
                shuffledBlocks.get(i).fadeInBlock();
            }
            hasShuffledBlocks = true;
        } else {
            hasShuffledBlocks = false;
        }
    }

    public boolean isHasShuffledBlocks() {
        return hasShuffledBlocks;
    }

    public String[] getWoord_letters() {
        return woord_letters;
    }

    public void setWoord_letters(String[] woord_letters) {
        this.woord_letters = woord_letters;
    }

    public int[] getWoord_positie() {
        return woord_positie;
    }

    public void setWoord_positie(int[] woord_positie) {
        this.woord_positie = woord_positie;
    }


    public ArrayList<String> getRemoveHelp() {
        return removeHelp;
    }

    public int getAantal() {
        return 1;
    }


    public ArrayList<String> getWoorden() {
        return woorden;
    }

    public void setWoorden(ArrayList<String> woorden) {
        this.woorden = woorden;
    }

    public ArrayList<String> getLetters() {
        return letters;
    }


    public void setLetters(ArrayList<String> letters) {
        this.letters = letters;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public boolean lettersAreClickable() {
        boolean canClick = false;
        for (int i = 0; i < woord_letters.length; i++) {
            if (woord_letters[i].length() == 0) {
                canClick = true;
            }
        }

        return canClick;
    }

    public int getNr() {
        return nr;
    }

    public boolean shuffleIsPossible(){
        return !shuffledBlocks.isEmpty();
    }
}

