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
package Networking;

@FunctionalInterface
public interface MessageListener {
    void messageReceived(String message);
}