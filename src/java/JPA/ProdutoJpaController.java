/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JPA;

import JPA.exceptions.IllegalOrphanException;
import JPA.exceptions.NonexistentEntityException;
import JPA.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Categoria;
import model.Evento;
import model.Item;
import model.Produto;

/**
 *
 * @author Adriano
 */
@Stateless
public class ProdutoJpaController implements Serializable {

    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    private EntityManagerFactory emf;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Produto produto) throws RollbackFailureException, Exception {
        if (produto.getItemCollection() == null) {
            produto.setItemCollection(new ArrayList<Item>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            Categoria categoriaId = produto.getCategoriaId();
            if (categoriaId != null) {
                categoriaId = em.getReference(categoriaId.getClass(), categoriaId.getId());
                produto.setCategoriaId(categoriaId);
            }
            Collection<Item> attachedItemCollection = new ArrayList<Item>();
            for (Item itemCollectionItemToAttach : produto.getItemCollection()) {
                itemCollectionItemToAttach = em.getReference(itemCollectionItemToAttach.getClass(), itemCollectionItemToAttach.getId());
                attachedItemCollection.add(itemCollectionItemToAttach);
            }
            produto.setItemCollection(attachedItemCollection);
            em.persist(produto);
            if (categoriaId != null) {
                categoriaId.getProdutoCollection().add(produto);
                categoriaId = em.merge(categoriaId);
            }
            for (Item itemCollectionItem : produto.getItemCollection()) {
                Produto oldProdutoIdOfItemCollectionItem = itemCollectionItem.getProdutoId();
                itemCollectionItem.setProdutoId(produto);
                itemCollectionItem = em.merge(itemCollectionItem);
                if (oldProdutoIdOfItemCollectionItem != null) {
                    oldProdutoIdOfItemCollectionItem.getItemCollection().remove(itemCollectionItem);
                    oldProdutoIdOfItemCollectionItem = em.merge(oldProdutoIdOfItemCollectionItem);
                }
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Produto produto) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Produto persistentProduto = em.find(Produto.class, produto.getId());
            Categoria categoriaIdOld = persistentProduto.getCategoriaId();
            Categoria categoriaIdNew = produto.getCategoriaId();
            Collection<Item> itemCollectionOld = persistentProduto.getItemCollection();
            Collection<Item> itemCollectionNew = produto.getItemCollection();
            List<String> illegalOrphanMessages = null;
            for (Item itemCollectionOldItem : itemCollectionOld) {
                if (!itemCollectionNew.contains(itemCollectionOldItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Item " + itemCollectionOldItem + " since its produtoId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (categoriaIdNew != null) {
                categoriaIdNew = em.getReference(categoriaIdNew.getClass(), categoriaIdNew.getId());
                produto.setCategoriaId(categoriaIdNew);
            }
            Collection<Item> attachedItemCollectionNew = new ArrayList<Item>();
            for (Item itemCollectionNewItemToAttach : itemCollectionNew) {
                itemCollectionNewItemToAttach = em.getReference(itemCollectionNewItemToAttach.getClass(), itemCollectionNewItemToAttach.getId());
                attachedItemCollectionNew.add(itemCollectionNewItemToAttach);
            }
            itemCollectionNew = attachedItemCollectionNew;
            produto.setItemCollection(itemCollectionNew);
            produto = em.merge(produto);
            if (categoriaIdOld != null && !categoriaIdOld.equals(categoriaIdNew)) {
                categoriaIdOld.getProdutoCollection().remove(produto);
                categoriaIdOld = em.merge(categoriaIdOld);
            }
            if (categoriaIdNew != null && !categoriaIdNew.equals(categoriaIdOld)) {
                categoriaIdNew.getProdutoCollection().add(produto);
                categoriaIdNew = em.merge(categoriaIdNew);
            }
            for (Item itemCollectionNewItem : itemCollectionNew) {
                if (!itemCollectionOld.contains(itemCollectionNewItem)) {
                    Produto oldProdutoIdOfItemCollectionNewItem = itemCollectionNewItem.getProdutoId();
                    itemCollectionNewItem.setProdutoId(produto);
                    itemCollectionNewItem = em.merge(itemCollectionNewItem);
                    if (oldProdutoIdOfItemCollectionNewItem != null && !oldProdutoIdOfItemCollectionNewItem.equals(produto)) {
                        oldProdutoIdOfItemCollectionNewItem.getItemCollection().remove(itemCollectionNewItem);
                        oldProdutoIdOfItemCollectionNewItem = em.merge(oldProdutoIdOfItemCollectionNewItem);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = produto.getId();
                if (findProduto(id) == null) {
                    throw new NonexistentEntityException("The produto with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Produto produto;
            try {
                produto = em.getReference(Produto.class, id);
                produto.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The produto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Item> itemCollectionOrphanCheck = produto.getItemCollection();
            for (Item itemCollectionOrphanCheckItem : itemCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Produto (" + produto + ") cannot be destroyed since the Item " + itemCollectionOrphanCheckItem + " in its itemCollection field has a non-nullable produtoId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Categoria categoriaId = produto.getCategoriaId();
            if (categoriaId != null) {
                categoriaId.getProdutoCollection().remove(produto);
                categoriaId = em.merge(categoriaId);
            }
            em.remove(produto);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Produto> findProdutoEntities() {
        return findProdutoEntities(true, -1, -1);
    }

    public List<Produto> findProdutoEntities(int maxResults, int firstResult) {
        return findProdutoEntities(false, maxResults, firstResult);
    }

    private List<Produto> findProdutoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Produto.class));
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

    public List<Produto> findProdutoEntitiesByEvento(Evento evento) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Produto.findByEvento");
            query.setParameter("eventoId", evento);
            return (List<Produto>) query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            return null;
        } finally {
            em.close();
        }
    }
        public List<Produto> findProdutoEntitiesByCategoria(Categoria categoria) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Produto.findByCategoria");
            query.setParameter("categoriaId", categoria);
            return (List<Produto>) query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            return null;
        } finally {
            em.close();
        }
    }
        
    public Produto findProduto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    public Produto findByCodigo(Integer codigo) {
        EntityManager em = getEntityManager();

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

    public Produto findByCodigoAndEvento(Integer codigo, Evento evento) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Produto.findByCodigoAndEvento");
            query.setParameter("codigo", codigo);
            query.setParameter("eventoId", evento);
            return (Produto) query.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            return null;
        } finally {
            em.close();
        }
    }
    
    public int getProdutoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Produto> rt = cq.from(Produto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
