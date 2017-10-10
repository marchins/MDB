package GestoreAccountRemoto;

import LogicaDominio.Account;
import LogicaDominio.Amico;
import LogicaDominio.Autenticazione;
import LogicaDominio.Categoria;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Stateless
public class GestoreAccountRemoto implements GestoreAccountRemotoRemote {

    /* Metodo che verifica che lo username non sia già in uso e in caso negativo persiste l'account creato */
    @Override
    public Boolean verificaDatiAccount(String username, String password, String nome, String cognome, String email, String dataNascita) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        Account account = new Account();
        List<Account> accounts = (List<Account>) em.createQuery("SELECT a FROM Account a WHERE a.username = '" + username + "'", Account.class).getResultList();

        if (accounts.size() > 0) {
            em.close();
            return false;
        }

        account.setNome(nome);
        account.setCognome(cognome);
        account.setUsername(username);
        account.setPassword(password);
        account.setEmail(email);
        account.setDataNascita(dataNascita);
        em.getTransaction().begin();
        em.persist(account);
        em.getTransaction().commit();
        em.close();
        return true;
    }

    /* Metodo che verifica la correttezzaz dei dati inseriti in fase di login */
    @Override
    public Account verificaDatiLogin(String username, String password) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        List<Account> accounts = (List<Account>) em.createQuery(
                "SELECT a "
                + "FROM Account a "
                + "WHERE a.username = '" + username + "' AND a.password = '" + password + "'",
                Account.class).getResultList();
        if (accounts.size() == 1) {
            return accounts.get(0);
        }
        em.close();

        return null;
    }

    /* Metodo utilizzato nella sincronizzazione, che recupera tutti i dati di un utente presenti nel database remoto */
    @Override
    public HashMap<String, Vector<Object>> sincronizzaDatiDaRemoto(Autenticazione autenticazione) {

        HashMap<String, Vector<Object>> datiUtente = new HashMap<>();

        datiUtente.put("categorie", (Vector<Object>) (Vector<?>) getCategorie(autenticazione.getUsername()));
        datiUtente.put("copieutente", (Vector<Object>) (Vector<?>) getCopieUtente(autenticazione.getUsername()));
        datiUtente.put("libri", (Vector<Object>) (Vector<?>) getLibri(autenticazione.getUsername()));
        datiUtente.put("amici", (Vector<Object>) (Vector<?>) getRubrica(autenticazione.getUsername()));

        return datiUtente;
    }

    /* =============== INIZIO METODI PRIVATI ======================*/
    /* Metodo privato che restituisce una lista di oggetti Libro di un dato utente */
    private List<Libro> getLibri(String username) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();

        List<Libro> libri = em.createQuery("SELECT l FROM Libro l, CopiaUtente c WHERE l.isbn = c.libro.isbn AND c.account.username = '" + username + "'", Libro.class).getResultList();

        em.close();

        return libri;
    }

    /* Metodo privato che restituisce una lista di oggetti CopiaUtente di un dato utente */
    private List<CopiaUtente> getCopieUtente(String username) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();

        List<CopiaUtente> copieUtente = em.createQuery("SELECT c FROM CopiaUtente c WHERE c.account.username = '" + username + "'", CopiaUtente.class).getResultList();

        em.close();

        return copieUtente;
    }

    /* Metodo privato che restituisce una lista di oggetti Categoria di un dato utente */
    private List<Categoria> getCategorie(String username) {

        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();

        List<Categoria> categorie = em.createQuery("SELECT c FROM Categoria c WHERE c.account.username = '" + username + "'", Categoria.class).getResultList();

        em.close();

        return categorie;
    }

    /* Metodo privato che restituisce una lista di oggetti Amico di un dato utente */
    private List<Amico> getRubrica(String username) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();

        List<Amico> amici = em.createQuery("SELECT a FROM Amico a WHERE a.account.username = '" + username + "'", Amico.class).getResultList();

        em.close();

        return amici;
    }

}
