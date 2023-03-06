/**
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***/

package net.wimpi.modbus.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;

/**
 * Interface defining a StreamTransport class.
 *
 * @author vssavin
 * @version @version@ (@date@)
 */
public interface StreamTransport {

    /**
     * Returns the input stream
     * @return input stream as <tt>InputStream</tt>.
     */
    InputStream getInputStream();

    /**
     * Returns the output stream
     * @return output stream as <tt>OutputStream</tt>.
     */
    OutputStream getOutputStream();

    /**
     * Sets the timeout in milliseconds
     * @param timeoutMs the timeout in milliseconds
     */
    void setTimeout(int timeoutMs);

    /**
     * Returns the open state of the object.
     * @return true if the object is open
     */
    boolean isOpen();

    /**
     * Unchecked exception thrown when error occurred.
     *
     * @author vssavin on 20.01.2023
     * @version @version@ (@date@)
     */
    class StreamTransportException extends RuntimeException {
        public StreamTransportException(String message) {
            super(message);
        }
        public StreamTransportException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
