/*
 * The MIT License
 * Copyright © 2024-2025 Andy Miles
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
package com.amilesend.mediainfo;

import com.amilesend.mediainfo.lib.MediaInfoAccessor;
import com.amilesend.mediainfo.type.InfoType;
import com.amilesend.mediainfo.type.StreamType;

/**
 * The default optional class to access media file attributes without the need to define a customized class that
 * extends {@link MediaInfoBase}.
 *
 * Example usage:
 *
 * <pre>
 * MediaInfoLibrary library = MediaInfoLibrary.newInstance();
 * MediaInfoAccessor accessor = new MediaInfoAccessor(library);
 * try (MediaInfo myVideo = new MediaInfo(accessor).open("./MyVideo.mkv")) {
 *     List<String> videoCodecs = MediaInfo.parseList(myVideo.get(StreamType.General, 0, "Video_Codec_List"));
 *     int videoWidth = Integer.parseInt(myVideo.get(StreamType.Video, 0, "Width"));
 *
 *     // Get and parse additional parameters...
 * }
 * </pre>
 *
 * @see MediaInfoBase
 */
public class MediaInfo extends MediaInfoBase<MediaInfo> {
    /**
     * Creates a new {@code MediaInfo} object.
     *
     * @param accessor the accessor used to call the underling libMediaInfo library.
     */
    public MediaInfo(final MediaInfoAccessor accessor) {
        super(accessor);
    }

    /**
     * Get all details about a file.
     *
     * @return All details about a file in one string
     */
    public String inform() {
        return getAccessor().inform();
    }

    /**
     * Get a piece of information about a file (parameter is a string).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameter the parameter you are looking for in the stream (e.g., resolution, codec, bitrate, etc.)
     * @return the query result, or empty if there is a problem or not found
     * @see StreamType
     */
    public String get(final StreamType streamType, final int streamNumber, final String parameter) {
        return getAccessor().get(streamType, streamNumber, parameter);
    }

    /**
     * Get a piece of information about a file (parameter is a string).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameter the parameter you are looking for in the stream (e.g., resolution, codec, bitrate, etc.)
     * @param infoType the type of information about the parameter
     * @see StreamType
     * @see InfoType
     */
    public String get(
            final StreamType streamType,
            final int streamNumber,
            final String parameter,
            final InfoType infoType) {
        return getAccessor().get(streamType, streamNumber, parameter, infoType);
    }

    /**
     * Get a piece of information about a file (parameter is a string). For a list of available parameters, please
     * refer to <a href="https://github.com/MediaArea/MediaInfoLib/blob/master/Source/Resource/Text/Stream/General.csv">
     * General.csv</a>.
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameter the parameter you are looking for in the stream (e.g., resolution, codec, bitrate, etc.)
     * @param infoType the type of information about the parameter
     * @param searchType describes where to look for the parameter
     * @return the queries information; or an empty string if there was a problem
     */
    public String get(
            final StreamType streamType,
            final int streamNumber,
            final String parameter,
            final InfoType infoType,
            final InfoType searchType) {
        return getAccessor().get(streamType, streamNumber, parameter, infoType, searchType);
    }

    /**
     * Get a piece of information about a file (parameter is an integer that represents the parameter index).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameterIndex the parameter index that you are looking for in the stream
     * @return a string about information you search, an empty string if there is a problem
     */
    public String get(final StreamType streamType, final int streamNumber, final int parameterIndex) {
        return getAccessor().get(streamType, streamNumber, parameterIndex);
    }

    /**
     * Get a piece of information about a file (parameter is an integer that represents the parameter index).
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @param parameterIndex the parameter index that you are looking for in the stream
     * @param infoType the type of information you want about the parameter (the text, the measure, the help...)
     * @return the information or an empty string if there is a problem
     */
    public String get(
            final StreamType streamType,
            final int streamNumber,
            final int parameterIndex,
            final InfoType infoType) {
        return getAccessor().get(streamType, streamNumber, parameterIndex, infoType);
    }

    /**
     * Gets the number of streams for the given stream type.
     *
     * @param streamType the stream type
     * @return number of streams of the given stream type
     */
    public int getStreamCount(final StreamType streamType) {
        return getAccessor().getStreamCount(streamType);
    }

    /**
     * Gets the number of streams for the given stream type or the total count of information parameters for a stream.
     *
     * @param streamType the stream type
     * @param streamNumber the stream number
     * @return number of streams of the given stream type
     */
    public int getStreamOrParameterCount(final StreamType streamType, final int streamNumber) {
        return getAccessor().getStreamOrParameterCount(streamType, streamNumber);
    }

    /**
     * Gets information about MediaInfo.
     *
     * @param option The name of option
     * @return the option value
     */
    public String getOption(final String option) {
        return getAccessor().getOption(option);
    }

    /**
     * Configures information about MediaInfo.
     *
     * @param option The name of option
     * @param value The value of option
     * @return {@code ""} means no; else, any other value means yes
     */
    public String setOption(final String option, final String value) {
        return getAccessor().setOption(option, value);
    }
}
