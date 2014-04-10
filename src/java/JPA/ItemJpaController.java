/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package JPA;

import JPA.exceptions.NonexistentEntityException;
import JPA.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Item;
import model.Produto;
import model.Venda;

/**
 *
 * @author Adriano
 */
@Stateless
public class ItemJpaController implements Serializable {

    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    private EntityManagerFactory emf;
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Item item) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Produto produtoId = item.getProdutoId();
            if (produtoId != null) {
                produtoId = em.getReference(produtoId.getClass(), produtoId.getId());
                item.setProdutoId(produtoId);
            }
            Venda vendaId = item.getVendaId();
            if (vendaId != null) {
                vendaId = em.getReference(vendaId.getClass(), vendaId.getId());
                item.setVendaId(vendaId);
            }
            em.persist(item);
            if (produtoId != null) {
                produtoId.getItemCollection().add(item);
                produtoId = em.merge(produtoId);
            }
            if (vendaId != null) {
                vendaId.getItemCollection().add(item);
                vendaId = em.merge(vendaId);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Item item) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Item persistentItem = em.find(Item.class, item.getId());
            Produto produtoIdOld = persistentItem.getProdutoId();
            Produto produtoIdNew = item.getProdutoId();
            Venda vendaIdOld = persistentItem.getVendaId();
            Venda vendaIdNew = item.getVendaId();
            if (produtoIdNew != null) {
                produtoIdNew = em.getReference(produtoIdNew.getClass(), produtoIdNew.getId());
                item.setProdutoId(produtoIdNew);
            }
            if (vendaIdNew != null) {
                vendaIdNew = em.getReference(vendaIdNew.getClass(), vendaIdNew.getId());
                item.setVendaId(vendaIdNew);
            }
            item = em.merge(item);
            if (produtoIdOld != null && !produtoIdOld.equals(produtoIdNew)) {
                produtoIdOld.getItemCollection().remove(item);
                produtoIdOld = em.merge(produtoIdOld);
            }
            if (produtoIdNew != null && !produtoIdNew.equals(produtoIdOld)) {
                produtoIdNew.getItemCollection().add(item);
                produtoIdNew = em.merge(produtoIdNew);
            }
            if (vendaIdOld != null && !vendaIdOld.equals(vendaIdNew)) {
                vendaIdOld.getItemCollection().remove(item);
                vendaIdOld = em.merge(vendaIdOld);
            }
            if (vendaIdNew != null && !vendaIdNew.equals(vendaIdOld)) {
                vendaIdNew.getItemCollection().add(item);
                vendaIdNew = em.merge(vendaIdNew);
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = item.getId();
                if (findItem(id) == null) {
                    throw new NonexistentEntityException("The item with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Item item;
            try {
                item = em.getReference(Item.class, id);
                item.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The item with id " + id + " no longer exists.", enfe);
            }
            Produto produtoId = item.getProdutoId();
            if (produtoId != null) {
                produtoId.getItemCollection().remove(item);
                produtoId = em.merge(produtoId);
            }
            Venda vendaId = item.getVendaId();
            if (vendaId != null) {
                vendaId.getItemCollection().remove(item);
                vendaId = em.merge(vendaId);
            }
            em.remove(item);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Item> findItemEntities() {
        return findItemEntities(true, -1, -1);
    }

    public List<Item> findItemEntities(int maxResults, int firstResult) {
        return findItemEntities(false, maxResults, firstResult);
    }

    private List<Item> findItemEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Item.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Item findItem(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Item.class, id);
        } finally {
            em.close();
        }
    }

    public int getItemCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Item> rt = cq.from(Item.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
