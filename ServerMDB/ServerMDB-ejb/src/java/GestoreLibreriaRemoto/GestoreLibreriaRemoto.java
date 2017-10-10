package GestoreLibreriaRemoto;

import LogicaDominio.Account;
import LogicaDominio.Autenticazione;
import LogicaDominio.Categoria;
import LogicaDominio.CopiaUtente;
import LogicaDominio.Libro;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.UnitOfWork;

@Stateless
public class GestoreLibreriaRemoto implements GestoreLibreriaRemotoRemote {

    /* Metodo utilizzato da RF-LIB-03 per modificare in remoto le informazioni relative a una copia utente. */
    @Override
    public Boolean aggiornaCopiaUtenteInRemoto(CopiaUtente copiaUtente, Autenticazione autenticazione) {
        if (checkAutenticazione(autenticazione)) {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            Session session = ((EntityManagerImpl) em).getActiveSession();
            UnitOfWork uow = session.acquireUnitOfWork();

            uow.mergeCloneWithReferences(copiaUtente);
            uow.commit();
            em.getTransaction().commit();

            em.close();
            return true;
        }
        return false;
    }

    /* Metodo utilizzato da RF-LIB-04 per rimuovere in remoto una copia utente. */
    @Override
    public Boolean rimuoviCopiaInRemoto(CopiaUtente copiaUtente, Autenticazione autenticazione) {
        if (checkAutenticazione(autenticazione)) {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.remove(em.merge(copiaUtente));
            em.getTransaction().commit();
            em.close();
            return true;
        }
        return false;
    }

    /* Metodo utilizzato da RF-RIC-02 per aggiungere in remoto una copia utente. */
    @Override
    public Boolean aggiungiLibroInRemoto(Libro libro, CopiaUtente copiaUtente, Autenticazione autenticazione) {
        if (checkAutenticazione(autenticazione)) {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            // Se è la prima volta che viene aggiunto il libro, persistilo.
            if (!libroGiaPresenteNellaCollezione(libro)) {
                em.persist(libro);
            }

            // Persisti la copia.
            em.persist(copiaUtente);
            em.getTransaction().commit();
            em.close();
            return true;
        }
        return false;
    }

    /* Metodo utilizzato da RF-CAT-03 per aggiungere una categoria in remoto. */
    @Override
    public Boolean aggiungiCategoriaInRemoto(Categoria categoria, Autenticazione autenticazione) {
        if (checkAutenticazione(autenticazione)) {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(categoria);
            em.getTransaction().commit();
            em.close();
            return true;
        }
        return false;
    }

    /* Metodo utilizzato da RF-CAT-04 per rimuovere una categoria in remoto. */
    @Override
    public Boolean rimuoviCategoriaInRemoto(Categoria categoria, Autenticazione autenticazione) {
        if (checkAutenticazione(autenticazione)) {
            EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.remove(em.merge(categoria));
            em.getTransaction().commit();
            em.close();
            return true;
        }
        return false;
    }

    /* =============== INIZIO METODI PRIVATI ======================*/
    /* Metodo che controlla la validità di un oggetto Autenticazione */
    private boolean checkAutenticazione(Autenticazione autenticazione) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        List<Account> accounts = (List<Account>) em.createQuery(
                "SELECT a "
                + "FROM Account a "
                + "WHERE a.username = '" + autenticazione.getUsername() + "' AND a.password = '" + autenticazione.getPassword() + "'",
                Account.class).getResultList();
        if (accounts.size() == 1) {
            em.close();
            return true;
        }
        em.close();
        return false;
    }

    /* Metodo che controlla se un libro è già presente nella libreria remota */
    private boolean libroGiaPresenteNellaCollezione(Libro libro) {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        List<Libro> libri = (List<Libro>) em.createQuery(
                "SELECT l "
                + "FROM Libro l "
                + "WHERE l.isbn = '" + libro.getIsbn() + "'",
                Libro.class).getResultList();
        if (libri.size() > 0) {
            em.close();
            return true;
        }
        em.close();
        return false;
    }

}
