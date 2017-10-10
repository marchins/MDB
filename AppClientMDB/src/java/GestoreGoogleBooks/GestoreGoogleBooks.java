package GestoreGoogleBooks;

import LogicaDominio.Libro;
import Util.TrustManager;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class GestoreGoogleBooks {

    private static final String DEFAULT_COVER = "img/default.jpg";
    private static final String INFO_MANCANTE = "Informazione mancante";
    
    /* Metodo che efettua la richiesta https e restituisce la risposta */
    public static String richiestaGoogleBooks(String urlRichiesta) throws MalformedURLException, IOException {
        //configuro trustmanager per accettare certificati SSL
        TrustManager.initTrustManager();

        //compongo URL ed effettuo richiesta http
        URL googleBooks = new URL(urlRichiesta);
        HttpURLConnection con = (HttpURLConnection) googleBooks.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        String response = "";

        //ottengo risposta http
        while ((inputLine = in.readLine()) != null) {
            response = response + inputLine;
        }

        return response;
    }

    /* Metodo privato che converte la stringa di risposta http in una lista di oggetti Libro */
    public static List<Libro> convertiFormato(String responseGoogleBooks) {
        List<Libro> libriConvertiti = new ArrayList<>();

        //converto la risposta http di google in un oggetto json
        JSONObject json = (JSONObject) JSONSerializer.toJSON(responseGoogleBooks);

        //controllo che il json contenga almeno un libro
        if (json.containsKey("items") && Integer.parseInt(json.get("totalItems").toString()) > 0) {
            JSONArray risultatiRicercaGoogleBooks = json.getJSONArray("items");

            //scorro gli items (libri) contenuti nel json
            for (Object object : risultatiRicercaGoogleBooks) {

                JSONObject volumeInfo = ((JSONObject) object).getJSONObject("volumeInfo");

                JSONArray identifiers = (JSONArray) ((JSONObject) volumeInfo).get("industryIdentifiers");
                String isbn = "";
                String titolo = "";
                String autori = "";
                String dataPubblicazione = "";
                String casaEditrice = "";
                String urlCopertina = "";
                String pathCopertina = "";
                for (Object obj : identifiers) {
                    if (((JSONObject) obj).getString("type").equals("ISBN_13")) {
                        isbn = ((JSONObject) obj).getString("identifier");
                        //controllo presenza del campo title
                        titolo = (((JSONObject) volumeInfo).containsKey("title") ? ((JSONObject) volumeInfo).getString("title") : INFO_MANCANTE);

                        //controllo presenza del campo authors
                        autori = (((JSONObject) volumeInfo).containsKey("authors") ? ((JSONObject) volumeInfo).getString("authors") : INFO_MANCANTE);
                        autori = autori.replace("\"", "");
                        autori = autori.replace("[", "");
                        autori = autori.replace("]", "");

                        //controllo presenza del campo publishedDate
                        dataPubblicazione = (((JSONObject) volumeInfo).containsKey("publishedDate") ? ((JSONObject) volumeInfo).getString("publishedDate") : INFO_MANCANTE);

                        //controllo presenza del campo publisher
                        casaEditrice = (((JSONObject) volumeInfo).containsKey("publisher") ? ((JSONObject) volumeInfo).getString("publisher") : INFO_MANCANTE);

                        //controllo presenza del campo imageLinks e scarico copertina
                        if (((JSONObject) volumeInfo).containsKey("imageLinks")) {
                            urlCopertina = ((JSONObject) volumeInfo).getJSONObject("imageLinks").getString("thumbnail");
                            //TODO: il download della copertina va fatto solo quando il libro viene aggiunto, non per tutti i libri ricercati!
                            pathCopertina = GestoreGoogleBooks.downloadCopertina(urlCopertina, isbn);
                        } else {
                            pathCopertina = INFO_MANCANTE;
                        }
                    }
                }

                //creo oggetto Libro e lo aggiungo alla lista
                Libro libro = new Libro(isbn, titolo, autori, casaEditrice, dataPubblicazione, urlCopertina);
                libriConvertiti.add(libro);
            }
        }
        return libriConvertiti;
    }

    /* Metodo privato che scarica in locale la copertina di un libro */
    private static String downloadCopertina(String urlCopertina, String isbn) {

        String path = "img/tmp/";
        String fileExtension = "jpg";
        String result = "";

        try {
            URL url = new URL(urlCopertina);
            URLConnection urlConn = url.openConnection();

            //simulo intestazione browser per la richiesta http
            urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.21; Mac_PowerPC)");
            urlConn.connect();

            //ottengo lo stream
            InputStream urlStream = urlConn.getInputStream();

            //genero immagine
            BufferedImage b = ImageIO.read(urlStream);

            //salvo immagine nel path predefinito
            File file = new File(path + isbn + "." + fileExtension);

            if (ImageIO.write(b, fileExtension, file)) {
                if (file.exists()) {
                    result = file.getPath();
                }
            }
        } catch (IOException e) {
        }
        return result;
    }

}
