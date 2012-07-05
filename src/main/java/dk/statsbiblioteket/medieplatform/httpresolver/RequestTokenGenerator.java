package dk.statsbiblioteket.medieplatform.httpresolver;

import java.util.UUID;

public class RequestTokenGenerator {

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
