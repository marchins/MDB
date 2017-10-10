package GestoreLibreriaLocale;

import Enumerations.*;
import Exceptions.*;
import GestoreFotocamera.GestoreFotocamera;
import GestoreGoogleBooks.GestoreGoogleBooks;
import GestoreLibreriaRemoto.GestoreLibreriaRemotoRemote;
import LogicaDominio.*;
import MultilanguageLabels.*;
import Util.Connection;
import Util.ParametroRicerca;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.FileUtils;

public class GestoreLibreriaLocale {

    private static final EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ClientMDBPU");
    private static final EntityManager em = emf.createEntityManager();

    private static final String SCHEDA_LIBRO
            = "\n" + BookFieldLabels.ISBN + ": %s"
            + "\n" + BookFieldLabels.TITLE_ITA + ": %s"
            + "\n" + BookFieldLabels.AUTHOR_ITA + ": %s"
            + "\n" + BookFieldLabels.PUBLISHER_ITA + ": %s"
            + "\n" + BookFieldLabels.DATE_ITA + ": %s"
            + "\n" + BookFieldLabels.COVER_ITA + ": %s"
            + "\n" + UserCopyFieldLabels.COPY_NUMBER_ITA + ": %s"
            + "\n" + UserCopyFieldLabels.FORMAT_ITA + ": %s"
            + "\n" + UserCopyFieldLabels.READING_STATE_ITA + ": %s"
            + "\n" + UserCopyFieldLabels.RATING_ITA + ": %s"
            + "\n" + UserCopyFieldLabels.LIBRARY_ITA + ": %s"
            + "\n" + UserCopyFieldLabels.POSITION_ITA + ": %s";

    private static final String LIBRERIA_DIGITALE
            = "\n" + BookFieldLabels.TITLE_ITA + ": %s"
            + "\n" + BookFieldLabels.AUTHOR_ITA + ": %s"
            + "\n" + BookFieldLabels.COVER_ITA + ": %s";

    private static final String API_KEY = "AIzaSyC_W66XwLm0yOBP1TOaC53AQ9xCkN1CvHA";
    private static final String URL_RICHIESTA = "https://www.googleapis.com/books/v1/volumes?key=%s&q=%s%s";
    private static final String URL_RICHIESTA_ISBN = "isbn:";
    private static final String URL_RICHIESTA_AUTORE = "inauthor:";
    private static final String URL_RICHIESTA_TITOLO = "intitle:";
    public static final String ISBN = "isbn";
    public static final String TITOLO = "titolo";
    public static final String AUTORE = "autore";

    /* RF-LIB-01: Visualizza libreria digitale. */
    public static void visualizzaLibreriaDigitale() {
        List<CopiaUtente> copie = GestoreLibreriaLocale.getCopieUtente();

        // ordino alfabeticamente
        Collections.sort(copie, new Comparator<CopiaUtente>() {
            @Override
            public int compare(CopiaUtente copia1, CopiaUtente copia2) {
                return copia1.getLibro().getTitolo().compareTo(copia2.getLibro().getTitolo());
            }
        });

        String libreria = "";
        if (copie.isEmpty()) {
            System.out.println(GenericLabels.EMPTY_LIBRARY_ITA);
        } else {
            for (CopiaUtente copia : copie) {
                libreria = libreria + String.format(LIBRERIA_DIGITALE,
                        copia.getLibro().getTitolo(),
                        copia.getLibro().getAutore(),
                        copia.getCopertinaLocale());
            }
            System.out.println(libreria);
        }
    }

    /* RF-LIB-02: Visualizza scheda libro. */
    public static void visualizzaSchedaLibro(CopiaUtente copiaUtente) {
        Libro libro = em.createQuery("SELECT l FROM Libro l WHERE l.isbn='" + copiaUtente.getLibro().getIsbn() + "'", Libro.class).getSingleResult();
        String schedaLibro = String.format(SCHEDA_LIBRO,
                libro.getIsbn(),
                libro.getTitolo(),
                libro.getAutore(),
                libro.getCasaEditrice(),
                libro.getDataPubblicazione(),
                copiaUtente.getCopertinaLocale(),
                copiaUtente.getNumeroCopia(),
                copiaUtente.getFormato(),
                copiaUtente.getStatoLettura().toString(),
                copiaUtente.getValutazione(),
                copiaUtente.getNomelibreria(),
                copiaUtente.getPosizioneNellaLibreria());

        System.out.println(schedaLibro);
    }

    /* RF-LIB-03: Modifica infomazioni soggettive libro. */
    public static void modificaInformazioniSoggettive(CopiaUtente copiaUtente, Formato formato, StatoLettura statoLettura, Valutazione valutazione, String libreria, String posizione) throws ModificaInformazioniException, VincoliInputException, ConnectionErrorException {

        if (Connection.isConnectionAvailable()) {

            if (formato != null) {
                copiaUtente.setFormato(formato);
            }
            if (statoLettura != null) {
                copiaUtente.setStatoLettura(statoLettura);
            }
            if (valutazione != null) {
                copiaUtente.setValutazione(valutazione);
            }
            if (libreria != null) {
                if (libreria.length() > 200) {
                    throw new VincoliInputException(ErrorLabels.LIBRARY_LENGTH_ERROR_ITA);
                }
                copiaUtente.setNomelibreria(libreria);
            }
            if (posizione != null) {
                if (posizione.length() > 100) {
                    throw new VincoliInputException(ErrorLabels.POSITION_LENGTH_ERROR_ITA);
                }
                copiaUtente.setPosizioneNellaLibreria(posizione);
            }

            em.getTransaction().begin();

            em.merge(copiaUtente);

            GestoreLibreriaRemotoRemote gestore = lookupGestoreLibreriaRemotoRemote();
            Autenticazione aut = getAutenticazione();

            if (!gestore.aggiornaCopiaUtenteInRemoto(copiaUtente, getAutenticazione())) {
                em.getTransaction().rollback();
                throw new ModificaInformazioniException();
            } else {
                em.getTransaction().commit();
            }

        } else {
            throw new ConnectionErrorException();
        }
    }

    /* RF-LIB-04: Rimuovi libro. */
    public static void rimuoviLibro(CopiaUtente copiaUtente) throws RimozioneCopiaException, ConnectionErrorException {

        if (Connection.isConnectionAvailable()) {

            GestoreLibreriaRemotoRemote gestore = lookupGestoreLibreriaRemotoRemote();
            if (!gestore.rimuoviCopiaInRemoto(copiaUtente, getAutenticazione())) {
                throw new RimozioneCopiaException();
            }
            em.remove(em.merge(copiaUtente));
            List<CopiaUtente> copieDelLibro = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + copiaUtente.getLibro().getIsbn() + "'", CopiaUtente.class).getResultList();
            // Se era l'unica copia del libro in possesso, allora cancella anche il libro.
            if (copieDelLibro.size() == 1) {
                Libro libroDaRimuovere = em.find(Libro.class, copiaUtente.getLibro().getIsbn());
                em.remove(libroDaRimuovere);
            }
            em.getTransaction().begin();
            em.getTransaction().commit();

        } else {
            throw new ConnectionErrorException();
        }
    }

    /* RF-LIB-05: Ricerca libro nella libreria. */
    public static List<CopiaUtente> ricercaLibroNellaLibreria(String titolo, String autore, Valutazione valutazione, StatoLettura statoLettura, Formato formato, String libreria) throws InputRicercaException {
        String query = formattaQuery(titolo, autore, valutazione, statoLettura, formato, libreria);
        List<CopiaUtente> risultatiRicerca = em.createQuery(query, CopiaUtente.class).getResultList();

        return risultatiRicerca;
    }

    /* RF-RIC-01: Ricerca libro online */
    public static List<Libro> ricercaLibroOnline(String keyword, ParametroRicerca parametro) throws IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        keyword = keyword.replace(" ", "%20");
        String response = "";

        emptyTempFiles();

        if (Connection.isConnectionAvailable()) {
            switch (parametro) {
                case ISBN:
                    String regex = "[0-9]+";
                    if ((keyword.length() == 13 || keyword.length() == 10) && keyword.matches(regex)) {
                        response = GestoreGoogleBooks.richiestaGoogleBooks(String.format(URL_RICHIESTA, API_KEY, URL_RICHIESTA_ISBN, keyword));
                    } else {
                        throw new InputRicercaException(ErrorLabels.ISBN_NOT_VALID_ITA);
                    }
                    break;
                case TITOLO:
                    if (keyword.length() >= 1 && keyword.length() <= 200) {
                        response = GestoreGoogleBooks.richiestaGoogleBooks(String.format(URL_RICHIESTA, API_KEY, URL_RICHIESTA_TITOLO, keyword));
                    } else {
                        throw new InputRicercaException(ErrorLabels.TITLE_LENGTH_ERROR_ITA);
                    }
                    break;
                case AUTORE:
                    if (keyword.length() >= 1 && keyword.length() <= 200) {
                        response = GestoreGoogleBooks.richiestaGoogleBooks(String.format(URL_RICHIESTA, API_KEY, URL_RICHIESTA_AUTORE, keyword));
                    } else {
                        throw new InputRicercaException(ErrorLabels.AUTHOR_LENGTH_ERROR_ITA);
                    }
                    break;
            }
        } else {
            throw new ConnectionErrorException();
        }

        return GestoreGoogleBooks.convertiFormato(response);
    }

    /*RF-RIC-02: Aggiungi libro. */
    public static void aggiungiLibro(Libro libro) throws ConnectionErrorException {

        if (Connection.isConnectionAvailable()) {

            CopiaUtente copiaUtente = new CopiaUtente();
            copiaUtente.setLibro(libro);
            copiaUtente.setFormato(Formato.CARTACEO);
            copiaUtente.setStatoLettura(StatoLettura.NON_SPECIFICATO);
            copiaUtente.setValutazione(Valutazione.NON_VALUTATO);
            // Preleva l'account.
            Account account = em.createQuery("SELECT a FROM Account a", Account.class).getSingleResult();
            copiaUtente.setAccount(account);
            copiaUtente.setNomelibreria(GenericLabels.NOT_SPECIFIED_ITA);
            copiaUtente.setPosizioneNellaLibreria(GenericLabels.NOT_SPECIFIED_ITA);
            copiaUtente.setCopertinaLocale(salvaCopertina(libro.getIsbn()));

            // Verifica se esistono già altre copie dello stesso libro.
            List<Libro> result = em.createQuery("SELECT l FROM Libro l WHERE l.isbn = '" + libro.getIsbn() + "'", Libro.class).getResultList();
            em.getTransaction().begin();

            //libro.getCopieUtente().add(copiaUtente);
            if (result.size() > 0) {
                List<CopiaUtente> copie = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.libro.isbn = '" + libro.getIsbn() + "'", CopiaUtente.class).getResultList();
                copiaUtente.setNumeroCopia(copie.size() + 1);
            } else {
                copiaUtente.setNumeroCopia(1);
                em.persist(libro);
            }

            em.persist(copiaUtente);
            GestoreLibreriaRemotoRemote gestore = lookupGestoreLibreriaRemotoRemote();
            if (!gestore.aggiungiLibroInRemoto(libro, copiaUtente, getAutenticazione())) {
                System.out.println("Errore nell'aggiunta del libro");
                System.exit(0);
            }
            em.getTransaction().commit();

        } else {
            throw new ConnectionErrorException();
        }
    }

    /* RF-RIC-03: Aggiungi libro tramite lettura barcode. */
    public static void aggiungiLibroTramiteLetturaBarcode() throws InterruptedException, ExecutionException, IOException, MalformedURLException, ConnectionErrorException, InputRicercaException {
        String isbn = GestoreFotocamera.acquisisciIsbn();

        Libro libro;
        List<Libro> libri = GestoreLibreriaLocale.ricercaLibroOnline(isbn, ParametroRicerca.ISBN);

        if (libri.size() > 0) {
            libro = libri.get(0);
            GestoreLibreriaLocale.aggiungiLibro(libro);
        } else {
            System.out.println("Libro non trovato");
        }

    }

    /*
     ===========================================================================
     =                           GESTIONE CATEGORIE                            =
     ===========================================================================
     */
    /* RF-CAT-01: Visualizza elenco categorie. */
    public static void visualizzaElencoCategorie() {
        List<Categoria> categorie = getElencoCategorie();
        if (categorie.isEmpty()) {
            System.out.println(GenericLabels.EMPTY_CATEGORIES_ITA);
        } else {
            for (Categoria categoria : categorie) {
                System.out.println(categoria.getNome());
            }
        }
    }

    /* RF-CAT-03: Crea categoria. */
    public static void creaCategoria(String nome) throws CategoriaGiaEsistenteException, CreazioneCategoriaException, ConnectionErrorException {

        if (Connection.isConnectionAvailable()) {

            if (categoriaGiaEsistente(nome)) {
                throw new CategoriaGiaEsistenteException();
            }

            Account account = em.createQuery("SELECT c FROM Account c", Account.class).getSingleResult();
            Categoria categoria = new Categoria();
            categoria.setNome(nome);
            categoria.setAccount(account);
            em.getTransaction().begin();
            em.persist(categoria);

            GestoreLibreriaRemotoRemote gestore = lookupGestoreLibreriaRemotoRemote();

            if (!gestore.aggiungiCategoriaInRemoto(categoria, getAutenticazione())) {
                em.getTransaction().rollback();
                throw new CreazioneCategoriaException();
            }
            em.getTransaction().commit();

        } else {
            throw new ConnectionErrorException();
        }

    }

    /* RF-CAT-04: Rimuovi categoria. */
    public static void rimuoviCategoria(Categoria categoria) throws RimozioneCategoriaException, ConnectionErrorException {

        if (Connection.isConnectionAvailable()) {

            GestoreLibreriaRemotoRemote gestore = lookupGestoreLibreriaRemotoRemote();
            if (!gestore.rimuoviCategoriaInRemoto(categoria, getAutenticazione())) {
                throw new RimozioneCategoriaException();
            }
            em.remove(em.merge(categoria));
            em.getTransaction().begin();
            em.getTransaction().commit();

            System.out.println("Categoria rimossa con successo!");

        } else {
            throw new ConnectionErrorException();
        }
    }

    /* =============== INIZIO METODI PRIVATI ====================== */
    /* Metodo privato per recuperare il contenuto della libreria. */
    private static List<CopiaUtente> getCopieUtente() {
        List<CopiaUtente> copie = em.createQuery("SELECT cu FROM CopiaUtente cu", CopiaUtente.class).getResultList();
        return copie;
    }

    /* Metodo privato che crea e restituisce un oggetto di tipo Autenticazione */
    private static Autenticazione getAutenticazione() {
        Account account = em.createQuery("SELECT c FROM Account c", Account.class).getSingleResult();
        Autenticazione autenticazione = new Autenticazione(account.getUsername(), account.getPassword());
        return autenticazione;
    }

    /* Metodo privato che compone la query sulla base dei parametri specificati */
    private static String formattaQuery(String titolo, String autore, Valutazione valutazione, StatoLettura statoLettura, Formato formato, String libreria) throws InputRicercaException {
        ArrayList<String> strings = new ArrayList<>();
        String queryBase = "SELECT c FROM CopiaUtente c WHERE ";

        if (titolo != null) {
            if (titolo.length() <= 200 && titolo.length() >= 1) {
                String[] titoli = titolo.split(" ");
                for (String t : titoli) {
                    String queryTitolo = "UPPER(c.libro.titolo) LIKE '%" + t.toUpperCase() + "%'";
                    strings.add(queryTitolo);
                }
            } else {
                throw new InputRicercaException(ErrorLabels.TITLE_LENGTH_ERROR_ITA);
            }

        }

        if (autore != null) {
            if (autore.length() <= 200 && autore.length() >= 1) {
                String[] autori = autore.split(" ");
                for (String a : autori) {
                    String queryAutore = "UPPER(c.libro.autore) LIKE '%" + a.toUpperCase() + "%'";
                    strings.add(queryAutore);
                }
            } else {
                throw new InputRicercaException(ErrorLabels.AUTHOR_LENGTH_ERROR_ITA);
            }

        }

        if (valutazione != null) {
            if (valutazione instanceof Valutazione) {
                String queryValutazione = "UPPER(c.valutazione) = '" + valutazione.name() + "'";
                strings.add(queryValutazione);
            } else {
                throw new InputRicercaException(ErrorLabels.RATING_NOT_VALID_ITA);
            }
        }

        if (statoLettura != null) {
            if (statoLettura instanceof StatoLettura) {
                String queryStatoLettura = "UPPER(c.statoLettura) = '" + statoLettura.name() + "'";
                strings.add(queryStatoLettura);
            } else {
                throw new InputRicercaException(ErrorLabels.READING_STATUS_NOT_VALID_ITA);
            }
        }

        if (formato != null) {
            if (formato instanceof Formato) {
                String queryFormato = "UPPER(c.formato) = '" + formato.name() + "'";
                strings.add(queryFormato);
            } else {
                throw new InputRicercaException(ErrorLabels.FORMATO_NOT_VALID_ITA);
            }
        }

        if (libreria != null) {
            if (libreria.length() <= 200 && libreria.length() >= 1) {
                String[] librerie = libreria.split(" ");
                for (String l : librerie) {
                    String queryLibreria = "UPPER(c.nomelibreria) LIKE '%" + l.toUpperCase() + "%'";
                    strings.add(queryLibreria);
                }
            } else {
                throw new InputRicercaException(ErrorLabels.LIBRARY_LENGTH_ERROR_ITA);
            }

        }

        String query = StringUtils.join(strings, " AND ");
        query = queryBase + query;

        return query;
    }

    /* Metodo privato che controlla se una categoria è gia esistente oppure no */
    private static boolean categoriaGiaEsistente(String nome) {
        List<Categoria> result = em.createQuery("SELECT c FROM Categoria c WHERE c.nome = '" + nome + "'", Categoria.class).getResultList();
        return result.size() > 0;
    }

    /* Metodo privato che restituisce la lista di categorie esistenti */
    private static List<Categoria> getElencoCategorie() {
        List<Categoria> result = em.createQuery("SELECT c FROM Categoria c ", Categoria.class).getResultList();
        return result;
    }

    /* Metodo privato per accedere al componente remoto GestoreLibreriaRemto */
    private static GestoreLibreriaRemotoRemote lookupGestoreLibreriaRemotoRemote() {
        try {
            Context c = new InitialContext();
            return (GestoreLibreriaRemotoRemote) c.lookup("java:global/ServerMDB/ServerMDB-ejb/GestoreLibreriaRemoto");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    private static String salvaCopertina(String isbn) {

        File dir = new File("img/tmp");
        String fileToSearch = isbn + ".jpg";
        if (dir.canRead()) {
            for (File temp : dir.listFiles()) {
                if (fileToSearch.equals(temp.getName().toLowerCase())) {
                    temp.renameTo(new File("img/" + temp.getName()));
                    return "img/" + temp.getName();
                }
            }
        }
        return "img/default.jpg";
    }

    private static void emptyTempFiles() {
        try {
            FileUtils.cleanDirectory(new File("img/tmp/"));
        } catch (IOException ex) {
            Logger.getLogger(GestoreLibreriaLocale.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
