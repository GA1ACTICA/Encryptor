package cipherCore;

import CipherData.CipherKeyCache;
import CipherData.CipherKeySegmentCache;
import cipherDataHandling.characterCodec.CharacterCodecRepository;
import cipherDataHandling.characterCodec.CharacterCodecService;
import cipherDataHandling.permutationMap.PermutationMapRepository;
import cipherDataHandling.permutationMap.PermutationMapService;
import console.ConsoleOutput;

public class CipherManager {

    private int[] encipheringWithCipherSegment(Encrypting encryptor, int[] toBeEncrypted, CipherKeySegmentCache p) {

        if (CommonVariables.debug) {
            for (int j = 0; j < p.stepping().length; j++) {
                ConsoleOutput.printLnDebugInfo(
                        "Stepping for rotor " + (j) + ": " + p.stepping()[j] + " | Start Step: " + p.stepStart()[j]);
            }
        }

        return encryptor.calculate(toBeEncrypted, p);

    }

    private int[] cipherCoordinator(int[] toBeEncrypted, boolean decrypt, CipherKeyCache cipherKeyCache) {

        PermutationMapService pm = new PermutationMapService(new PermutationMapRepository());
        CharacterCodecService cc = new CharacterCodecService(new CharacterCodecRepository());

        Encrypting encryptor = new Encrypting(pm.getPermutationMap(), cc.getCharacterCodecLength());

        if (decrypt == false) {
            for (int i = 0; i < cipherKeyCache.stepping().length; i++) {
                CipherKeySegmentCache p = new CipherKeySegmentCache(cipherKeyCache.deflector()[i], cipherKeyCache.stepping()[i], cipherKeyCache.stepStart()[i],
                        cipherKeyCache.permutationMap()[i], cipherKeyCache.conditions()[i], cipherKeyCache.conditionReset()[i]);

                toBeEncrypted = encipheringWithCipherSegment(encryptor, toBeEncrypted, p);
            }
        } else {
            for (int i = cipherKeyCache.stepping().length - 1; i > -1; i--) {
                CipherKeySegmentCache p = new CipherKeySegmentCache(cipherKeyCache.deflector()[i], cipherKeyCache.stepping()[i], cipherKeyCache.stepStart()[i],
                        cipherKeyCache.permutationMap()[i], cipherKeyCache.conditions()[i], cipherKeyCache.conditionReset()[i]);

                toBeEncrypted = encipheringWithCipherSegment(encryptor, toBeEncrypted, p);
            }
        }

        return toBeEncrypted;
    }

    public int[] runCipher(int[] toBeEncrypted, boolean encryptOrDecrypt, CipherKeyCache cipherKeyCache) {

        try {
            CipherManager p = new CipherManager();

            return p.cipherCoordinator(toBeEncrypted, encryptOrDecrypt, cipherKeyCache);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println();
            System.err.println("Failed to run Cipher");
            return null;
        }

    }

}
