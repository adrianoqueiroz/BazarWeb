/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bean;

import dao.ProdutoDao;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
       ProdutoDao produtoDao = new ProdutoDao();
       produtoDao.persist(produto);
    }
}