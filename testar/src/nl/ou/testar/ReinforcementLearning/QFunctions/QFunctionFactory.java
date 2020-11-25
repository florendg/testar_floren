package nl.ou.testar.ReinforcementLearning.QFunctions;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

public class QFunctionFactory {
    private final static float ALPHA_DISCOUNT = 1.0f;
    private static final float GAMMA_DISCOUNT = .99f;
    private static final float DEFAULT_QVALUE = 0.0f;

    public static QFunction getQFunction (final Settings settings){
        final float alphaDiscount = settings.get(ConfigTags.Alpha, ALPHA_DISCOUNT);
        final float gammaDiscount = settings.get(ConfigTags.Gamma, GAMMA_DISCOUNT);
        final float defaultQValue = settings.get(ConfigTags.DefaultValue, DEFAULT_QVALUE);

        System.out.println(String.format("QFunction loaded with alpha='%s' gammaDiscount='%s' and defaultQValue='%s'", alphaDiscount, gammaDiscount, defaultQValue));

        return new SarsaQFunction(alphaDiscount, gammaDiscount, defaultQValue);
    }
}