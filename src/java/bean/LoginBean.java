/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import dao.FuncionarioDao;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.Evento;
import model.Funcionario;

/**
 *
 * @author Adriano
 */
@ManagedBean
@SessionScoped
public class LoginBean {
    private Evento eventoSelecionado = new Evento();
    private Funcionario funcionarioLogado = new Funcionario();
    private String usuario;
    private String senha;

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
        FuncionarioDao funcionarioDao = new FuncionarioDao();
        try {
            Funcionario funcionarioEncontrado = funcionarioDao.findByLogin(usuario, senha);

            if (funcionarioEncontrado != null) {
                funcionarioLogado = funcionarioEncontrado;
                context.addMessage(null, new FacesMessage("Funcionario Logado", funcionarioLogado.getNome()));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage("Erro no Login","Usuário e/ou senha incorreto!"));
        }
    }

}
