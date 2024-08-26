import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;

public class CurrencyConverter {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD"; 

    public static BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) throws IOException {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        JsonNode rates = getExchangeRates();
        BigDecimal fromRate = new BigDecimal(rates.get(fromCurrency).asText());
        BigDecimal toRate = new BigDecimal(rates.get(toCurrency).asText());
        return amount.multiply(toRate).divide(fromRate, 2, BigDecimal.ROUND_HALF_UP);
    }

    private static JsonNode getExchangeRates() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(response.toString());
        return jsonNode.get("rates");
    }
}

