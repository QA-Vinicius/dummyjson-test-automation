package payloads;

import java.util.HashMap;
import java.util.Map;

// Classe para construcao dos bodies de POST /auth/login
public class AuthPayloads {
    public static Map<String, Object> loginPayload(Object username, String password) {
        Map<String, Object> body = new HashMap<>();

        body.put("username", username);
        body.put("password", password);

        return body;
    }
}
