/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import model.Funcionario;

/**
 *
 * @author Adriano
 */
public class FuncionarioDao {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");

    public void persist(Funcionario funcionario) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(funcionario);
            em.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }
    
        public Funcionario findByLogin(String usuario, String senha) {
        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createNamedQuery("Funcionario.findByLogin");
            query.setParameter("usuario", usuario);
            query.setParameter("senha", senha);
            return (Funcionario) query.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            return null;
        } finally {
            em.close();
        }
    }
    
}
