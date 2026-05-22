package command;

import org.jline.reader.LineReader;
import org.jline.utils.AttributedStyle;

import CipherData.CipherKeyStore;
import console.ConsoleOutput;

public class InputCipherKeyCommand implements Command {
    private final LineReader reader;

    public InputCipherKeyCommand(LineReader reader) {
        this.reader = reader;
    }

    public String name() {
        return "-ic";
    }

    public String secondaryName() {
        return "--input-cipher-key";
    }

    public String description() {
        return "Used for inputting CipherKeys";
    }

    public void execute(String[] args) {

        try {
            String cipherKey = reader
                    .readLine(ConsoleOutput.colorize("Input Cipher Key: ", AttributedStyle.YELLOW) + "> ");
            CipherKeyStore.set(cipherKey);
            ConsoleOutput.printLnInfo("Cipher Key stored successfully.");
        } catch (Exception e) {
            ConsoleOutput.printLnError("An error occurred: ");
        }

    }

}
