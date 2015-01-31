/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import JPA.VendaJpaController;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import model.Venda;

/**
 *
 * @author Adriano
 */
@ManagedBean
@RequestScoped
public class PagamentoBean implements Serializable{
    @EJB
    VendaJpaController vendaJpaController;

    public String confirmarPagamento(Venda venda) {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            venda.setPago(Boolean.TRUE);

            vendaJpaController.edit(venda);
            context.addMessage(null, new FacesMessage("Pagamento Confirmado", venda.getClienteId().getNome()));
            return "/recibo_venda.xhtml";
        } catch (Exception ex) {
            Logger.getLogger(ReciboBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "/listarvendas.xhtml";
    }
}
