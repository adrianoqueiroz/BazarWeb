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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.FuncionarioJpaController;
import model.Funcionario;

/**
 *
 * @author Adriano
 */
@ManagedBean
@SessionScoped
public class FuncionarioBean {
    Funcionario funcionarioLogado;

    private String cpfProcurado;

    private Funcionario novoFuncionario = new Funcionario();

    public Funcionario getFuncionario() {
        return novoFuncionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.novoFuncionario = funcionario;
    }

    public void create() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        FuncionarioJpaController funcionarioJpa = new FuncionarioJpaController(emf);
        funcionarioJpa.create(novoFuncionario);
        emf.close();
    }

    public Funcionario findByCpf() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        FuncionarioJpaController funcionarioJpa = new FuncionarioJpaController(emf);
        emf.close();
        return funcionarioJpa.findFuncionarioByCpf(cpfProcurado);
    }
}
