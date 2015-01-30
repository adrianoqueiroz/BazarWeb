/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author adriano.queiroz
 */
public class Relatorio {
    private String categoria;
    private int produtosVendidos;
    private float total;

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getProdutosVendidos() {
        return produtosVendidos;
    }

    public void setProdutosVendidos(int produtosVendidos) {
        this.produtosVendidos = produtosVendidos;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
    
}
