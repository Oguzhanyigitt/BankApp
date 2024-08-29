
/*import com.fasterxml.jackson.databind.JsonNode;
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
*/

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CurrencyConverter {

    private static final String EXCHANGE_RATE_XML_URL = "https://www.tcmb.gov.tr/kurlar/today.xml";
    private static Map<String, BigDecimal> exchangeRates = new HashMap<>();

    static {
        try {
            loadExchangeRates();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadExchangeRates() throws IOException {
        try {
            URL url = new URL(EXCHANGE_RATE_XML_URL);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());

            NodeList currencyNodes = document.getElementsByTagName("Currency");
            if (!exchangeRates.containsKey("TRY")) {
                exchangeRates.put("TRY", BigDecimal.ONE);
                System.out.println("Eklenen TRY rate: " + BigDecimal.ONE);
            }
            for (int i = 0; i < currencyNodes.getLength(); i++) {
                Element currencyElement = (Element) currencyNodes.item(i);
                String code = currencyElement.getAttribute("CurrencyCode");
                String rateString = currencyElement.getElementsByTagName("BanknoteSelling").item(0).getTextContent();
                BigDecimal rate = new BigDecimal(rateString.replace(",", "."));
                exchangeRates.put(code, rate);
                System.out.println("Eklenen Rate " + code + ": " + rate);
            }

            

        } catch (Exception e) {
            throw new IOException("Failed to load exchange rates", e);
        }
    }

    public static BigDecimal getExchangeRate(String currencyCode) {
        return exchangeRates.get(currencyCode.toUpperCase());
    }

    public static BigDecimal convertAmount(BigDecimal amount, String fromCurrency, String toCurrency) throws IOException {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        if (fromCurrency.equals("TRY")) {
            BigDecimal rate = exchangeRates.get(toCurrency);
            if (rate == null) {
                throw new IOException("Bu birim için kur bilgisi bulunamadı: " + toCurrency);
            }
            return amount.divide(rate, BigDecimal.ROUND_HALF_UP);
        } else if (toCurrency.equals("TRY")) {
            BigDecimal rate = exchangeRates.get(fromCurrency);
            if (rate == null) {
                throw new IOException("Bu birim için kur bilgisi bulunamadı: " + fromCurrency);
            }
            return amount.multiply(rate);
        } else {
            throw new IOException("Var olmayan kur: " + toCurrency);
        }
    }
}
