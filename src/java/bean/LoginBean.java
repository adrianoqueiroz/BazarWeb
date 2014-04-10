package bean;

import JPA.EventoJpaController;
import JPA.FuncionarioJpaController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import model.Evento;
import model.Funcionario;

/**
 *
 * @author Adriano
 */

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
    private Collection<Evento> eventos;
    private Evento eventoSelecionado = new Evento();
    private Funcionario funcionarioLogado = new Funcionario();
    private String usuario;
    private String senha;
    private Integer idEventoSelecionado;
    @EJB
    private FuncionarioJpaController funcionarioJpaController;
    @EJB
    private EventoJpaController eventoJpaController;

    public Integer getIdEventoSelecionado() {
        return idEventoSelecionado;
    }

    public void setIdEventoSelecionado(Integer idEventoSelecionado) {
        this.idEventoSelecionado = idEventoSelecionado;
    }

    public Evento getEventoSelecionado() {
        return eventoSelecionado;
    }

    public void setEventoSelecionado(Evento eventoSelecionado) {
        this.eventoSelecionado = eventoSelecionado;
    }

    public Funcionario getFuncionarioLogado() {
        return funcionarioLogado;
    }

    public void setFuncionarioLogado(Funcionario funcionarioLogado) {
        this.funcionarioLogado = funcionarioLogado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void efetuaLogin() {
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        String url = "index.xhtml";

        try {
            funcionarioLogado = funcionarioJpaController.findByLogin(usuario, senha);
            
            
            eventoSelecionado = eventoJpaController.findEvento(idEventoSelecionado);

            if (funcionarioLogado != null && eventoSelecionado != null) {
                if (eventoSelecionado.getIsActive()) {
                    context.addMessage(null, new FacesMessage("Login Efetuado!", funcionarioLogado.getNome() + ", " + eventoSelecionado.getNome()));
                    externalContext.redirect(url);
                }
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage("Erro no Login", "Usuário e/ou senha incorreto!"));
        }
    }

    public List<SelectItem> getSelectItemEventos() {
        List<Evento> listaEventos = eventoJpaController.findEventoEntities();
        List<SelectItem> itens = new ArrayList<>(listaEventos.size());

        for (Evento e : listaEventos) {
            itens.add(new SelectItem(e.getId(), e.getNome()));

        }
        return itens;
    }

    public Collection<Evento> getEventos() {
        eventos = eventoJpaController.findEventoEntities();
        return eventos;
    }

    public void setEventos(Collection<Evento> eventos) {
        this.eventos = eventos;
    }
}
