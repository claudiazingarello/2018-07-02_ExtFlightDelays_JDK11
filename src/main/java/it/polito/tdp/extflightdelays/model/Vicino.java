package it.polito.tdp.extflightdelays.model;

public class Vicino implements Comparable<Vicino>{
	
	private Airport a;
	private Double peso;
	
	public Vicino(Airport a, Double peso) {
		super();
		this.a = a;
		this.peso = peso;
	}
	public Airport getA() {
		return a;
	}
	public void setA(Airport a) {
		this.a = a;
	}
	public Double getPeso() {
		return peso;
	}
	public void setPeso(Double peso) {
		this.peso = peso;
	}
	@Override
	public int compareTo(Vicino o) {
		return -(this.peso.compareTo(o.getPeso()));
	}
	@Override
	public String toString() {
		return a + " (DISTANZA: " + peso + ")";
	}
	

}
