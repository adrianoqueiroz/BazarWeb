/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JPA;

import JPA.exceptions.IllegalOrphanException;
import JPA.exceptions.NonexistentEntityException;
import JPA.exceptions.PreexistingEntityException;
import JPA.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Perfil;
import model.Venda;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import model.Funcionario;

/**
 *
 * @author Adriano
 */
public class FuncionarioJpaController implements Serializable {

    public FuncionarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Funcionario funcionario) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (funcionario.getVendaCollection() == null) {
            funcionario.setVendaCollection(new ArrayList<Venda>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Perfil grupoId = funcionario.getPerfilId();
            if (grupoId != null) {
                grupoId = em.getReference(grupoId.getClass(), grupoId.getId());
                funcionario.setPerfilId(grupoId);
            }
            Collection<Venda> attachedVendaCollection = new ArrayList<Venda>();
            for (Venda vendaCollectionVendaToAttach : funcionario.getVendaCollection()) {
                vendaCollectionVendaToAttach = em.getReference(vendaCollectionVendaToAttach.getClass(), vendaCollectionVendaToAttach.getId());
                attachedVendaCollection.add(vendaCollectionVendaToAttach);
            }
            funcionario.setVendaCollection(attachedVendaCollection);
            em.persist(funcionario);
            if (grupoId != null) {
                grupoId.getFuncionarioCollection().add(funcionario);
                grupoId = em.merge(grupoId);
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
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findFuncionario(funcionario.getId()) != null) {
                throw new PreexistingEntityException("Funcionario " + funcionario + " already exists.", ex);
            }
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
            utx.begin();
            em = getEntityManager();
            Funcionario persistentFuncionario = em.find(Funcionario.class, funcionario.getId());
            Perfil grupoIdOld = persistentFuncionario.getPerfilId();
            Perfil grupoIdNew = funcionario.getPerfilId();
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
            if (grupoIdNew != null) {
                grupoIdNew = em.getReference(grupoIdNew.getClass(), grupoIdNew.getId());
                funcionario.setPerfilId(grupoIdNew);
            }
            Collection<Venda> attachedVendaCollectionNew = new ArrayList<Venda>();
            for (Venda vendaCollectionNewVendaToAttach : vendaCollectionNew) {
                vendaCollectionNewVendaToAttach = em.getReference(vendaCollectionNewVendaToAttach.getClass(), vendaCollectionNewVendaToAttach.getId());
                attachedVendaCollectionNew.add(vendaCollectionNewVendaToAttach);
            }
            vendaCollectionNew = attachedVendaCollectionNew;
            funcionario.setVendaCollection(vendaCollectionNew);
            funcionario = em.merge(funcionario);
            if (grupoIdOld != null && !grupoIdOld.equals(grupoIdNew)) {
                grupoIdOld.getFuncionarioCollection().remove(funcionario);
                grupoIdOld = em.merge(grupoIdOld);
            }
            if (grupoIdNew != null && !grupoIdNew.equals(grupoIdOld)) {
                grupoIdNew.getFuncionarioCollection().add(funcionario);
                grupoIdNew = em.merge(grupoIdNew);
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
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
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
            utx.begin();
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
            Perfil grupoId = funcionario.getPerfilId();
            if (grupoId != null) {
                grupoId.getFuncionarioCollection().remove(funcionario);
                grupoId = em.merge(grupoId);
            }
            em.remove(funcionario);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
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
