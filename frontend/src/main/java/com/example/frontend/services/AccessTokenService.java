package com.example.frontend.services;

import com.example.frontend.exceptions.AccessTokenNotFoundException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.crypto.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AccessTokenService {
    private static final String HOME_DIR = System.getProperty("user.home");
    private static final String APP_FOLDER = ".task-wise";
    private static final String TOKEN_FILE_PATH = ".task-wise/access-token";

    public AccessTokenService() {
        try {
            // Create the app folder in the user's home directory => "~/.task-wise"
            Files.createDirectories(Paths.get(HOME_DIR, APP_FOLDER));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Load the access token from the file system. => "~/.task-wise/access-token"
     * @return The access token
     */
    public String loadAccessToken() throws IOException {
        Path accessTokenFile = Paths.get(HOME_DIR, TOKEN_FILE_PATH);

        if (!Files.exists(accessTokenFile)) {
            throw new AccessTokenNotFoundException("Access token not found.");
        }

        return new String(Files.readAllBytes(accessTokenFile));
    }

    /**
     * Save the access token to the file system. => "~/.task-wise/access-token"
     * @param accessToken The access token
     */
    public void saveAccessToken(String accessToken) throws IOException {
        FileWriter writer = new FileWriter(Paths.get(HOME_DIR, TOKEN_FILE_PATH).toString());
        writer.write(accessToken);
        writer.close();
    }

    /**
     * Generate the Google OAuth2 authorization URL.
     * @return The authorization URL
     */
    public String generateAuthUrl() throws MalformedURLException {
        final String CLIENT_ID = "999379647431-7a0u8oot8ta7r0snhqq5f1tv9pvkpjiq.apps.googleusercontent.com";
        final String REDIRECT_URI = "http://localhost:5000/authorize";

        ArrayList<String> scopes = new ArrayList<>();
        scopes.add("https://www.googleapis.com/auth/userinfo.profile");
        scopes.add("https://www.googleapis.com/auth/calendar");
        scopes.add("https://www.googleapis.com/auth/contacts");
        scopes.add("https://www.googleapis.com/auth/drive");

        URL authUrl = new URL("https://accounts.google.com/o/oauth2/v2/auth");
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("scope", String.join(" ", scopes));
        queryParams.put("response_type", "code");
        queryParams.put("redirect_uri", REDIRECT_URI);
        queryParams.put("client_id", CLIENT_ID);

        String params = String.join("&", queryParams.entrySet().stream().map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)).toArray(String[]::new));
        return authUrl + "?" + params;
    }

    /**
     * Verify the access token using the Google OAuth2 tokeninfo endpoint.
     * @param accessToken The access token
     * @return Whether the access token is valid
     */
    public boolean verifyAccessToken(String accessToken) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + accessToken);
        HttpResponse response = client.execute(request);

        return response.getStatusLine().getStatusCode() == 200;
    }
}
