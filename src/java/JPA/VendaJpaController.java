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
import model.Cliente;
import model.Evento;
import model.Funcionario;
import model.Item;
import model.Venda;

/**
 *
 * @author Adriano
 */
@Stateless
public class VendaJpaController implements Serializable {

    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    private EntityManagerFactory emf;
    
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Venda venda) throws RollbackFailureException, Exception {
        if (venda.getItemCollection() == null) {
            venda.setItemCollection(new ArrayList<Item>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            Evento eventoId = venda.getEventoId();
            if (eventoId != null) {
                eventoId = em.getReference(eventoId.getClass(), eventoId.getId());
                venda.setEventoId(eventoId);
            }
            Funcionario funcionarioId = venda.getFuncionarioId();
            if (funcionarioId != null) {
                funcionarioId = em.getReference(funcionarioId.getClass(), funcionarioId.getId());
                venda.setFuncionarioId(funcionarioId);
            }
            Cliente clienteId = venda.getClienteId();
            if (clienteId != null) {
                clienteId = em.getReference(clienteId.getClass(), clienteId.getId());
                venda.setClienteId(clienteId);
            }
            Collection<Item> attachedItemCollection = new ArrayList<Item>();
            for (Item itemCollectionItemToAttach : venda.getItemCollection()) {
                itemCollectionItemToAttach = em.getReference(itemCollectionItemToAttach.getClass(), itemCollectionItemToAttach.getId());
                attachedItemCollection.add(itemCollectionItemToAttach);
            }
            venda.setItemCollection(attachedItemCollection);
            em.persist(venda);
            if (eventoId != null) {
                eventoId.getVendaCollection().add(venda);
                eventoId = em.merge(eventoId);
            }
            if (funcionarioId != null) {
                funcionarioId.getVendaCollection().add(venda);
                funcionarioId = em.merge(funcionarioId);
            }
            if (clienteId != null) {
                clienteId.getVendaCollection().add(venda);
                clienteId = em.merge(clienteId);
            }
            for (Item itemCollectionItem : venda.getItemCollection()) {
                Venda oldVendaIdOfItemCollectionItem = itemCollectionItem.getVendaId();
                itemCollectionItem.setVendaId(venda);
                itemCollectionItem = em.merge(itemCollectionItem);
                if (oldVendaIdOfItemCollectionItem != null) {
                    oldVendaIdOfItemCollectionItem.getItemCollection().remove(itemCollectionItem);
                    oldVendaIdOfItemCollectionItem = em.merge(oldVendaIdOfItemCollectionItem);
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

    public void edit(Venda venda) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Venda persistentVenda = em.find(Venda.class, venda.getId());
            Evento eventoIdOld = persistentVenda.getEventoId();
            Evento eventoIdNew = venda.getEventoId();
            Funcionario funcionarioIdOld = persistentVenda.getFuncionarioId();
            Funcionario funcionarioIdNew = venda.getFuncionarioId();
            Cliente clienteIdOld = persistentVenda.getClienteId();
            Cliente clienteIdNew = venda.getClienteId();
            Collection<Item> itemCollectionOld = persistentVenda.getItemCollection();
            Collection<Item> itemCollectionNew = venda.getItemCollection();
            List<String> illegalOrphanMessages = null;
            for (Item itemCollectionOldItem : itemCollectionOld) {
                if (!itemCollectionNew.contains(itemCollectionOldItem)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Item " + itemCollectionOldItem + " since its vendaId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (eventoIdNew != null) {
                eventoIdNew = em.getReference(eventoIdNew.getClass(), eventoIdNew.getId());
                venda.setEventoId(eventoIdNew);
            }
            if (funcionarioIdNew != null) {
                funcionarioIdNew = em.getReference(funcionarioIdNew.getClass(), funcionarioIdNew.getId());
                venda.setFuncionarioId(funcionarioIdNew);
            }
            if (clienteIdNew != null) {
                clienteIdNew = em.getReference(clienteIdNew.getClass(), clienteIdNew.getId());
                venda.setClienteId(clienteIdNew);
            }
            Collection<Item> attachedItemCollectionNew = new ArrayList<Item>();
            for (Item itemCollectionNewItemToAttach : itemCollectionNew) {
                itemCollectionNewItemToAttach = em.getReference(itemCollectionNewItemToAttach.getClass(), itemCollectionNewItemToAttach.getId());
                attachedItemCollectionNew.add(itemCollectionNewItemToAttach);
            }
            itemCollectionNew = attachedItemCollectionNew;
            venda.setItemCollection(itemCollectionNew);
            venda = em.merge(venda);
            if (eventoIdOld != null && !eventoIdOld.equals(eventoIdNew)) {
                eventoIdOld.getVendaCollection().remove(venda);
                eventoIdOld = em.merge(eventoIdOld);
            }
            if (eventoIdNew != null && !eventoIdNew.equals(eventoIdOld)) {
                eventoIdNew.getVendaCollection().add(venda);
                eventoIdNew = em.merge(eventoIdNew);
            }
            if (funcionarioIdOld != null && !funcionarioIdOld.equals(funcionarioIdNew)) {
                funcionarioIdOld.getVendaCollection().remove(venda);
                funcionarioIdOld = em.merge(funcionarioIdOld);
            }
            if (funcionarioIdNew != null && !funcionarioIdNew.equals(funcionarioIdOld)) {
                funcionarioIdNew.getVendaCollection().add(venda);
                funcionarioIdNew = em.merge(funcionarioIdNew);
            }
            if (clienteIdOld != null && !clienteIdOld.equals(clienteIdNew)) {
                clienteIdOld.getVendaCollection().remove(venda);
                clienteIdOld = em.merge(clienteIdOld);
            }
            if (clienteIdNew != null && !clienteIdNew.equals(clienteIdOld)) {
                clienteIdNew.getVendaCollection().add(venda);
                clienteIdNew = em.merge(clienteIdNew);
            }
            for (Item itemCollectionNewItem : itemCollectionNew) {
                if (!itemCollectionOld.contains(itemCollectionNewItem)) {
                    Venda oldVendaIdOfItemCollectionNewItem = itemCollectionNewItem.getVendaId();
                    itemCollectionNewItem.setVendaId(venda);
                    itemCollectionNewItem = em.merge(itemCollectionNewItem);
                    if (oldVendaIdOfItemCollectionNewItem != null && !oldVendaIdOfItemCollectionNewItem.equals(venda)) {
                        oldVendaIdOfItemCollectionNewItem.getItemCollection().remove(itemCollectionNewItem);
                        oldVendaIdOfItemCollectionNewItem = em.merge(oldVendaIdOfItemCollectionNewItem);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = venda.getId();
                if (findVenda(id) == null) {
                    throw new NonexistentEntityException("The venda with id " + id + " no longer exists.");
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
            Venda venda;
            try {
                venda = em.getReference(Venda.class, id);
                venda.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The venda with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Item> itemCollectionOrphanCheck = venda.getItemCollection();
            for (Item itemCollectionOrphanCheckItem : itemCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Venda (" + venda + ") cannot be destroyed since the Item " + itemCollectionOrphanCheckItem + " in its itemCollection field has a non-nullable vendaId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Evento eventoId = venda.getEventoId();
            if (eventoId != null) {
                eventoId.getVendaCollection().remove(venda);
                eventoId = em.merge(eventoId);
            }
            Funcionario funcionarioId = venda.getFuncionarioId();
            if (funcionarioId != null) {
                funcionarioId.getVendaCollection().remove(venda);
                funcionarioId = em.merge(funcionarioId);
            }
            Cliente clienteId = venda.getClienteId();
            if (clienteId != null) {
                clienteId.getVendaCollection().remove(venda);
                clienteId = em.merge(clienteId);
            }
            em.remove(venda);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Venda> findVendaEntities() {
        return findVendaEntities(true, -1, -1);
    }

    public List<Venda> findVendaEntities(int maxResults, int firstResult) {
        return findVendaEntities(false, maxResults, firstResult);
    }

    private List<Venda> findVendaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Venda.class));
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

    public List<Venda> findVendaEntitiesByEvento(Evento evento) {
        EntityManager em = getEntityManager();

        try {
            Query query = em.createNamedQuery("Venda.findByEvento");
            query.setParameter("eventoId", evento);
            return (List<Venda>) query.getResultList();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            return null;
        } finally {
            em.close();
        }
    }
    
    public Venda findVenda(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Venda.class, id);
        } finally {
            em.close();
        }
    }

    public int getVendaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Venda> rt = cq.from(Venda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
