package me.nanorasmus.nanodev.hex_js.storage;


import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class PatternList {
    boolean isWhitelist = false;
    public ArrayList<String> angleSignatureList = new ArrayList<>();
    public HashMap<String, ArrayList<HexAngle>> redirectList = new HashMap<>();
    public HashMap<String, String> redirectListRaw = new HashMap<>();
    public int maxBookKeepersLength = -1;

    public PatternList() {
    }

    public PatternList(boolean isWhitelist) {
        this();
        this.isWhitelist = isWhitelist;
    }

    public PatternList(boolean isWhitelist, ArrayList<String> angleSignatureList) {
        this(isWhitelist);
        this.angleSignatureList = angleSignatureList;
    }

    public PatternList(boolean isWhitelist, ArrayList<String> angleSignatureList, HashMap<String, String> redirects) {
        this(isWhitelist, angleSignatureList);

        for (String redirectInput : redirects.keySet()) {
            addRedirect(redirectInput, redirects.get(redirectInput));
        }
    }

    public PatternList(boolean isWhitelist, ArrayList<String> angleSignatureList, HashMap<String, String> redirects, int maxBookKeepersLength) {
        this(isWhitelist, angleSignatureList, redirects);
        this.maxBookKeepersLength = maxBookKeepersLength;
    }

    public void clearPatternList() {
        angleSignatureList = new ArrayList<>();
    }

    public void clearRedirects() {
        redirectList = new HashMap<>();
        redirectListRaw = new HashMap<>();
    }

    public void addRedirect(String input, String output) {
        ArrayList<HexAngle> angles = IotaHelper.anglesFromString(output);
        redirectList.put(input, angles);
        redirectListRaw.put(input, output);
    }

    public void setRedirects(HashMap<String, String> redirects) {
        clearRedirects();
        for (String input : redirects.keySet()) {
            String output = redirects.get(input);

            ArrayList<HexAngle> angles = IotaHelper.anglesFromString(output);
            redirectList.put(input, angles);
            redirectListRaw.put(input, output);
        }
    }

    public PatternList copy() {
        return new PatternList(
                isWhitelist,
                new ArrayList<>(angleSignatureList),
                new HashMap<>(redirectListRaw),
                this.maxBookKeepersLength
        );
    }

    public boolean contains(HexPattern pattern) {
        boolean contains = false;
        for (String angleSignature : angleSignatureList) {
            if (angleSignature.equals(pattern.anglesSignature())) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public boolean contains(String signature) {
        return angleSignatureList.contains(signature);
    }

    public boolean blocks(HexPattern pattern) {
        boolean isBlocked = isWhitelist;
        for (String angleSignature : angleSignatureList) {
            if (angleSignature.equals(pattern.anglesSignature())) {
                isBlocked = !isWhitelist;
                break;
            }
        }
        return isBlocked;
    }

    public boolean blocks(String signature) {
        boolean isBlocked = isWhitelist;
        if (angleSignatureList.contains(signature)) {
            isBlocked = !isWhitelist;
        }
        return isBlocked;
    }


    public HexPattern handleRedirects(HexPattern pattern) {
        if (redirectList.containsKey(pattern.anglesSignature())) {
            return new HexPattern(pattern.getStartDir(), redirectList.get(pattern.anglesSignature()));
        }
        return null;
    }
}
