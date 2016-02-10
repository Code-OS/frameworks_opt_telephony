/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.internal.telephony;

import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.test.suitebuilder.annotation.SmallTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

public class Sms7BitEncodingTranslatorTest extends TelephonyTest {

    @Mock
    SmsManager mSmsManager;

    @Before
    public void setUp() throws Exception {
        logd("+Setup!");
        super.setUp(getClass().getSimpleName());
        replaceInstance(SmsManager.class, "sInstance", null, mSmsManager);
        doReturn(false).when(mSmsManager).isImsSmsSupported();
        logd("-Setup!");
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    @SmallTest
    public void testNoTranslate() {
        assertEquals("123", Sms7BitEncodingTranslator.translate("123"));
    }

    @Test
    @SmallTest
    public void testCommonTranslate() {
        String s = null;
        try {
            s = new String(new byte[]{(byte)0x00, (byte)0xD3,
                    (byte)0x00, (byte)0xCF, (byte)0x01, (byte)0x04}, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            fail(e.toString());
        }
        assertEquals("OIA", Sms7BitEncodingTranslator.translate(s));
    }

    @Test
    @SmallTest
    public void testGsmTranslate() {
        String s = null;
        try {
            s = new String(new byte[]{(byte)0x22, (byte)0x1A,
                    (byte)0x21, (byte)0x22, (byte)0x00, (byte)0xE7}, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            fail(e.toString());
        }
        assertEquals("??Ç", Sms7BitEncodingTranslator.translate(s));
    }

    @Test
    @SmallTest
    public void testCdmaTranslate() {

        TelephonyManager telephonyManager = TelephonyManager.from(mContext);
        doReturn(PhoneConstants.PHONE_TYPE_CDMA).when(telephonyManager).getCurrentPhoneType();

        String s = null;
        try {
            s = new String(new byte[]{(byte)0x00, (byte)0xD2,
                    (byte)0x00, (byte)0xD9, (byte)0x00, (byte)0xE7}, "UTF-16");
        } catch (UnsupportedEncodingException e) {
            fail(e.toString());
        }
        assertEquals("OUc", Sms7BitEncodingTranslator.translate(s));
    }
}