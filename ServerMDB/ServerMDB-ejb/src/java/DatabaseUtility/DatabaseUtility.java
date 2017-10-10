package DatabaseUtility;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Stateless
public class DatabaseUtility implements DatabaseUtilityRemote {

    @Override
    public void svuotaTabellaAccount() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Account a").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void svuotaTabellaCategoria() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Categoria c").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void svuotaTabellaLibro() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Libro l").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public void svuotaTabellaCopieUtenti() {
        EntityManagerFactory emf = javax.persistence.Persistence.createEntityManagerFactory("ServerMDBPU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM CopiaUtente c").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

}
