package com.geckoapps.raaddeplaat.model;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.geckoapps.raaddeplaat.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public Level(Context context, int nr,  String imageResource, String answer, int tiles, int tilesPerTurn, int coins, ImageView image, Toolbar toolbar) {
        this.nr = nr;
        this.context = context;
        this.imageResource = imageResource;
        this.tiles = tiles;
        this.answer = answer;
        this.tilesPerTurn = tilesPerTurn;
        this.coins = coins;
        this.image = image;
        this.toolbar = toolbar;

        image.setVisibility(View.INVISIBLE);
        image.setImageResource(context.getResources().getIdentifier(imageResource,
                "drawable", context.getPackageName()));

        letters = new ArrayList<>();

        woorden = new ArrayList<>();
        String[] w = answer.split(" ");
        for (int i = 0; i < w.length; i++) {
            woorden.add(w[i]);
        }

        for (int i = 0; i < answer.length(); i++) {
            if (!answer.substring(i, i + 1).equals(" ")) {
                letters.add(answer.substring(i, i + 1));
            }
        }
        numberOfLetters = letters.size();
        resetData();

        removeHelp = new ArrayList<>();
        for (int i = letters.size(); i < max_letters; i++) {
            Random r = new Random();
            String l = alfabet[r.nextInt(alfabet.length)];
            letters.add(l);
            removeHelp.add(l);
        }

        Collections.shuffle(this.letters);
        Collections.shuffle(this.removeHelp);

        toolbar.initProgressBar( (tiles*tiles) );
    }


    public void resetData() {
        woord_letters = new String[numberOfLetters];
        correct_letters = new String[numberOfLetters];
        woord_positie = new int[numberOfLetters];
        origin_positie = new int[20];
        for (int i = 0; i < woord_letters.length; i++) {
            correct_letters[i] = letters.get(i);
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
                if (x == (getTiles() - 1) && y == (getTiles() - 1)) {
                    getBlock(x, y).fadeIn(context, image);
                } else {
                    getBlock(x, y).fadeIn(context, null);
                }

            }
        }
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
        toolbar.animateProgressBar(tilesPerTurn);
        if(isHasShuffledBlocks()) {
            shuffleBlocks();
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

    public boolean shuffleIsPossible(){
        return !shuffledBlocks.isEmpty();
    }
}

