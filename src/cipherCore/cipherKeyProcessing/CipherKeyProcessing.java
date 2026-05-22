package cipherCore.cipherKeyProcessing;

import java.util.ArrayList;

import CipherData.CipherKeyCache;
import cipherCore.CommonVariables;
import cipherDataHandling.characterCodec.CharacterCodecRepository;
import cipherDataHandling.characterCodec.CharacterCodecService;
import console.ConsoleOutput;

class CipherKeySegmenter {
    ArrayList<Integer> leftOverCipherKey;
    int deflector;
    int[] decKeySegStepping;
    int[] decKeySegStepStart;
    int[] decKeySegPermutationMap;
    int[] decKeySegConditions;
    int decKeySegConReset;

    CipherKeySegmenter(ArrayList<Integer> leftOverCipherKey, int deflector, int[] stepping, int[] startStep,
            int[] permutationMap, int[] conditions, int conditionReset) {
        this.leftOverCipherKey = leftOverCipherKey;
        this.deflector = deflector;
        decKeySegStepping = stepping;
        decKeySegStepStart = startStep;
        decKeySegPermutationMap = permutationMap;
        decKeySegConditions = conditions;
        decKeySegConReset = conditionReset;
    }
}

class Combining {
    ArrayList<Integer> leftOverArrayListInput;
    int value;

    Combining(ArrayList<Integer> leftOverArrayListInput, int combinedIndex) {
        this.leftOverArrayListInput = leftOverArrayListInput;
        value = combinedIndex;
    }
}

class RotorReaderOutput {
    ArrayList<Integer> leftOverCipherKey;
    int[] stepping;
    int[] stepStart;
    int[] permutationMap;

    RotorReaderOutput(ArrayList<Integer> leftOverCipherKey, int[] stepping, int[] stepStart, int[] permutationMap) {
        this.leftOverCipherKey = leftOverCipherKey;
        this.stepping = stepping;
        this.stepStart = stepStart;
        this.permutationMap = permutationMap;
    }
}

/**
 * 
 */
public class CipherKeyProcessing {

    private static int permutationMapAmount;

    public CipherKeyProcessing(int permutationMapAmount) {
        CipherKeyProcessing.permutationMapAmount = permutationMapAmount;
    }

    public static int[] symbolMappingToIndex(String encrypted) {

        CharacterCodecService service = new CharacterCodecService(new CharacterCodecRepository());
        char[] chars = encrypted.toCharArray();

        int[] x = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            x[i] = service.getCharacterCodec().indexOf(chars[i]);
        }

        return x;
    }

    static Combining getCombining(ArrayList<Integer> arrayListInput, int stopPos) {

        if (CommonVariables.debug) {
            ConsoleOutput.printDebugInfo("Combining next " + stopPos + " integers from cipher key segment...");
        }

        arrayListInput.removeFirst();
        int x = 0;
        for (int i = 0; i < stopPos; i++) {
            int y = (int) Math.pow(10, (stopPos - 1) - i);
            if (CommonVariables.debug) {
                ConsoleOutput.printDebugInfo("Combining Value: " + arrayListInput.getFirst() + " * " + y);
            }
            x += arrayListInput.getFirst() * y;
            arrayListInput.removeFirst();
        }

        return new Combining(arrayListInput, x);

    }

    private static RotorReaderOutput rotorReader(ArrayList<Integer> cipherKeyInteger, int rotorCount) {

        int[] stepping = new int[rotorCount];
        int[] startStep = new int[rotorCount];
        int[] permutationMap = new int[rotorCount];

        for (int i = 0; i < rotorCount; i++) {

            //
            stepping[i] = cipherKeyInteger.getFirst(); // Extracting the "Key" part or the most important.
            cipherKeyInteger.removeFirst();
            startStep[i] = cipherKeyInteger.getFirst();
            cipherKeyInteger.removeFirst();

            if (CommonVariables.debug) {
                ConsoleOutput.printDebugInfo("Extracted stepping for rotor " + i + ": " + stepping[i]);
                ConsoleOutput.printDebugInfo("Extracted start step for rotor " + i + ": " + startStep[i]);
            }

            Combining pm = getCombining(cipherKeyInteger, cipherKeyInteger.getFirst());
            permutationMap[i] = pm.value % CipherKeyProcessing.permutationMapAmount;

            cipherKeyInteger = pm.leftOverArrayListInput;
        }
        return new RotorReaderOutput(cipherKeyInteger, stepping, startStep, permutationMap);
    }

    /**
     * 
     * @param cipherKeyInteger
     * @return
     */
    static CipherKeySegmenter getCipherKeySegmenter(ArrayList<Integer> cipherKeyInteger) {

        int permutationMapAmount = 100; // fix in future to be based on the units amount of permutation maps

        // reading condition content
        int conditionContent = cipherKeyInteger.getFirst();
        cipherKeyInteger.removeFirst();

        // reading deflector and rotor information
        Combining deflector = getCombining(cipherKeyInteger, cipherKeyInteger.getFirst());
        cipherKeyInteger = deflector.leftOverArrayListInput;

        // reading rotor amount and rotor information
        Combining rotor = getCombining(cipherKeyInteger, cipherKeyInteger.getFirst());
        cipherKeyInteger = rotor.leftOverArrayListInput;

        RotorReaderOutput rro = rotorReader(cipherKeyInteger, rotor.value);
        cipherKeyInteger = rro.leftOverCipherKey;

        // returns the information if there is no conditions
        if (conditionContent == 0) {
            return new CipherKeySegmenter(cipherKeyInteger, deflector.value % permutationMapAmount, rro.stepping,
                    rro.stepStart,
                    rro.permutationMap, null,
                    0);
        }

        // Reading in Condition amount
        Combining conA = getCombining(cipherKeyInteger, cipherKeyInteger.getFirst());
        cipherKeyInteger = conA.leftOverArrayListInput;

        int[] conditions = new int[conA.value * 2];

        // Extracting ConditionStart And ConditionDuration
        for (int i = 0; i < conA.value * 2; i++) {
            Combining con = getCombining(cipherKeyInteger, cipherKeyInteger.getFirst());
            conditions[i] = con.value;
            cipherKeyInteger = con.leftOverArrayListInput;
        }

        if (conditionContent == 2) {
            Combining conR = getCombining(cipherKeyInteger, cipherKeyInteger.getFirst());
            int conditionReset = conR.value;

            return new CipherKeySegmenter(cipherKeyInteger, deflector.value % permutationMapAmount, rro.stepping,
                    rro.stepStart,
                    rro.permutationMap,
                    conditions, conditionReset);
        }

        return new CipherKeySegmenter(cipherKeyInteger, deflector.value % permutationMapAmount, rro.stepping,
                rro.stepStart,
                rro.permutationMap,
                conditions, 0);
    }

    @Deprecated
    private void validateCipherKey(String cipherKey) {
        if (cipherKey == null || cipherKey.isEmpty()) {
            throw new IllegalArgumentException("Cipher key cannot be null or empty");
        }
        // Add more validation rules as needed, such as allowed characters, format, etc.
    }

    /**
     * 
     * @param cipherKey
     */
    public CipherKeyCache cipherKeyReader(String cipherKey) {

        //

        if (cipherKey.length() > CommonVariables.maxCipherKeyLength) {
            throw new IllegalArgumentException(
                    "Cipher key length exceeds maximum allowed: " + CommonVariables.maxCipherKeyLength);
            // add a feature in future to split the cipher key into multiple parts if it
            // exceeds the maximum length, and process them sequentially or in parallel.
        }

        ArrayList<Integer> cipherKeyInteger = new ArrayList<Integer>();
        int[] mappedKey = CipherKeyProcessing.symbolMappingToIndex(cipherKey);
        for (int i = 0; i < mappedKey.length; i++) {
            cipherKeyInteger.add(mappedKey[i]);
        }

        //
        Combining cipKeyAmount = getCombining(cipherKeyInteger, cipherKeyInteger.getFirst());
        cipherKeyInteger = cipKeyAmount.leftOverArrayListInput;

        int[] deflector = new int[cipKeyAmount.value];
        int[][] stepping = new int[cipKeyAmount.value][];
        int[][] stepStart = new int[cipKeyAmount.value][];
        int[][] permutationMap = new int[cipKeyAmount.value][];
        int[][] conditions = new int[cipKeyAmount.value][];
        int[] conditionResets = new int[cipKeyAmount.value];

        for (int i = 0; i < cipKeyAmount.value; i++) {
            try {
                CipherKeySegmenter segment = getCipherKeySegmenter(cipherKeyInteger);
                deflector[i] = segment.deflector;
                stepping[i] = segment.decKeySegStepping;
                stepStart[i] = segment.decKeySegStepStart;
                permutationMap[i] = segment.decKeySegPermutationMap;
                conditions[i] = segment.decKeySegConditions;
                conditionResets[i] = segment.decKeySegConReset;

                cipherKeyInteger = segment.leftOverCipherKey;

            } catch (Exception e) {
                e.printStackTrace();
                ConsoleOutput.printLnError("Sowwy :3, No wowky");

            }
        }

        CipherKeyCache x = new CipherKeyCache(deflector, stepping, stepStart, permutationMap,
                conditions,
                conditionResets);

        return x;

    }

}
