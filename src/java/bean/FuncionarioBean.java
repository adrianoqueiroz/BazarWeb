package bean;

import JPA.FuncionarioJpaController;
import JPA.PerfilJpaController;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import model.Funcionario;
import model.Perfil;

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
    private Integer idPerfilSelecionado;

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
            PerfilJpaController perfilJpaController = new PerfilJpaController(utx, emf);
            Perfil perfilSelecionado = perfilJpaController.findPerfil(idPerfilSelecionado);
            novoFuncionario.setPerfilId(perfilSelecionado);
            
            FuncionarioJpaController funcionarioJpaController = new FuncionarioJpaController(utx, emf);
            funcionarioJpaController.create(novoFuncionario);
            
            context.addMessage(null, new FacesMessage("Cadastro Efetuado!", "Funcionário " + novoFuncionario.getNome()));
        } catch (Exception ex) {
            Logger.getLogger(FuncionarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Integer getIdPerfilSelecionado() {
        return idPerfilSelecionado;
    }

    public void setIdPerfilSelecionado(Integer idPerfilSelecionado) {
        this.idPerfilSelecionado = idPerfilSelecionado;
    }

    public List<SelectItem> getSelectItemPerfis() {
        PerfilJpaController perfilJpaController = new PerfilJpaController((UserTransaction) utx, emf);

        List<Perfil> listaPerfis = perfilJpaController.findPerfilEntities();
        List<SelectItem> itens = new ArrayList<>(listaPerfis.size());

        for (Perfil p : listaPerfis) {
            itens.add(new SelectItem(p.getId(), p.getNome()));

        }
        return itens;
    }
}
