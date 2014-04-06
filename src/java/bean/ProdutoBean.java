/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.ProdutoJpaController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;
import model.Produto;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class ProdutoBean {

    @PersistenceUnit(unitName = "BazarWebPU") //inject from your application server
    EntityManagerFactory emf;
    @Resource //inject from your application server
    UserTransaction utx;

    private Produto produto;
    private Collection<Produto> produtoCollection;
    private int estoque;

    public ProdutoBean() {
        this.produtoCollection = new ArrayList<>();
        this.produto = new Produto();
    }

    public Collection<Produto> getProdutoCollection() {
        ProdutoJpaController produtoJpaController;
        produtoJpaController = new ProdutoJpaController(utx, emf);

        produtoCollection = produtoJpaController.findProdutoEntities();
        return produtoCollection;
    }

    public void setProdutoCollection(Collection<Produto> produtoCollection) {
        this.produtoCollection = produtoCollection;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public void create() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            ProdutoJpaController produtoJpaController = new ProdutoJpaController(utx, emf);
            produtoJpaController.create(produto);
            produto = new Produto();
            context.addMessage(null, new FacesMessage("Produto cadastrado!", produto.getNome()));

        } catch (Exception ex) {
            context.addMessage(null, new FacesMessage("Falha!", "Falha"));
            Logger.getLogger(ProdutoBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getEstoque(Produto produto) {
        //TODO: fazer a verificação em vendas.itemcollection de quantos produtos foram vendidos
        estoque = produto.getQuantidade();
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

}
