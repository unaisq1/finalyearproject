/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2020 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.script;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.InvalidParameterException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.WithConfigsTest;
import org.zaproxy.zap.extension.script.ScriptsCache.Configuration;

/** Unit test for {@link ExtensionScript}. */
class ExtensionScriptUnitTest {

    @BeforeEach
    void setUp() {
        WithConfigsTest.setUpConstantMessages();
    }

    @AfterEach
    void cleanUp() {
        Constant.messages = null;
    }

    @Test
    void shouldCreateScriptsCache() {
        // Given
        ExtensionScript extensionScript = new ExtensionScript();
        @SuppressWarnings("unchecked")
        Configuration<Script> configuration = mock(Configuration.class);
        // When
        ScriptsCache<Script> scriptsCache = extensionScript.createScriptsCache(configuration);
        // Then
        assertThat(scriptsCache, is(not(nullValue())));
    }

    @Test
    void shouldPersistScriptPropertiesWhenItIsEnabled() {
        // Given
        var extensionScript = spy(new ExtensionScript());
        var scriptParam = mock(ScriptParam.class);
        when(extensionScript.getScriptParam()).thenReturn(scriptParam);
        var script = spy(new ScriptWrapper());
        script.setType(new ScriptType("scriptType", "scriptType", null, true));
        script.setEngine(mock(ScriptEngineWrapper.class));
        // When
        extensionScript.setEnabled(script, true);
        // Then
        verify(script).setEnabled(true);
        verify(scriptParam).saveScriptProperties(script);
    }

    @Test
    void shouldReturnEngineWithGivenName() {
        // Given
        ExtensionScript extensionScript = new ExtensionScript();
        ScriptEngineWrapper graalWrapper = mock(ScriptEngineWrapper.class);
        when(graalWrapper.getEngineName()).thenReturn("Test");
        extensionScript.registerScriptEngineWrapper(graalWrapper);

        // When
        ScriptEngineWrapper wrapper = extensionScript.getEngineWrapper("Test");

        // Then
        assertThat(wrapper, is(not(nullValue())));
        assertThat(wrapper.getEngineName(), is("Test"));
    }

    @Test
    void shouldNotReturnEngineWithUnknownName() {
        // Given
        ExtensionScript extensionScript = new ExtensionScript();
        ScriptEngineWrapper graalWrapper = mock(ScriptEngineWrapper.class);
        when(graalWrapper.getEngineName()).thenReturn("Test");
        extensionScript.registerScriptEngineWrapper(graalWrapper);

        // When / Then
        assertThrows(
                InvalidParameterException.class, () -> extensionScript.getEngineWrapper("Unknown"));
    }

    @Test
    void shouldReturnGraalEngineInsteadOfNashorn() {
        // Given
        ExtensionScript extensionScript = new ExtensionScript();
        ScriptEngineWrapper graalWrapper = mock(ScriptEngineWrapper.class);
        when(graalWrapper.getEngineName()).thenReturn("Graal.js");
        extensionScript.registerScriptEngineWrapper(graalWrapper);

        // When
        ScriptEngineWrapper wrapper = extensionScript.getEngineWrapper("Oracle Nashorn");

        // Then
        assertThat(wrapper, is(not(nullValue())));
        assertThat(wrapper.getEngineName(), is("Graal.js"));
    }

    private interface Script {}
}
