package it.polito.tdp.extflightdelays.model;

public class AirportDuration implements Comparable<AirportDuration>{
	private Airport a;
	private Double durata;
	public AirportDuration(Airport a, Double durata) {
		super();
		this.a = a;
		this.durata = durata;
	}
	public Airport getA() {
		return a;
	}
	public void setA(Airport a) {
		this.a = a;
	}
	public Double getDurata() {
		return durata;
	}
	public void setDurata(Double durata) {
		this.durata = durata;
	}
	
	@Override
	public int compareTo(AirportDuration o) {
		return this.durata.compareTo(o.getDurata());
	}
	
	

}
