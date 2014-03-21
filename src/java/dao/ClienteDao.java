package dao;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import model.Cliente;

/**
 *
 * @author Adriano
 */
public class ClienteDao {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");

    public void persist(Cliente Cliente) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(Cliente);
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    public Cliente findByCpf(String cpf) {
        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createNamedQuery("Cliente.findByCpf");
            query.setParameter("cpf", cpf);
            return (Cliente) query.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            return null;
        } finally {
            em.close();
        }
    }
}
