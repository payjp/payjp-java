package jp.pay;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class DocumentationTest {

    @Test
    public void testVersionAgreesWithVERSIONFile() throws IOException {
        File versionFile = new File("VERSION").getAbsoluteFile();
        Assert.assertTrue(String.format("Expected VERSION file to exist, but it doesn't. (path is %s).", versionFile.getAbsolutePath()), versionFile.exists());
        Assert.assertTrue(String.format("Expected VERSION to be a file, but it doesn't. (path is %s).", versionFile.getAbsolutePath()), versionFile.isFile());
        BufferedReader reader = new BufferedReader(new FileReader(versionFile));
        String firstLine = reader.readLine();
        Assert.assertEquals(firstLine, Payjp.VERSION);
    }

    @Test
    public void testReadMeContainsMavenPomThatMatches() throws IOException {
        // this will be very flaky, but we want to ensure that the readme is correct.
        File readmeFile = new File("README.md").getAbsoluteFile();
        Assert.assertTrue(String.format("Expected README.md file to exist, but it doesn't. (path is %s).", readmeFile.getAbsolutePath()), readmeFile.exists());
        Assert.assertTrue(String.format("Expected README.md to be a file, but it doesn't. (path is %s).", readmeFile.getAbsolutePath()), readmeFile.isFile());
        BufferedReader reader = new BufferedReader(new FileReader(readmeFile));
        int expectedMentionsOfVersion = 2;
        // Currently two places mention the Payjp version: the sample pom and gradle files.
        String line;
        List<String> mentioningLines = new LinkedList<String>();
        while ((line = reader.readLine()) != null) {
            if (line.contains(Payjp.VERSION)) {
                mentioningLines.add(line);
            }
        }
        String message = String.format("invalid payjp-java version in the Readme");
        Assert.assertSame(message, expectedMentionsOfVersion, mentioningLines.size());
    }

    @Test
    public void testPomContainsVersionThatMatches() throws IOException {
        // we want to ensure that the pom's version matches the static version.
        File readmeFile = new File("pom.xml").getAbsoluteFile();
        Assert.assertTrue(String.format("Expected pom.xml file to exist, but it doesn't. (path is %s).", readmeFile.getAbsolutePath()), readmeFile.exists());
        Assert.assertTrue(String.format("Expected pom.xml to be a file, but it doesn't. (path is %s).", readmeFile.getAbsolutePath()), readmeFile.isFile());
        BufferedReader reader = new BufferedReader(new FileReader(readmeFile));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(Payjp.VERSION)) {
                return;
            }
        }
        Assert.fail(String.format("Expected the Payjp.VERSION (%s) to match up with the one listed in the pom.xml file. It wasn't found.", Payjp.VERSION));
    }
}
