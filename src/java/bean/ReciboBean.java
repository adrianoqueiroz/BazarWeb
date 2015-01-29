/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.VendaJpaController;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import model.Item;
import model.Venda;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class ReciboBean implements Serializable {
    private Venda venda;
    
    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }
    
    public float calculaValorCompra() {
        int valor = 0;
        for (Item i : this.getVenda().getItemCollection()) {
            valor += i.getPrecoVenda() * i.getQuantidade();
        }
        return valor;
    }    

    public String selecionaRecibo(Venda venda){
        this.venda = venda;
        return "/recibo_venda.xhtml";
    }
}
