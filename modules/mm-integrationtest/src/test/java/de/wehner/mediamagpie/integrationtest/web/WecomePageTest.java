package de.wehner.mediamagpie.integrationtest.web;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;

import java.io.IOException;

import net.sourceforge.jwebunit.api.IElement;
import net.sourceforge.jwebunit.htmlunit.HtmlUnitElementImpl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlButton;

import de.wehner.mediamagpie.integrationtest.testsupport.JWebUnitEnvironment;

/**
 * Run with: <code>-Xmx200m -XX:MaxPermSize=120m</code>
 * 
 * @author Ralf Wehner
 *
 */
public class WecomePageTest {

    @Rule
    public JWebUnitEnvironment jWebUnitEnvironment = new JWebUnitEnvironment();

    @Before
    public void prepare() {
    }

    @Test
    public void testDirectLogin() throws IOException {
        beginAt("");
        clickLink("login");
        assertTitleEquals("MediaMagpie - Login");
        // System.out.println(getPageSource());
        setTextField("j_username", "rwe");
        setTextField("j_password", "rwe");
        IElement elementByXPath = getTestingEngine().getElementByXPath("//button[@type='submit']");
        HtmlButton submitButton = (HtmlButton) ((HtmlUnitElementImpl) elementByXPath).getHtmlElement();

        submitButton.click();

        // System.out.println(getPageSource());
        assertTitleEquals("MediaMagpie - Welcome to MediaMagpie");
    }
}
