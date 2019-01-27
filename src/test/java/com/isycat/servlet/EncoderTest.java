package com.isycat.servlet;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class EncoderTest {
    @Test
    public void encodingProofOfConcept() throws DecoderException {
        final String testString = "potato";
        final String encoded = Hex.encodeHexString(testString.getBytes(UTF_8));
        final String decoded = new String(Hex.decodeHex(encoded.toCharArray()), UTF_8);
        assertEquals(testString, decoded);
    }
}
