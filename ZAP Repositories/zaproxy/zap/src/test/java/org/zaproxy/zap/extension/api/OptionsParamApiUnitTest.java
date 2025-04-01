/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2016 The ZAP Development Team
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
package org.zaproxy.zap.extension.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.jupiter.api.Test;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.utils.ZapXmlConfiguration;

/** Unit test for {@link OptionsParamApi}. */
class OptionsParamApiUnitTest {

    @Test
    void shouldNotHaveConfigByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.getConfig(), is(equalTo(null)));
    }

    @Test
    void shouldHaveEnabledStateByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isEnabled(), is(equalTo(true)));
    }

    @Test
    void shouldFailToSetEnabledStateWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setEnabled(true));
    }

    @Test
    void shouldSetEnabledStateWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setEnabled(false);
        // Then
        assertThat(param.isEnabled(), is(equalTo(false)));
        assertThat(param.getConfig().getBoolean(OptionsParamApi.ENABLED), is(equalTo(false)));
    }

    @Test
    void shouldHaveSecureOnlyDisabledByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isSecureOnly(), is(equalTo(false)));
    }

    @Test
    void shouldFailToSetSecureOnlyWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setSecureOnly(true));
    }

    @Test
    void shouldSetSecureOnlyWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setSecureOnly(true);
        // Then
        assertThat(param.isSecureOnly(), is(equalTo(true)));
        assertThat(param.getConfig().getBoolean(OptionsParamApi.SECURE_ONLY), is(equalTo(true)));
    }

    @Test
    void shouldHaveKeyEnabledByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isDisableKey(), is(equalTo(false)));
    }

    @Test
    void shouldFailToSetDisableKeyWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setDisableKey(true));
    }

    @Test
    void shouldSetDisableKeyWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setDisableKey(true);
        // Then
        assertThat(param.isEnabled(), is(equalTo(true)));
        assertThat(param.getConfig().getBoolean(OptionsParamApi.DISABLE_KEY), is(equalTo(true)));
    }

    @Test
    void shouldHaveIncErrorDetailsDisabledByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isIncErrorDetails(), is(equalTo(false)));
    }

    @Test
    void shouldFailToSetIncErrorDetailsWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setIncErrorDetails(true));
    }

    @Test
    void shouldSetIncErrorDetailsWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setIncErrorDetails(true);
        // Then
        assertThat(param.isIncErrorDetails(), is(equalTo(true)));
        assertThat(
                param.getConfig().getBoolean(OptionsParamApi.INC_ERROR_DETAILS), is(equalTo(true)));
    }

    @Test
    void shouldHaveAutofillKeyDisabledByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isAutofillKey(), is(equalTo(false)));
    }

    @Test
    void shouldFailToSetAutofillKeyWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setAutofillKey(true));
    }

    @Test
    void shouldSetAutofillKeyWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setAutofillKey(true);
        // Then
        assertThat(param.isAutofillKey(), is(equalTo(true)));
        assertThat(param.getConfig().getBoolean(OptionsParamApi.AUTOFILL_KEY), is(equalTo(true)));
    }

    @Test
    void shouldHaveEnableJSONPDisabledByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isEnableJSONP(), is(equalTo(false)));
    }

    @Test
    void shouldFailToSetEnableJSONPWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setEnableJSONP(true));
    }

    @Test
    void shouldSetEnableJSONPWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setEnableJSONP(true);
        // Then
        assertThat(param.isEnableJSONP(), is(equalTo(true)));
        assertThat(param.getConfig().getBoolean(OptionsParamApi.ENABLE_JSONP), is(equalTo(true)));
    }

    @Test
    void shouldHaveReportPermErrorsDisabledByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isReportPermErrors(), is(equalTo(false)));
    }

    @Test
    void shouldFailToSetReportPermErrorsWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setReportPermErrors(true));
    }

    @Test
    void shouldSetReportPermErrorsWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setReportPermErrors(true);
        // Then
        assertThat(param.isReportPermErrors(), is(equalTo(true)));
        assertThat(
                param.getConfig().getBoolean(OptionsParamApi.REPORT_PERM_ERRORS),
                is(equalTo(true)));
    }

    @Test
    void shouldHaveNonceTimeToLiveInSecsSetTo5MinsByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.getNonceTimeToLiveInSecs(), is(equalTo(5 * 60)));
    }

    @Test
    void shouldHaveNoKeyForViewsOrSafeOthersDisabledByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.isNoKeyForSafeOps(), is(equalTo(false)));
    }

    @Test
    void shouldFailToSetNoKeyForViewsOrSafeOthersWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setNoKeyForSafeOps(true));
    }

    @Test
    void shouldSetNoKeyForViewsOrSafeOthersWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setNoKeyForSafeOps(true);
        // Then
        assertThat(param.isNoKeyForSafeOps(), is(equalTo(true)));
        assertThat(
                param.getConfig().getBoolean(OptionsParamApi.NO_KEY_FOR_SAFE_OPS),
                is(equalTo(true)));
    }

    @Test
    void shouldHaveEmptyRealKeyByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.getRealKey(), is(emptyString()));
    }

    @Test
    void shouldHaveGeneratedKeyByDefault() {
        // Given / When
        OptionsParamApi param = new OptionsParamApi();
        // Then
        assertThat(param.getKey(), is(not(equalTo(""))));
    }

    @Test
    void shouldFailToSetKeyWithoutConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When / Then
        assertThrows(NullPointerException.class, () -> param.setKey(""));
    }

    @Test
    void shouldSetKeyWithConfig() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        String apiKey = "Key";
        // When
        param.setKey(apiKey);
        // Then
        assertThat(param.getKey(), is(equalTo(apiKey)));
        assertThat(param.getConfig().getString(OptionsParamApi.API_KEY), is(equalTo(apiKey)));
    }

    @Test
    void shouldSaveGeneratedKeyWithConfig() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        Configuration conf = new Configuration();
        param.load(conf);
        param.setKey(null);
        // When
        String key = param.getKey();
        // Then
        assertThat(key, is(not(equalTo(""))));
        assertThat(conf.getString(OptionsParamApi.API_KEY), is(equalTo(key)));
        assertThat(conf.isSaved(), is(equalTo(true)));
    }

    @Test
    void shouldReturnEmptyKeyIfKeyDisabled() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        param.setDisableKey(true);
        param.setKey("Key");
        // When
        String key = param.getKey();
        // Then
        assertThat(key, is(equalTo("")));
        assertThat(param.getRealKey(), is(equalTo("Key")));
    }

    @Test
    void shouldParseLoadedFileConfiguration() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        FileConfiguration config = createTestConfig();
        // When
        param.load(config);
        // Then
        assertThat(param.isEnabled(), is(equalTo(false)));
        assertThat(param.isSecureOnly(), is(equalTo(true)));
        assertThat(param.isDisableKey(), is(equalTo(true)));
        assertThat(param.isIncErrorDetails(), is(equalTo(true)));
        assertThat(param.isAutofillKey(), is(equalTo(true)));
        assertThat(param.isEnableJSONP(), is(equalTo(true)));
        assertThat(param.getRealKey(), is(equalTo("ApiKey")));
        assertThat(param.isFileTransferAllowed(), is(equalTo(false)));
        assertThat(param.getTransferDir(), is(equalTo("/tmp")));
    }

    @Test
    void shouldBeCloneableByDefault() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When
        OptionsParamApi clone = param.clone();
        // Then
        assertThat(clone, is(notNullValue()));
        assertThat(param.isEnabled(), is(equalTo(true)));
        assertThat(param.isSecureOnly(), is(equalTo(false)));
        assertThat(param.isDisableKey(), is(equalTo(false)));
        assertThat(param.isIncErrorDetails(), is(equalTo(false)));
        assertThat(param.isAutofillKey(), is(equalTo(false)));
        assertThat(param.isEnableJSONP(), is(equalTo(false)));
        assertThat(param.getRealKey(), is(equalTo("")));
        assertThat(param.isFileTransferAllowed(), is(equalTo(false)));
    }

    @Test
    void shouldHaveLoadedConfigsAfterCloning() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        FileConfiguration config = createTestConfig();
        param.load(config);
        // When
        OptionsParamApi clone = param.clone();
        // Then
        assertThat(clone, is(notNullValue()));
        assertThat(param.isEnabled(), is(equalTo(false)));
        assertThat(param.isSecureOnly(), is(equalTo(true)));
        assertThat(param.isDisableKey(), is(equalTo(true)));
        assertThat(param.isIncErrorDetails(), is(equalTo(true)));
        assertThat(param.isAutofillKey(), is(equalTo(true)));
        assertThat(param.isEnableJSONP(), is(equalTo(true)));
        assertThat(param.getRealKey(), is(equalTo("ApiKey")));
        assertThat(param.isFileTransferAllowed(), is(equalTo(false)));
        assertThat(param.getTransferDir(), is(equalTo("/tmp")));
    }

    @Test
    void shouldUseDefaultValuesWhenLoadingFileConfigurationWithInvalidValues() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        FileConfiguration config = createTestConfigWithInvalidValues();
        // When
        param.load(config);
        // Then
        assertThat(param.isEnabled(), is(equalTo(true)));
        assertThat(param.isSecureOnly(), is(equalTo(false)));
        assertThat(param.isDisableKey(), is(equalTo(false)));
        assertThat(param.isIncErrorDetails(), is(equalTo(false)));
        assertThat(param.isAutofillKey(), is(equalTo(false)));
        assertThat(param.isEnableJSONP(), is(equalTo(false)));
        assertThat(param.getRealKey(), is(equalTo("")));
        assertThat(param.isFileTransferAllowed(), is(equalTo(false)));
        assertThat(
                param.getTransferDir(),
                is(equalTo(new File(Constant.getZapHome(), "transfer").getAbsolutePath())));
    }

    @Test
    void shouldDefaultToNoPersistentCallbacks() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        // When
        param.load(new ZapXmlConfiguration());
        // Then
        assertThat(param.getPersistentCallBacks().size(), is(equalTo(0)));
    }

    @Test
    void shouldLoadPersistentCallbacks() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        ZapXmlConfiguration config = new ZapXmlConfiguration();
        String url1 = "https://zap//zapCallBackUrl/1234";
        String url2 = "https://zap//zapCallBackUrl/5678";
        config.setProperty("api.callbacks.callback(0).url", url1);
        config.setProperty("api.callbacks.callback(0).prefix", "test1");
        config.setProperty("api.callbacks.callback(1).url", url2);
        config.setProperty("api.callbacks.callback(1).prefix", "test2");
        // When
        param.load(config);
        // Then
        assertThat(param.getPersistentCallBacks().size(), is(equalTo(2)));
        Map<String, String> map = param.getPersistentCallBacks();
        assertThat(map.get(url1), is(equalTo("test1")));
        assertThat(map.get(url2), is(equalTo("test2")));
    }

    @Test
    void shouldSavePersistentCallbacks() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        String url1 = "https://zap//zapCallBackUrl/1234";
        String url2 = "https://zap//zapCallBackUrl/5678";
        // When
        ZapXmlConfiguration config = new ZapXmlConfiguration();
        param.load(config);
        param.addPersistantCallBack(url1, "test1");
        param.addPersistantCallBack(url2, "test2");
        List<HierarchicalConfiguration> fields =
                ((HierarchicalConfiguration) config).configurationsAt(OptionsParamApi.CALLBACK_KEY);
        Map<String, String> confMap = new HashMap<>();
        for (HierarchicalConfiguration sub : fields) {
            confMap.put(sub.getString(".url", ""), sub.getString(".prefix", ""));
        }

        // Then
        Map<String, String> cbMap = param.getPersistentCallBacks();
        assertThat(cbMap.size(), is(equalTo(2)));
        assertThat(cbMap.get(url1), is(equalTo("test1")));
        assertThat(cbMap.get(url2), is(equalTo("test2")));
        assertThat(fields.size(), is(equalTo(2)));
        assertThat(confMap.size(), is(equalTo(2)));
        assertThat(confMap.get(url1), is(equalTo("test1")));
        assertThat(confMap.get(url2), is(equalTo("test2")));
        assertThat(config.getProperty("api.callbacks.callback(0).url"), is(equalTo(url2)));
        assertThat(config.getProperty("api.callbacks.callback(0).prefix"), is(equalTo("test2")));
        assertThat(config.getProperty("api.callbacks.callback(1).url"), is(equalTo(url1)));
        assertThat(config.getProperty("api.callbacks.callback(1).prefix"), is(equalTo("test1")));
    }

    @Test
    void shouldRemovePersistentCallbacks() {
        // Given
        OptionsParamApi param = new OptionsParamApi();
        ZapXmlConfiguration config = new ZapXmlConfiguration();
        String url = "https://zap//zapCallBackUrl/1234";
        config.setProperty(OptionsParamApi.CALLBACK_KEY + ".url", url);
        config.setProperty(OptionsParamApi.CALLBACK_KEY + ".prefix", "test");
        // When
        param.load(config);
        String val1 = param.removePersistantCallBack(url);
        // Do it twice just to check it handles unused keys
        String val2 = param.removePersistantCallBack(url);
        // Then
        assertThat(param.getPersistentCallBacks().size(), is(equalTo(0)));
        assertThat(val1, is(equalTo("test")));
        assertThat(val2, is(equalTo(null)));
    }

    @Test
    void shouldNotSetFileTransferIfNoApiKey() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setDisableKey(true);
        param.setFileTransferAllowed(true);
        // Then
        assertThat(param.isDisableKey(), is(equalTo(true)));
        assertThat(param.isFileTransferAllowed(), is(equalTo(false)));
        assertThat(
                param.getConfig().containsKey(OptionsParamApi.FILE_TRANSFER), is(equalTo(false)));
    }

    @Test
    void shouldSetFileTransferIfApiKey() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        param.setDisableKey(false);
        param.setFileTransferAllowed(true);
        // Then
        assertThat(param.isDisableKey(), is(equalTo(false)));
        assertThat(param.isFileTransferAllowed(), is(equalTo(true)));
        assertThat(param.getConfig().getBoolean(OptionsParamApi.FILE_TRANSFER), is(equalTo(true)));
    }

    @Test
    void shouldSetTransferDirectory() {
        // Given
        OptionsParamApi param = createOptionsParamApiWithConfig();
        // When
        String dir = "/test/dir";
        param.setTransferDir(dir);
        // Then
        assertThat(param.getTransferDir(), is(equalTo(dir)));
        assertThat(param.getConfig().getString(OptionsParamApi.TRANSFER_DIR), is(equalTo(dir)));
    }

    private static OptionsParamApi createOptionsParamApiWithConfig() {
        OptionsParamApi param = new OptionsParamApi();
        param.load(new ZapXmlConfiguration());
        return param;
    }

    private static FileConfiguration createTestConfig() {
        ZapXmlConfiguration config = new ZapXmlConfiguration();
        config.setProperty(OptionsParamApi.ENABLED, "false");
        config.setProperty(OptionsParamApi.SECURE_ONLY, "true");
        config.setProperty(OptionsParamApi.API_KEY, "ApiKey");
        config.setProperty(OptionsParamApi.DISABLE_KEY, "true");
        config.setProperty(OptionsParamApi.INC_ERROR_DETAILS, "true");
        config.setProperty(OptionsParamApi.AUTOFILL_KEY, "true");
        config.setProperty(OptionsParamApi.ENABLE_JSONP, "true");
        config.setProperty(OptionsParamApi.NO_KEY_FOR_SAFE_OPS, "true");
        config.setProperty(OptionsParamApi.REPORT_PERM_ERRORS, "true");
        config.setProperty(OptionsParamApi.FILE_TRANSFER, "false");
        config.setProperty(OptionsParamApi.TRANSFER_DIR, "/tmp");
        return config;
    }

    private static FileConfiguration createTestConfigWithInvalidValues() {
        ZapXmlConfiguration config = new ZapXmlConfiguration();
        config.setProperty(OptionsParamApi.ENABLED, "Not Boolean");
        config.setProperty(OptionsParamApi.SECURE_ONLY, "Not Boolean");
        config.setProperty(OptionsParamApi.DISABLE_KEY, "Not Boolean");
        config.setProperty(OptionsParamApi.INC_ERROR_DETAILS, "Not Boolean");
        config.setProperty(OptionsParamApi.AUTOFILL_KEY, "Not Boolean");
        config.setProperty(OptionsParamApi.ENABLE_JSONP, "Not Boolean");
        config.setProperty(OptionsParamApi.NO_KEY_FOR_SAFE_OPS, "Not Boolean");
        config.setProperty(OptionsParamApi.REPORT_PERM_ERRORS, "Not Boolean");
        config.setProperty(OptionsParamApi.FILE_TRANSFER, "Not Boolean");
        return config;
    }

    private static class Configuration extends ZapXmlConfiguration {

        private static final long serialVersionUID = 3822957830178644758L;

        private boolean saved;

        @Override
        public void save() throws ConfigurationException {
            saved = true;
        }

        boolean isSaved() {
            return saved;
        }
    }
}
