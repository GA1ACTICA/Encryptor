/**
 * Project: Encryptor
 *
 * Author: Galactica
 *
 * Licensed under the GPL 3.0 License.
 * See LICENSE file in the project root for full license information.
 *
 * Copyright © 2026 Galactica
 */
package command;

import org.jline.reader.LineReader;

import Networking.Client.Client;
import Networking.Server.Server;

public class NetworkCommand implements Command {

    LineReader reader;

    public NetworkCommand(LineReader reader) {
        this.reader = reader;
    }

    @Override
    public String name() {
        return "-N";
    }

    @Override
    public String secondaryName() {
        return "--Network";
    }

    @Override
    public String description() {
        return "Provides a way to either set up a server or connect to a known host";
    }

    @Override
    public void execute(String[] args) {
        while (true) {

            String input = prompt("Setup server or Connect to known host? |s/c|");

            if (input.equalsIgnoreCase("c")) {
                String address = prompt("Adders: |<pi>:<port>|");
                new Client(address.split(":")[0], Integer.parseInt(address.split(":")[1]));
                return;

            } else if (input.equalsIgnoreCase("s")) {
                int port = Integer.parseInt(prompt("Port:"));
                new Server(port);
                return;
            }

            System.out.println("Invalid syntax");
        }
    }

    private String prompt(String message) {
        System.out.println(message);
        return reader.readLine("> ").trim();
    }

}
