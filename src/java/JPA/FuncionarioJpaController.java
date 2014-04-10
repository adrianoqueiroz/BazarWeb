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
import model.Funcionario;
import model.Perfil;
import model.Venda;

/**
 *
 * @author Adriano
 */
@Stateless
public class FuncionarioJpaController implements Serializable {

    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    private EntityManagerFactory emf;
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Funcionario funcionario) throws RollbackFailureException, Exception {
        if (funcionario.getVendaCollection() == null) {
            funcionario.setVendaCollection(new ArrayList<Venda>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            Perfil perfilId = funcionario.getPerfilId();
            if (perfilId != null) {
                perfilId = em.getReference(perfilId.getClass(), perfilId.getId());
                funcionario.setPerfilId(perfilId);
            }
            Collection<Venda> attachedVendaCollection = new ArrayList<Venda>();
            for (Venda vendaCollectionVendaToAttach : funcionario.getVendaCollection()) {
                vendaCollectionVendaToAttach = em.getReference(vendaCollectionVendaToAttach.getClass(), vendaCollectionVendaToAttach.getId());
                attachedVendaCollection.add(vendaCollectionVendaToAttach);
            }
            funcionario.setVendaCollection(attachedVendaCollection);
            em.persist(funcionario);
            if (perfilId != null) {
                perfilId.getFuncionarioCollection().add(funcionario);
                perfilId = em.merge(perfilId);
            }
            for (Venda vendaCollectionVenda : funcionario.getVendaCollection()) {
                Funcionario oldFuncionarioIdOfVendaCollectionVenda = vendaCollectionVenda.getFuncionarioId();
                vendaCollectionVenda.setFuncionarioId(funcionario);
                vendaCollectionVenda = em.merge(vendaCollectionVenda);
                if (oldFuncionarioIdOfVendaCollectionVenda != null) {
                    oldFuncionarioIdOfVendaCollectionVenda.getVendaCollection().remove(vendaCollectionVenda);
                    oldFuncionarioIdOfVendaCollectionVenda = em.merge(oldFuncionarioIdOfVendaCollectionVenda);
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

    public void edit(Funcionario funcionario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            Funcionario persistentFuncionario = em.find(Funcionario.class, funcionario.getId());
            Perfil perfilIdOld = persistentFuncionario.getPerfilId();
            Perfil perfilIdNew = funcionario.getPerfilId();
            Collection<Venda> vendaCollectionOld = persistentFuncionario.getVendaCollection();
            Collection<Venda> vendaCollectionNew = funcionario.getVendaCollection();
            List<String> illegalOrphanMessages = null;
            for (Venda vendaCollectionOldVenda : vendaCollectionOld) {
                if (!vendaCollectionNew.contains(vendaCollectionOldVenda)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Venda " + vendaCollectionOldVenda + " since its funcionarioId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (perfilIdNew != null) {
                perfilIdNew = em.getReference(perfilIdNew.getClass(), perfilIdNew.getId());
                funcionario.setPerfilId(perfilIdNew);
            }
            Collection<Venda> attachedVendaCollectionNew = new ArrayList<Venda>();
            for (Venda vendaCollectionNewVendaToAttach : vendaCollectionNew) {
                vendaCollectionNewVendaToAttach = em.getReference(vendaCollectionNewVendaToAttach.getClass(), vendaCollectionNewVendaToAttach.getId());
                attachedVendaCollectionNew.add(vendaCollectionNewVendaToAttach);
            }
            vendaCollectionNew = attachedVendaCollectionNew;
            funcionario.setVendaCollection(vendaCollectionNew);
            funcionario = em.merge(funcionario);
            if (perfilIdOld != null && !perfilIdOld.equals(perfilIdNew)) {
                perfilIdOld.getFuncionarioCollection().remove(funcionario);
                perfilIdOld = em.merge(perfilIdOld);
            }
            if (perfilIdNew != null && !perfilIdNew.equals(perfilIdOld)) {
                perfilIdNew.getFuncionarioCollection().add(funcionario);
                perfilIdNew = em.merge(perfilIdNew);
            }
            for (Venda vendaCollectionNewVenda : vendaCollectionNew) {
                if (!vendaCollectionOld.contains(vendaCollectionNewVenda)) {
                    Funcionario oldFuncionarioIdOfVendaCollectionNewVenda = vendaCollectionNewVenda.getFuncionarioId();
                    vendaCollectionNewVenda.setFuncionarioId(funcionario);
                    vendaCollectionNewVenda = em.merge(vendaCollectionNewVenda);
                    if (oldFuncionarioIdOfVendaCollectionNewVenda != null && !oldFuncionarioIdOfVendaCollectionNewVenda.equals(funcionario)) {
                        oldFuncionarioIdOfVendaCollectionNewVenda.getVendaCollection().remove(vendaCollectionNewVenda);
                        oldFuncionarioIdOfVendaCollectionNewVenda = em.merge(oldFuncionarioIdOfVendaCollectionNewVenda);
                    }
                }
            }
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = funcionario.getId();
                if (findFuncionario(id) == null) {
                    throw new NonexistentEntityException("The funcionario with id " + id + " no longer exists.");
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
            Funcionario funcionario;
            try {
                funcionario = em.getReference(Funcionario.class, id);
                funcionario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The funcionario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Venda> vendaCollectionOrphanCheck = funcionario.getVendaCollection();
            for (Venda vendaCollectionOrphanCheckVenda : vendaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Funcionario (" + funcionario + ") cannot be destroyed since the Venda " + vendaCollectionOrphanCheckVenda + " in its vendaCollection field has a non-nullable funcionarioId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Perfil perfilId = funcionario.getPerfilId();
            if (perfilId != null) {
                perfilId.getFuncionarioCollection().remove(funcionario);
                perfilId = em.merge(perfilId);
            }
            em.remove(funcionario);
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Funcionario> findFuncionarioEntities() {
        return findFuncionarioEntities(true, -1, -1);
    }

    public List<Funcionario> findFuncionarioEntities(int maxResults, int firstResult) {
        return findFuncionarioEntities(false, maxResults, firstResult);
    }

    private List<Funcionario> findFuncionarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Funcionario.class));
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

    public Funcionario findFuncionario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Funcionario.class, id);
        } finally {
            em.close();
        }
    }

        public Funcionario findByLogin(String usuario, String senha) {
        EntityManager em = getEntityManager();

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
        
    public int getFuncionarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Funcionario> rt = cq.from(Funcionario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
