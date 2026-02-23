package payloads;

import java.util.HashMap;
import java.util.Map;

// Classe para construcao dos bodies de POST /products/add
public class ProductsAddPayloads {
    public static Map<String, Object> produtoValido() {
        Map<String, Object> body = new HashMap<>();

        body.put("title", "Perfume Oil");
        body.put("description", "Mega Discount, Impression of A...");
        body.put("price", 13);
        body.put("discountPercentage", 8.4);
        body.put("rating", 4.26);
        body.put("stock", 65);
        body.put("brand", "Impression of Acqua Di Gio");
        body.put("category", "fragrances");
        body.put("thumbnail", "https://i.dummyjson.com/data/products/11/thumnail.jpg");

        return body;
    }

    public static Map<String, Object> produtoSemParametrosEspecificos(String... parametros) {
        Map<String, Object> body = new HashMap<>(produtoValido());

        for (String param : parametros) {
            body.remove(param);
        }

        return body;
    }

    public static Map<String, Object> produtoComParametroAdicional(String chave, Object valor) {
        Map<String, Object> body = new HashMap<>(produtoValido());

        body.put(chave, valor);

        return body;
    }
}
