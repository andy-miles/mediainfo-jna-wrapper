/*
 * The MIT License
 * Copyright © 2024-2026 Andy Miles
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
package com.amilesend.mediainfo.type;

/** Defines the kind of info types available to parse from media files. */
public enum InfoType {
    /** Parameter name. */
    Name,
    /** Parameter value. */
    Text,
    /** Unit of parameter value. */
    Measure,
    /** Indicates options for parsing. */
    Options,
    /** Translated name of the parameter. */
    NameText,
    /** Translated name of the measurement unit. */
    MeasureText,
    /** More information about the parameter. */
    Info,
    /**
     * How this parameter is supported. Values can be:
     * <ul>
     *     <li>{@code N} (No)</li>
     *     <li>{@code B} (Beta)</li>
     *     <li>{@code R} (Read only)</li>
     *     <li>{@code W} (Read/Write)</li>
     * </ul>
     */
    HowTo,
    /** Domain of this piece of information. */
    Domain;
}
