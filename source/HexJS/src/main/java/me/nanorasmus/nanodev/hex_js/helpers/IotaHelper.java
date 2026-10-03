package me.nanorasmus.nanodev.hex_js.helpers;



import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import java.util.ArrayList;

public class IotaHelper {
    public static ArrayList<HexAngle> anglesFromString(String input) {
        ArrayList<HexAngle> angles = new ArrayList<>();
        for (String angle : input.toLowerCase().split("")) {
            switch (angle) {
                case "a":
                    angles.add(HexAngle.LEFT_BACK);
                    break;
                case "q":
                    angles.add(HexAngle.LEFT);
                    break;
                case "w":
                    angles.add(HexAngle.FORWARD);
                    break;
                case "e":
                    angles.add(HexAngle.RIGHT);
                    break;
                case "d":
                    angles.add(HexAngle.RIGHT_BACK);
                    break;
                case "s":
                    angles.add(HexAngle.BACK);
                    break;
            }
        }
        return angles;
    }

    public static HexPattern patternFromString(String angles) {
        return new HexPattern(HexDir.EAST, anglesFromString(angles));
    }

    public static PatternIota patternIotaFromString(String angles) {
        return new PatternIota(new HexPattern(
                    HexDir.EAST,
                    IotaHelper.anglesFromString(angles)
        ));
    }

    public static ArrayList<Iota> patternIotasFromStrings(ArrayList<String> patternAngles) {
        ArrayList<Iota> patterns = new ArrayList<>();

        for (String angles : patternAngles) {
            patterns.add(patternIotaFromString(angles));
        }
        return patterns;
    }

    /**
     * Sets the element at the given index of the given list iota to the given iota and returns a modified copy of the list iota, or null if the index was out of bounds.
     * @param list the list to insert the iota into
     * @param idx The index to override
     * @param iota the iota to override with
     * @return itself or null if the index was out of bounds
     */
    public static ListIota setElementAtIndexOfListIota(ListIota list, int idx, Iota iota) {
        if (idx >= list.getList().size()) {
            return null;
        }

        return new ListIota(list.getList().modifyAt(idx, it -> new SpellList.LPair(iota, it.getCdr())));
    }


    /**
     * @param list the list to retrieve the iota from
     * @param idx The index of the desired iota
     * @return The iota at the given index, or null if there is none
     */
    public Iota getIotaAtIndexofListIota (ListIota list, int idx) {
        if (idx >= list.getList().size()) {
            return null;
        }
        return list.getList().getAt(idx);
    }
}
