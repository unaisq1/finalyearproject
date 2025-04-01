/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.hc.client5.http.impl.cookie;

import java.time.Instant;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hc.client5.http.cookie.CommonCookieAttributeHandler;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieAttributeHandler;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.CookiePriorityComparator;
import org.apache.hc.client5.http.cookie.CookieSpec;
import org.apache.hc.client5.http.cookie.MalformedCookieException;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.http.FormattedHeader;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.message.BufferedHeader;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.CharArrayBuffer;
import org.apache.hc.core5.util.Tokenizer;
import org.apache.hc.core5.util.Tokenizer.Cursor;

/**
 * Forked {@code RFC6265CookieSpec} to behave like browsers do.
 *
 * <p>Cookies are sent mostly as they are received, without removing or attempting to double quote values.
 */
@Contract(threading = ThreadingBehavior.SAFE)
public class RFC6265CookieSpec implements CookieSpec {

    private final static char PARAM_DELIMITER  = ';';
    private final static char EQUAL_CHAR       = '=';

    // IMPORTANT!
    // These private static variables must be treated as immutable and never exposed outside this class
    private static final BitSet TOKEN_DELIMS = Tokenizer.INIT_BITSET(EQUAL_CHAR, PARAM_DELIMITER);
    private static final BitSet VALUE_DELIMS = Tokenizer.INIT_BITSET(PARAM_DELIMITER);

    private final CookieAttributeHandler[] attribHandlers;
    private final Map<String, CookieAttributeHandler> attribHandlerMap;
    private final Tokenizer tokenParser;

    protected RFC6265CookieSpec(final CommonCookieAttributeHandler... handlers) {
        super();
        this.attribHandlers = handlers.clone();
        this.attribHandlerMap = new ConcurrentHashMap<>(handlers.length);
        for (final CommonCookieAttributeHandler handler: handlers) {
            this.attribHandlerMap.put(handler.getAttributeName().toLowerCase(Locale.ROOT), handler);
        }
        this.tokenParser = Tokenizer.INSTANCE;
    }

    static String getDefaultPath(final CookieOrigin origin) {
        String defaultPath = origin.getPath();
        int lastSlashIndex = defaultPath.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            if (lastSlashIndex == 0) {
                //Do not remove the very first slash
                lastSlashIndex = 1;
            }
            defaultPath = defaultPath.substring(0, lastSlashIndex);
        }
        return defaultPath;
    }

    static String getDefaultDomain(final CookieOrigin origin) {
        return origin.getHost();
    }

    @Override
    public final List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(header, "Header");
        Args.notNull(origin, "Cookie origin");
        if (!header.getName().equalsIgnoreCase("Set-Cookie")) {
            throw new MalformedCookieException("Unrecognized cookie header: '" + header + "'");
        }
        final CharArrayBuffer buffer;
        final Tokenizer.Cursor cursor;
        if (header instanceof FormattedHeader) {
            buffer = ((FormattedHeader) header).getBuffer();
            cursor = new Tokenizer.Cursor(((FormattedHeader) header).getValuePos(), buffer.length());
        } else {
            final String s = header.getValue();
            if (s == null) {
                throw new MalformedCookieException("Header value is null");
            }
            buffer = new CharArrayBuffer(s.length());
            buffer.append(s);
            cursor = new Tokenizer.Cursor(0, buffer.length());
        }
        final String name = parseData(buffer, cursor, TOKEN_DELIMS).trim();
        if (cursor.atEnd()) {
            return Collections.emptyList();
        }
        final int valueDelim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (valueDelim != '=') {
            throw new MalformedCookieException("Cookie value is invalid: '" + header + "'");
        }
        final String value = parseData(buffer, cursor, VALUE_DELIMS).trim();
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        final BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setPath(getDefaultPath(origin));
        cookie.setDomain(getDefaultDomain(origin));
        cookie.setCreationDate(Instant.now());

        final Map<String, String> attribMap = new LinkedHashMap<>();
        while (!cursor.atEnd()) {
            final String paramName = tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS)
                    .toLowerCase(Locale.ROOT);
            String paramValue = null;
            if (!cursor.atEnd()) {
                final int paramDelim = buffer.charAt(cursor.getPos());
                cursor.updatePos(cursor.getPos() + 1);
                if (paramDelim == EQUAL_CHAR) {
                    paramValue = tokenParser.parseToken(buffer, cursor, VALUE_DELIMS);
                    if (!cursor.atEnd()) {
                        cursor.updatePos(cursor.getPos() + 1);
                    }
                }
            }
            cookie.setAttribute(paramName, paramValue);
            attribMap.put(paramName, paramValue);
        }
        // Ignore 'Expires' if 'Max-Age' is present
        if (attribMap.containsKey(Cookie.MAX_AGE_ATTR)) {
            attribMap.remove(Cookie.EXPIRES_ATTR);
        }

        for (final Map.Entry<String, String> entry: attribMap.entrySet()) {
            final String paramName = entry.getKey();
            final String paramValue = entry.getValue();
            final CookieAttributeHandler handler = this.attribHandlerMap.get(paramName);
            if (handler != null) {
                handler.parse(cookie, paramValue);
            }
        }

        return Collections.singletonList(cookie);
    }

    @Override
    public final void validate(final Cookie cookie, final CookieOrigin origin)
            throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        for (final CookieAttributeHandler handler: this.attribHandlers) {
            handler.validate(cookie, origin);
        }
    }

    @Override
    public final boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        for (final CookieAttributeHandler handler: this.attribHandlers) {
            if (!handler.match(cookie, origin)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        Args.notEmpty(cookies, "List of cookies");
        final List<? extends Cookie> sortedCookies;
        if (cookies.size() > 1) {
            // Create a mutable copy and sort the copy.
            sortedCookies = new ArrayList<>(cookies);
            sortedCookies.sort(CookiePriorityComparator.INSTANCE);
        } else {
            sortedCookies = cookies;
        }
        final CharArrayBuffer buffer = new CharArrayBuffer(20 * sortedCookies.size());
        buffer.append("Cookie");
        buffer.append(": ");
        for (int n = 0; n < sortedCookies.size(); n++) {
            final Cookie cookie = sortedCookies.get(n);
            if (n > 0) {
                buffer.append(PARAM_DELIMITER);
                buffer.append(' ');
            }
            final String s = cookie.getName();
            if (s != null && !s.isEmpty()) {
                buffer.append(s);
                buffer.append(EQUAL_CHAR);
            }
            buffer.append(cookie.getValue());
        }
        final List<Header> headers = new ArrayList<>(1);
        try {
            headers.add(new BufferedHeader(buffer));
        } catch (final ParseException ignore) {
            // should never happen
        }
        return headers;
    }

    private static String parseData(final CharSequence buf, final Cursor cursor, final BitSet delimiters) {
        final StringBuilder dst = new StringBuilder();
        while (!cursor.atEnd()) {
            if (delimiters != null && delimiters.get(buf.charAt(cursor.getPos()))) {
                break;
            }
            copyData(buf, cursor, delimiters, dst);
        }
        return dst.toString();
    }

    private static void copyData(final CharSequence buf, final Cursor cursor, final BitSet delimiters, final StringBuilder dst) {
        int pos = cursor.getPos();
        final int indexFrom = cursor.getPos();
        final int indexTo = cursor.getUpperBound();
        for (int i = indexFrom; i < indexTo; i++) {
            final char current = buf.charAt(i);
            if ((delimiters != null && delimiters.get(current))) {
                break;
            }
            pos++;
            dst.append(current);
        }
        cursor.updatePos(pos);
    }
}
