/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jpa.ProdutoJpaController;
import model.Produto;

/**
 *
 * @author Adriano
 */

@ManagedBean
@RequestScoped
public class ProdutoBean {
    private Produto produto = new Produto();

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
    
    public void create(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        ProdutoJpaController produtoJpa = new ProdutoJpaController(emf);
        produtoJpa.create(produto);
        emf.close();
    }
}