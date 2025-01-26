package org.example.scrapper;

public class DadosRaspadosSobreMercado {

	private final String rotulo;
	private final String value;
	private final String sigla;

	public DadosRaspadosSobreMercado(String rotulo, String value, String sigla) {
		this.rotulo = rotulo;
		this.value = value;
		this.sigla = sigla;
	}

	public Object getSigla() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getSigla'");
	}

	public char[] getValue() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getValue'");
	}

	public String rotulo() {
		return this.rotulo;
	}

	public String value() {
		return this.value;
	}

	public String sigla() {
		return this.sigla;
	}

	@Override
	public String toString() {
		return "DadosRaspadosSobreMercado{rotulo=" + rotulo + ", value=" + value + ", sigla=" + sigla + "}";
	}
}
