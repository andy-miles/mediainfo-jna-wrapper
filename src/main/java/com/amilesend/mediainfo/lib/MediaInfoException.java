/*
 * The MIT License
 * Copyright © 2024 Andy Miles
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.amilesend.mediainfo.lib;

import com.sun.jna.Platform;

/** Defines the exception thrown for errors when initializing the libMediaInfo library. */
public class MediaInfoException extends RuntimeException {
    public MediaInfoException(final LinkageError linkageError) {
        super(toMsg(linkageError), linkageError);
    }

    private static String toMsg(final LinkageError linkageError) {
        String name;
        switch (Platform.getOSType()) {
            case Platform.MAC:
                name = "libmediainfo.dylib";
                break;
            case Platform.WINDOWS:
                name = "MediaInfo.dll";
                break;
            default: /* Fall through. */
                name= "libmediainfo.so";
                break;
        }

        final String arch = System.getProperty("os.arch");
        final String bitArch = Platform.is64Bit() ? "64-bit" : "32-bit";
        return new StringBuilder("Unable to load ")
                .append(arch)
                .append(" (")
                .append(bitArch)
                .append(") native library ")
                .append(name)
                .append(": ")
                .append(linkageError.getMessage())
                .toString();
    }
}
