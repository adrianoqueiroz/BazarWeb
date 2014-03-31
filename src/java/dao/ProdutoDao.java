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
import model.Produto;

/**
 *
 * @author Adriano
 */
public class ProdutoDao {

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");

    public boolean persist(Produto produto) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(produto);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public Produto findByCodigo(Integer codigo) {
        EntityManager em = emf.createEntityManager();

        try {
            Query query = em.createNamedQuery("Produto.findByCodigo");
            query.setParameter("codigo", codigo);
            return (Produto) query.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            return null;
        } finally {
            em.close();
        }
    }
}
