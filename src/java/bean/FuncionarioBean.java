package bean;

import JPA.FuncionarioJpaController;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import model.Funcionario;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class FuncionarioBean {

    @PersistenceUnit(unitName = "BazarWebPU")
    EntityManagerFactory emf;
    @Resource
    UserTransaction utx;

    private Funcionario novoFuncionario = new Funcionario();

    public Funcionario getFuncionario() {
        return novoFuncionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.novoFuncionario = funcionario;
    }

    public void create() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            FuncionarioJpaController funcionarioJpaController = new FuncionarioJpaController(utx, emf);
            funcionarioJpaController.create(novoFuncionario);
            context.addMessage(null, new FacesMessage("Cadastro Efetuado!", "Funcionário " + novoFuncionario.getNome()));
        } catch (Exception ex) {
            Logger.getLogger(FuncionarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
