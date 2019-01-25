package com.isycat.recog;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class CredProvider implements AWSCredentialsProvider {
    private final Properties creds = new Properties();

    public CredProvider() {
        refresh();
    }

    @Override
    public AWSCredentials getCredentials() {
        return new BasicAWSCredentials(
                creds.getProperty("public-key"),
                creds.getProperty("secret-key")
        );
    }

    @Override
    public void refresh() {
        try {
            creds.load(new FileReader("e:\\aws-creds.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load AWS credentials", e);
        }
    }
}
