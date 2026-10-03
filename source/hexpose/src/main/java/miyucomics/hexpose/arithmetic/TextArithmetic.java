package miyucomics.hexpose.arithmetic;

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic;
import at.petrak.hexcasting.api.casting.arithmetic.engine.InvalidOperatorException;
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator;
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic;
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBinary;
import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorUnary;
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate;
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import miyucomics.hexpose.iotas.TextIota;
import miyucomics.hexpose.utils.TextUtils;
import net.minecraft.network.chat.Component;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static at.petrak.hexcasting.api.casting.arithmetic.Arithmetic.*;

public class TextArithmetic implements Arithmetic {
    @Override
    public String arithName() {
        return "text_arithmetic";
    }

    @Override
    public Iterable<HexPattern> opTypes() {
        return List.of(ADD, MUL, ABS, INDEX, SLICE, REV, INDEX_OF, REMOVE, REPLACE, UNIQUE);
    }

    @Override
    public Operator getOperator(HexPattern pattern) {
        if (pattern.equals(ADD)) {
            return new OperatorBinary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (BinaryOperator<Iota>) (a, b) -> {
                        Component textA = ((TextIota) a).getText();
                        Component textB = ((TextIota) b).getText();
                        return new TextIota(Component.empty().append(textA).append(textB));
                    }
            );
        } else if (pattern.equals(MUL)) {
            return new OperatorBinary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (BinaryOperator<Iota>) (a, b) -> {
                        Component text = ((TextIota) a).getText();
                        double number = ((DoubleIota) b).getDouble();
                        Component newCopy = Component.empty();
                        for (int i = 0; i < (int) number; i++) {
                            newCopy = Component.empty().append(newCopy).append(text);
                        }
                        return new TextIota(newCopy);
                    }
            );
        } else if (pattern.equals(ABS)) {
            return new OperatorUnary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (UnaryOperator<Iota>) (a) -> {
                        Component text = ((TextIota) a).getText();
                        return new DoubleIota(TextUtils.split(text).size());
                    }
            );
        } else if (pattern.equals(INDEX)) {
            return new OperatorBinary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (BinaryOperator<Iota>) (a, b) -> {
                        Component text = ((TextIota) a).getText();
                        double number = ((DoubleIota) b).getDouble();
                        List<Component> split = TextUtils.split(text);
                        int idx = (int) number;
                        if (idx < 0 || idx >= split.size()) {
                            return new NullIota();
                        }
                        return new TextIota(split.get(idx));
                    }
            );
        } else if (pattern.equals(SLICE)) {
            return new OperatorBasic(
                    3,
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE))
            ) {
                @Override
                public Iterable<Iota> apply(Iterable<? extends Iota> iotas, CastingEnvironment env) {
                    Iterator<? extends Iota> it = iotas.iterator();
                    Component text = ((TextIota) it.next()).getText();
                    List<Component> list = TextUtils.split(text);

                    double d0 = ((DoubleIota) it.next()).getDouble();
                    double d1 = ((DoubleIota) it.next()).getDouble();

                    int index0 = (int) Math.floor(d0);
                    int index1 = (int) Math.floor(d1);

                    index0 = Math.max(0, Math.min(index0, list.size() - 1));
                    index1 = Math.max(0, Math.min(index1, list.size() - 1));

                    if (index0 == index1) {
                        return List.of(new TextIota(Component.empty()));
                    }

                    int start = Math.min(index0, index1);
                    int end = Math.max(index0, index1) + 1;
                    List<Component> subList = list.subList(start, end);
                    return List.of(new TextIota(TextUtils.mergeText(subList)));
                }
            };
        } else if (pattern.equals(REV)) {
            return new OperatorUnary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (UnaryOperator<Iota>) (a) -> {
                        Component text = ((TextIota) a).getText();
                        List<Component> list = TextUtils.split(text);
                        Collections.reverse(list);
                        return new TextIota(TextUtils.mergeText(list));
                    }
            );
        } else if (pattern.equals(INDEX_OF)) {
            return new OperatorBinary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (BinaryOperator<Iota>) (a, b) -> {
                        Component text1 = ((TextIota) a).getText();
                        Component text2 = ((TextIota) b).getText();

                        List<Component> haystack = TextUtils.split(text1);
                        List<Component> probe = TextUtils.split(text2);

                        if (probe.isEmpty() || probe.size() > haystack.size()) {
                            return new DoubleIota(-1.0);
                        }

                        for (int i = 0; i <= haystack.size() - probe.size(); i++) {
                            boolean match = true;
                            for (int j = 0; j < probe.size(); j++) {
                                if (!haystack.get(i + j).equals(probe.get(j))) {
                                    match = false;
                                    break;
                                }
                            }
                            if (match) {
                                return new DoubleIota(i);
                            }
                        }
                        return new DoubleIota(-1.0);
                    }
            );
        } else if (pattern.equals(REMOVE)) {
            return new OperatorBinary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (BinaryOperator<Iota>) (a, b) -> {
                        Component text = ((TextIota) a).getText();
                        double number = ((DoubleIota) b).getDouble();

                        List<Component> list = TextUtils.split(text);
                        int idx = (int) number;
                        if (idx >= 0 && idx < list.size()) {
                            list.remove(idx);
                        }
                        return new TextIota(TextUtils.mergeText(list));
                    }
            );
        } else if (pattern.equals(REPLACE)) {
            return new OperatorBasic(
                    3,
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE))
            ) {
                @Override
                public Iterable<Iota> apply(Iterable<? extends Iota> iotas, CastingEnvironment env) {
                    Iterator<? extends Iota> it = iotas.iterator();
                    Component text = ((TextIota) it.next()).getText();
                    double number = ((DoubleIota) it.next()).getDouble();
                    Component fragment = ((TextIota) it.next()).getText();

                    List<Component> list = TextUtils.split(text);
                    int idx = (int) number;
                    if (idx >= 0 && idx < list.size()) {
                        list.set(idx, fragment);
                    }
                    return List.of(new TextIota(TextUtils.mergeText(list)));
                }
            };
        } else if (pattern.equals(UNIQUE)) {
            return new OperatorUnary(
                    IotaMultiPredicate.all(IotaPredicate.ofType(TextIota.TYPE)),
                    (UnaryOperator<Iota>) (a) -> {
                        Component text = ((TextIota) a).getText();
                        Set<Component> seen = new HashSet<>();
                        Component result = Component.empty();
                        for (Component charText : TextUtils.split(text)) {
                            if (seen.add(charText)) {
                                result = Component.empty().append(result).append(charText);
                            }
                        }
                        return new TextIota(result);
                    }
            );
        } else {
            throw new InvalidOperatorException(pattern + " is not a valid operator in Arithmetic " + this + ".");
        }
    }
}