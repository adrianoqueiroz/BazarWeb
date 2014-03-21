/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import dao.FuncionarioDao;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import model.Funcionario;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class FuncionarioBean {
    private Funcionario novoFuncionario = new Funcionario();

    public Funcionario getFuncionario() {
        return novoFuncionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.novoFuncionario = funcionario;
    }

    public void create() {
        FuncionarioDao funcionarioDao = new FuncionarioDao();
        funcionarioDao.persist(novoFuncionario);
    }
}
