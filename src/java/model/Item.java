package model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Adriano
 */
@Entity
@Table(name = "item")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Item.findAll", query = "SELECT i FROM Item i"),
    @NamedQuery(name = "Item.findById", query = "SELECT i FROM Item i WHERE i.id = :id"),
    @NamedQuery(name = "Item.findByProduto", query = "SELECT i FROM Item i WHERE i.produtoId = :produtoId"),
    @NamedQuery(name = "Item.findByEventoWithVendaPaga", query = "SELECT i FROM Item i WHERE i.vendaId.eventoId = :eventoId AND i.vendaId.pago = TRUE"),
    @NamedQuery(name = "Item.findItemEventoCategoriaPago", query = "SELECT i FROM Item i WHERE i.vendaId.eventoId = :eventoId AND i.produtoId.categoriaId = :categoriaId AND i.vendaId.pago = TRUE"),
    @NamedQuery(name = "Item.findEventoCategoriaPeriodoPago", query = "SELECT i FROM Item i WHERE i.vendaId.eventoId = :eventoId AND i.vendaId.pago = TRUE AND i.produtoId.categoriaId = :categoriaId AND i.vendaId.dataVenda BETWEEN :dataInicial AND :dataFinal"),
    @NamedQuery(name = "Item.findByQuantidade", query = "SELECT i FROM Item i WHERE i.quantidade = :quantidade"),
    @NamedQuery(name = "Item.findByDesconto", query = "SELECT i FROM Item i WHERE i.desconto = :desconto"),
    @NamedQuery(name = "Item.findByPrecoVenda", query = "SELECT i FROM Item i WHERE i.precoVenda = :precoVenda")})
public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "quantidade")
    private int quantidade;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "desconto")
    private Float desconto;
    @Basic(optional = false)
    @Column(name = "preco_venda")
    private float precoVenda;
    @JoinColumn(name = "produto_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Produto produtoId;
    @JoinColumn(name = "venda_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Venda vendaId;
    private Float total;
    
    public Item() {
    }

    public Item(Integer id) {
        this.id = id;
    }

    public Item(Integer id, int quantidade, float precoCompra) {
        this.id = id;
        this.quantidade = quantidade;
        this.precoVenda = precoCompra;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Float getDesconto() {
        return desconto;
    }

    public void setDesconto(Float desconto) {
        this.desconto = desconto;
    }

    public float getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(float precoVenda) {
        this.precoVenda = precoVenda;
    }

    public Produto getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Produto produtoId) {
        this.produtoId = produtoId;
    }

    public Venda getVendaId() {
        return vendaId;
    }

    public void setVendaId(Venda vendaId) {
        this.vendaId = vendaId;
    }

    public Float getTotal() {
        total = getQuantidade() * getPrecoVenda();
        return total;
    }

    public void getTotal(Float total) {
        this.total = total;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.id);
        hash = 11 * hash + this.quantidade;
        hash = 11 * hash + Objects.hashCode(this.desconto);
        hash = 11 * hash + Float.floatToIntBits(this.precoVenda);
        hash = 11 * hash + Objects.hashCode(this.produtoId);
        hash = 11 * hash + Objects.hashCode(this.vendaId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.quantidade != other.quantidade) {
            return false;
        }
        if (!Objects.equals(this.desconto, other.desconto)) {
            return false;
        }
        if (Float.floatToIntBits(this.precoVenda) != Float.floatToIntBits(other.precoVenda)) {
            return false;
        }
        if (!Objects.equals(this.produtoId, other.produtoId)) {
            return false;
        }
        if (!Objects.equals(this.vendaId, other.vendaId)) {
            return false;
        }
        return true;
    }

     
    @Override
    public String toString() {
        return "model.Item[ id=" + id + " ]";
    }
    
}
