/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.VendaJpaController;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import model.Item;
import model.Venda;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class ReciboBean implements Serializable {

    @EJB
    private VendaJpaController vendaJpaController;

    private Integer idProcurado;

    private Venda venda;

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Integer getIdProcurado() {
        return idProcurado;
    }

    public void setIdProcurado(Integer idProcurado) {
        this.idProcurado = idProcurado;
    }

    public float calculaValorCompra() {
        int valor = 0;
        for (Item i : this.getVenda().getItemCollection()) {
            valor += i.getPrecoVenda() * i.getQuantidade();
        }
        return valor;
    }

    public String selecionaRecibo(Venda venda) {
        this.venda = venda;
        return "/recibo_venda.xhtml";
    }

    public String selecionaRecibo() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            this.venda = vendaJpaController.findVenda(idProcurado);
            if (venda.getId() != null) {
                context.addMessage(null, new FacesMessage("Recibo de venda", venda.getId().toString()));
                return "/recibo_venda.xhtml";
            } else {
                idProcurado = null;
                context.addMessage(null, new FacesMessage("Falha", "Venda não encontrada!"));
            }

        } catch (Exception e) {
        }
        idProcurado = null;
        context.addMessage(null, new FacesMessage("Falha", "Venda não encontrada!"));
        return "/listarvendas.xhtml";
    }
}
