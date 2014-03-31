/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.ProdutoJpaController;
import dao.ProdutoDao;
import java.util.ArrayList;
import java.util.Collection;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import model.Produto;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class ProdutoBean {

    private Produto produto;
    private Collection<Produto> produtoCollection;
    private int estoque;
    
    public ProdutoBean() {
        this.produtoCollection = new ArrayList<>();
        this.produto = new Produto();
    }

    public Collection<Produto> getProdutoCollection() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("BazarWebPU");
        ProdutoJpaController produtoJpaController;
        produtoJpaController = new ProdutoJpaController(emf);

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
        ProdutoDao produtoDao = new ProdutoDao();
        if(produtoDao.persist(produto)){
            produto = new Produto();
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
