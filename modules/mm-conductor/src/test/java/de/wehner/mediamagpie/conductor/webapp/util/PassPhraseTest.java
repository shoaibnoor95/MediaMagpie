package de.wehner.mediamagpie.conductor.webapp.util;

import static org.fest.assertions.Assertions.*;

import org.junit.Test;

public class PassPhraseTest {

    @Test
    public void test() {
        PassPhrase passPhrase = new PassPhrase();
        String pharse1 = passPhrase.getNext();
        System.out.println(pharse1);
        assertThat(pharse1).hasSize(6);
    }
}
